package com.lycoris.loconautics.content.steelcable;

import com.lycoris.loconautics.Config;
import com.lycoris.loconautics.mixin.RopeStrandHolderBehaviorAccessor;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.simulated_team.simulated.content.blocks.rope.RopeStrandHolderBehavior;
import dev.simulated_team.simulated.content.blocks.rope.rope_winch.RopeWinchBlockEntity;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.RopeAttachment;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.RopeAttachmentPoint;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.ServerLevelRopeManager;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.ServerRopeStrand;
import dev.simulated_team.simulated.data.advancements.SimAdvancements;
import dev.simulated_team.simulated.service.SimConfigService;
import dev.simulated_team.simulated.index.SimDataComponents;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;
import com.lycoris.loconautics.network.packets.SteelCableStrandPacket;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Steel Cable — a long-range rope item.
 *
 * Reimplements strand creation from {@code RopeStrandHolderBehavior.createRope()} verbatim,
 * but substitutes a configurable range for Simulated's {@code maxRopeRange}.
 * When {@link Config#STEEL_CABLE_MAX_RANGE} is {@code -1} (default), the effective range is
 * {@code 2 * Simulated's current maxRopeRange}. Any positive value overrides the range directly.
 *
 * Private ownership fields on {@link RopeStrandHolderBehavior} are written through
 * {@link RopeStrandHolderBehaviorAccessor}.
 */
public class SteelCableItem extends Item {

    public SteelCableItem(Properties properties) {
        super(properties);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    public static boolean isValidAttachment(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof SmartBlockEntity sbe) {
            RopeStrandHolderBehavior behavior = sbe.getBehaviour(RopeStrandHolderBehavior.TYPE);
            return behavior != null && !behavior.isAttached();
        }
        return false;
    }

    public static RopeStrandHolderBehavior getRopeHolder(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof SmartBlockEntity sbe) {
            return sbe.getBehaviour(RopeStrandHolderBehavior.TYPE);
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Item interaction
    // -------------------------------------------------------------------------

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos clickedPos = context.getClickedPos();
        Level level = context.getLevel();
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();

        if (player != null && player.isShiftKeyDown()) {
            stack.remove(SimDataComponents.ROPE_FIRST_CONNECTION);
            return InteractionResult.SUCCESS;
        }

        if (isValidAttachment(level, clickedPos)) {
            if (stack.has(SimDataComponents.ROPE_FIRST_CONNECTION)) {
                if (!level.isClientSide) {
                    BlockPos firstPos = stack.get(SimDataComponents.ROPE_FIRST_CONNECTION);
                    if (!createSteelCableStrand(level, firstPos, clickedPos)) {
                        stack.remove(SimDataComponents.ROPE_FIRST_CONNECTION);
                        return InteractionResult.SUCCESS;
                    }
                    SimAdvancements.LEARNING_THE_ROPES.awardTo(player);
                }
                stack.remove(SimDataComponents.ROPE_FIRST_CONNECTION);
                if (!player.isCreative()) {
                    context.getItemInHand().shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
            stack.set(SimDataComponents.ROPE_FIRST_CONNECTION, clickedPos);
            return InteractionResult.SUCCESS;
        }

        return super.useOn(context);
    }

    // -------------------------------------------------------------------------
    // Strand creation — mirrors RopeStrandHolderBehavior.createRope() exactly,
    // except the range guard uses Config.STEEL_CABLE_MAX_RANGE.
    // -------------------------------------------------------------------------

    private boolean createSteelCableStrand(Level level, BlockPos posA, BlockPos posB) {
        RopeStrandHolderBehavior holderA = getRopeHolder(level, posA);
        if (holderA == null) return false;
        RopeStrandHolderBehavior holderB = getRopeHolder(level, posB);
        if (holderB == null) return false;

        // Winch must always be holderA (same rule as Simulated's RopeItem)
        if (holderB.blockEntity instanceof RopeWinchBlockEntity
                && !(holderA.blockEntity instanceof RopeWinchBlockEntity)) {
            RopeStrandHolderBehavior tmp = holderA;
            holderA = holderB;
            holderB = tmp;
        }
        if (holderA.blockEntity instanceof RopeWinchBlockEntity
                && holderB.blockEntity instanceof RopeWinchBlockEntity) {
            return false;
        }

        // Mirrors the guards inside createRope()
        if (holderA == holderB) return false;
        if (holderB.isAttached()) return false;

        // Project endpoints out of any contraption sub-levels
        Vec3 localRopeStart  = holderA.getAttachmentPoint();
        Vec3 localRopeTarget = holderB.getAttachmentPoint();
        SubLevel subLevelStart  = Sable.HELPER.getContaining(level, (Position) localRopeStart);
        SubLevel subLevelTarget = Sable.HELPER.getContaining(level, (Position) localRopeTarget);
        Vec3 ropeStart  = Sable.HELPER.projectOutOfSubLevel(level, localRopeStart);
        Vec3 ropeTarget = Sable.HELPER.projectOutOfSubLevel(level, localRopeTarget);

        // --- The only deviation from createRope(): extended range via our config ---
        // -1 (default) = 2x Simulated's current maxRopeRange; any positive value = direct override.
        double configuredRange = Config.STEEL_CABLE_MAX_RANGE.get();
        double simRange = (double) SimConfigService.INSTANCE.server().blocks.maxRopeRange.get();
        double maxRange = (configuredRange < 0) ? simRange * 2.0 : configuredRange;
        if (!ropeTarget.closerThan((Position) ropeStart, maxRange)) {
            return false;
        }

        // Destroy any pre-existing strand on holderA before creating a new one
        holderA.destroyRope(null, null);

        // Build physics point list spaced ~1 block apart along the strand
        double distance = ropeTarget.distanceTo(ropeStart);
        int oneLongSegments = Mth.floor(distance);
        int points = Math.max(1, oneLongSegments + 1);
        Vec3 diff = ropeTarget.subtract(ropeStart).normalize();
        ObjectArrayList<Vector3d> pointList = new ObjectArrayList<>();
        pointList.add(JOMLConversion.toJOML((Position) ropeStart));
        double shortSegmentLength = distance - (double) oneLongSegments;
        for (int i = 0; i < points; ++i) {
            pointList.add(JOMLConversion.toJOML((Position) ropeStart.add(diff.scale((double) i + shortSegmentLength))));
        }

        // Construct the strand and register its attachments
        ServerRopeStrand strand = new ServerRopeStrand(UUID.randomUUID(), (Collection<Vector3d>) pointList);
        strand.updateFirstSegmentExtension(shortSegmentLength);

        ServerLevel serverLevel = (ServerLevel) level;
        strand.addAttachment(serverLevel, RopeAttachmentPoint.START,
                new RopeAttachment(RopeAttachmentPoint.START,
                        Optional.ofNullable(subLevelStart).map(SubLevel::getUniqueId).orElse(null),
                        holderA.blockEntity.getBlockPos()));
        strand.addAttachment(serverLevel, RopeAttachmentPoint.END,
                new RopeAttachment(RopeAttachmentPoint.END,
                        Optional.ofNullable(subLevelTarget).map(SubLevel::getUniqueId).orElse(null),
                        holderB.blockEntity.getBlockPos()));

        // Write ownership flags via accessor mixin (mirrors the assignments in createRope())
        UUID strandUUID = strand.getUUID();

        // Mark this strand as steel cable so the client renderer can identify it
        SteelCableTracker.register(strandUUID);
        RopeStrandHolderBehaviorAccessor accessorA = (RopeStrandHolderBehaviorAccessor) holderA;
        RopeStrandHolderBehaviorAccessor accessorB = (RopeStrandHolderBehaviorAccessor) holderB;

        accessorA.loconautics$setStrandOwner(true);
        accessorA.loconautics$setAttachedRopeID(strandUUID);
        accessorB.loconautics$setStrandOwner(false);
        accessorB.loconautics$setAttachedRopeID(strandUUID);

        // Hand the strand to holderA and register it with the level manager
        holderA.takeOwnedStrand(strand);
        ServerLevelRopeManager.getOrCreate(level).addStrand(strand);

        holderA.blockEntity.notifyUpdate();
        holderB.blockEntity.notifyUpdate();

        // Tell all tracking clients this strand is a steel cable so they use our renderer
        PacketDistributor.sendToPlayersTrackingChunk(
                (ServerLevel) level, new net.minecraft.world.level.ChunkPos(posA),
                new SteelCableStrandPacket(strandUUID));

        level.playSound(null, posA, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 0.5f, 1.0f);
        level.playSound(null, posB, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 0.5f, 1.0f);
        return true;
    }
}