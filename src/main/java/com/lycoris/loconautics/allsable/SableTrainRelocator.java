package com.lycoris.loconautics.allsable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.lycoris.loconautics.Config;
import com.lycoris.loconautics.network.packets.SableTrainRelocatePacket;
import com.lycoris.loconautics.registry.LoconauticsRegistries;

import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.entity.TravellingPoint.ITrackSelector;
import com.simibubi.create.content.trains.entity.TravellingPoint.SteerDirection;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackGraphHelper;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.foundation.utility.CreateLang;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;

import net.createmod.catnip.data.Pair;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.InputEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Client-side wrench relocation for Sable train sub-levels — a faithful re-make of Create's {@code TrainRelocator}
 * state machine, but targeting a Sable train sub-level (identified through {@link SableTrainClientRegistry})
 * instead of a {@code CarriageContraptionEntity}/Create {@code Train}.
 *
 * <p>The ghost rail outline is drawn <b>identically</b> to Create's carriage relocation: the same
 * {@link Outliner#showLine} calls, the same colours (green {@code 0x95DF41} when valid, red {@code 0xEA5C2B} when
 * blocked), the same {@code -0.925} Y offset below the rail surface, the same per-segment width alternation
 * ({@code 0.25} even, {@code 0.16666667} odd) and the same {@code disableLineNormals()} suppression — see
 * {@link #relocateClient}. The HUD action-bar messages reuse Create's own {@code train.relocate*} lang keys so the
 * wording and colours match exactly.
 *
 * <p>Flow: the use-item key press ({@link #onInteract}) starts relocation when the player wrenches a train
 * sub-level, and confirms/aborts it while relocating; {@link #clientTick} redraws the preview and updates the HUD
 * every client tick (mirroring Create's {@code ClientEvents}/{@code InputEvents} wiring). The actual move is
 * validated and applied server-side via {@link SableTrainRelocatePacket}, so the client only ever simulates.
 */
@OnlyIn(Dist.CLIENT)
public final class SableTrainRelocator {

    // Create's exact outline colours (ARGB-less ints, as passed to Outliner): valid = 0x95DF41, invalid = 0xEA5C2B.
    private static final int COLOR_VALID = 9817409;
    private static final int COLOR_INVALID = 15359019;

    /** The body sub-level UUID currently being relocated, or {@code null} when not relocating. */
    private static UUID relocatingSubLevel;
    /** A world block on/near the train when relocation began — the reach anchor (Create's {@code relocatingOrigin}). */
    private static BlockPos relocatingOrigin;
    /** The carriage span of the relocating train (ghost-rail length), copied from the client marker. */
    private static double relocatingSpacing;
    /** The relocating sub-level's display name, for the {@code train.relocate} HUD message. */
    private static Component relocatingName;

    // Per-frame hover cache (mirrors TrainRelocator's lastHovered* fields).
    private static BlockPos lastHoveredPos;
    private static Boolean lastHoveredResult;
    private static List<Vec3> toVisualise;

    private SableTrainRelocator() {
    }

    public static boolean isRelocating() {
        return relocatingSubLevel != null;
    }

    /** True if the stack is the Sable Train Relocator tool (the item that drives this whole flow). */
    private static boolean isRelocator(ItemStack stack) {
        return stack.is(LoconauticsRegistries.SABLE_TRAIN_RELOCATOR.get());
    }

    // ---------------------------------------------------------------------------------------------
    // Use-item key: start relocation (wrench on a train sub-level) / confirm / abort.
    // Analogous to ContraptionHandlerClient.rightClickingOnContraptionsGetsHandledLocally + TrainRelocator.onClicked.
    // ---------------------------------------------------------------------------------------------

    public static void onInteract(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isUseItem()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || player.isSpectator() || mc.level == null) {
            return;
        }

        // ---- already relocating: this click confirms (or aborts) the placement ----
        if (relocatingSubLevel != null) {
            if (!player.canInteractWithBlock(relocatingOrigin, 24.0) || player.isShiftKeyDown()) {
                relocatingSubLevel = null;
                player.displayClientMessage(
                        CreateLang.translateDirect("train.relocate.abort").withStyle(ChatFormatting.RED), true);
                event.setCanceled(true);
                return;
            }
            if (player.isPassenger()) {
                return;
            }
            Boolean relocate = relocateClient(false);
            if (relocate != null && relocate) {
                relocatingSubLevel = null;
            }
            if (relocate != null) {
                event.setCanceled(true);
            }
            return;
        }

        // ---- not relocating: a wrench on a marked train sub-level starts relocation ----
        if (!Config.WRENCH_RELOCATION_ENABLED.get()) {
            return;
        }
        if (!isRelocator(player.getItemInHand(event.getHand()))) {
            return;
        }
        if (!(mc.hitResult instanceof BlockHitResult blockHit) || mc.hitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }
        SubLevel sub = Sable.HELPER.getContaining(mc.level, blockHit.getBlockPos());
        if (sub == null) {
            return;
        }
        SableTrainClientRegistry.TrainMarker marker = SableTrainClientRegistry.get(sub.getUniqueId());
        if (marker == null) {
            return; // not a train sub-level (generic Sable sub-levels are never marked) — leave it alone
        }
        if (Config.WRENCH_RELOCATION_DERAILED_ONLY.get() && !marker.derailed()) {
            return; // config restricts relocation to derailed trains, and this one is on the rails
        }

        relocatingSubLevel = sub.getUniqueId();
        relocatingOrigin = BlockPos.containing((Position) blockHit.getLocation());
        relocatingSpacing = marker.bogeySpacing();
        relocatingName = sub.getName() != null ? Component.literal(sub.getName()) : Component.literal("Train");
        lastHoveredPos = null;
        lastHoveredResult = null;
        toVisualise = null;
        event.setCanceled(true);
        event.setSwingHand(false);
    }

    // ---------------------------------------------------------------------------------------------
    // Client tick: redraw the ghost rail + update the HUD, and enforce the abort/too-far conditions.
    // Analogous to TrainRelocator.clientTick (called by Create's ClientEvents every client tick).
    // ---------------------------------------------------------------------------------------------

    public static void clientTick() {
        if (relocatingSubLevel == null) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || player.isPassenger() || mc.level == null) {
            return;
        }

        SableTrainClientRegistry.TrainMarker marker = SableTrainClientRegistry.get(relocatingSubLevel);
        if (marker == null) {
            relocatingSubLevel = null; // the train was removed/destroyed under us
            return;
        }
        if (!isRelocator(player.getMainHandItem())) {
            player.displayClientMessage(
                    CreateLang.translateDirect("train.relocate.abort").withStyle(ChatFormatting.RED), true);
            relocatingSubLevel = null;
            return;
        }
        if (!player.canInteractWithBlock(relocatingOrigin, 24.0)) {
            player.displayClientMessage(
                    CreateLang.translateDirect("train.relocate.too_far").withStyle(ChatFormatting.RED), true);
            return;
        }

        Boolean success = relocateClient(true);
        if (success == null) {
            player.displayClientMessage(CreateLang.translateDirect("train.relocate", relocatingName), true);
        } else if (success) {
            player.displayClientMessage(
                    CreateLang.translateDirect("train.relocate.valid").withStyle(ChatFormatting.GREEN), true);
        } else {
            player.displayClientMessage(
                    CreateLang.translateDirect("train.relocate.invalid").withStyle(ChatFormatting.RED), true);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // The hover simulation: draw the cached ghost, recompute validity for the current hover, and on a
    // confirming click (simulate=false) fire the serverbound relocation packet.
    // Mirrors TrainRelocator.relocateClient — note Create always simulates on the client; the real move
    // happens server-side.
    // ---------------------------------------------------------------------------------------------

    @Nullable
    private static Boolean relocateClient(boolean simulate) {
        Minecraft mc = Minecraft.getInstance();
        HitResult hitResult = mc.hitResult;
        if (!(hitResult instanceof BlockHitResult blockhit)) {
            return null;
        }
        BlockPos blockPos = blockhit.getBlockPos();

        // Draw the previously-computed ghost line (exactly Create's inline outline: -0.925 Y, alternating widths,
        // disabled line normals, green unless the last segment is the blocking one).
        Vec3 offset = Vec3.ZERO;
        if (simulate && toVisualise != null && lastHoveredResult != null) {
            for (int i = 0; i < toVisualise.size() - 1; i++) {
                Vec3 v1 = toVisualise.get(i).add(offset);
                Vec3 v2 = toVisualise.get(i + 1).add(offset);
                Outliner.getInstance()
                        .showLine(Pair.of(relocatingSubLevel, i),
                                v1.add(0.0, -0.925f, 0.0), v2.add(0.0, -0.925f, 0.0))
                        .colored(lastHoveredResult || i != toVisualise.size() - 2 ? COLOR_VALID : COLOR_INVALID)
                        .disableLineNormals()
                        .lineWidth(i % 2 == 1 ? 0.16666667f : 0.25f);
            }
        }

        if (simulate) {
            if (lastHoveredPos != null && lastHoveredPos.equals(blockPos)) {
                return lastHoveredResult;
            }
            lastHoveredPos = blockPos;
            toVisualise = null;
        }

        BlockState blockState = mc.level.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (!(block instanceof ITrackBlock)) {
            lastHoveredResult = null;
            return null;
        }

        Vec3 lookAngle = mc.player.getLookAngle();
        boolean result = relocate(mc.level, blockPos, lookAngle, true);

        if (!simulate && result) {
            CatnipServices.NETWORK.sendToServer(
                    new SableTrainRelocatePacket(relocatingSubLevel, blockPos, lookAngle, false));
        }
        lastHoveredResult = result;
        return result;
    }

    /**
     * Probes the Create track graph at {@code pos} the way Create's {@code TrainRelocator.relocate} does — resolving
     * a graph location from the look direction, then walking a {@link TravellingPoint} backwards along the rail by
     * the carriage span to lay out the ghost-rail points. Returns whether the placement is valid (the whole span
     * stays on connected, unobstructed track) and, when {@code simulate}, fills {@link #toVisualise}.
     */
    private static boolean relocate(Level level, BlockPos pos, Vec3 lookAngle, boolean simulate) {
        BlockState blockState = level.getBlockState(pos);
        if (!(blockState.getBlock() instanceof ITrackBlock track)) {
            return false;
        }
        Pair<Vec3, Direction.AxisDirection> axis = track.getNearestTrackAxis(level, pos, blockState, lookAngle);
        TrackGraphLocation graphLocation =
                TrackGraphHelper.getGraphLocationAt(level, pos, axis.getSecond(), axis.getFirst());
        if (graphLocation == null || graphLocation.graph == null) {
            return false;
        }
        TrackGraph graph = graphLocation.graph;
        TravellingPoint probe = RailFollower.pointAt(graphLocation);
        if (probe == null) {
            return false;
        }
        Vec3 upNormal = track.getUpNormal(level, pos, blockState);
        ITrackSelector steer = probe.steer(SteerDirection.NONE, upNormal);

        List<Vec3> recorded = new ArrayList<>();
        recorded.add(probe.getPosition(graph));
        boolean blocked = false;
        double remaining = Math.max(1.0, relocatingSpacing);
        while (remaining > 1.0e-3 && !blocked) {
            double d = Math.min(1.0, remaining);
            probe.travel(graph, -d, steer, probe.ignoreEdgePoints(), probe.ignoreTurns(), probe.ignorePortals());
            recorded.add(probe.getPosition(graph));
            if (probe.blocked) {
                blocked = true;
            }
            remaining -= d;
        }
        if (simulate) {
            toVisualise = recorded;
        }
        return !blocked;
    }
}
