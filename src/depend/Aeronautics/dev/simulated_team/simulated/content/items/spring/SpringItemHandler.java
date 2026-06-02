/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.Sable
 *  foundry.veil.api.network.VeilPacketManager
 *  net.createmod.catnip.outliner.Outliner
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.items.spring;

import dev.ryanhcode.sable.Sable;
import dev.simulated_team.simulated.content.items.spring.SpringItem;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.index.SimItems;
import dev.simulated_team.simulated.network.packets.PlaceSpringPacket;
import dev.simulated_team.simulated.util.SimColors;
import dev.simulated_team.simulated.util.SimDistUtil;
import dev.simulated_team.simulated.util.click_interactions.InteractCallback;
import foundry.veil.api.network.VeilPacketManager;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SpringItemHandler
implements InteractCallback {
    public static final double MAX_LENGTH = 9.0;
    public BlockPos linkPos;
    public Direction linkDirection;

    public boolean tryStartPlacement(UseOnContext context) {
        LocalPlayer player = (LocalPlayer)SimDistUtil.getClientPlayer();
        Level level = player.level();
        Direction dir = context.getClickedFace();
        BlockPos pos = context.getClickedPos();
        BlockPos relative = pos.relative(dir);
        if (this.linkPos != null) {
            return false;
        }
        if (!this.testPlacementAndSendError(level, relative, pos, dir)) {
            return false;
        }
        this.linkPos = pos;
        this.linkDirection = dir;
        return true;
    }

    @Override
    public InteractCallback.Result onUse(int modifiers, int action, KeyMapping rightKey) {
        LocalPlayer player = (LocalPlayer)SimDistUtil.getClientPlayer();
        Level level = player.level();
        if (action == 1) {
            BlockHitResult hit;
            InteractionHand hand = this.getHandOrNull(player);
            if (hand == null) {
                this.reset(true);
                return InteractCallback.Result.empty();
            }
            if (this.linkPos != null && player.isShiftKeyDown()) {
                player.swing(hand);
                this.reset(true);
                return new InteractCallback.Result(true);
            }
            HitResult clientHit = Minecraft.getInstance().hitResult;
            if (clientHit instanceof BlockHitResult && (hit = (BlockHitResult)clientHit).getType() != HitResult.Type.MISS && this.linkPos != null) {
                BlockPos parentCenter;
                Direction dir = hit.getDirection();
                BlockPos pos = hit.getBlockPos();
                BlockPos childCenter = pos.relative(dir);
                if (this.testExceedsRange(level, childCenter, parentCenter = this.linkPos.relative(this.linkDirection))) {
                    this.sendMessage("out_of_range", SimColors.NUH_UH_RED);
                    return InteractCallback.Result.empty();
                }
                if (parentCenter.equals((Object)childCenter)) {
                    this.sendMessage("same_block", SimColors.NUH_UH_RED);
                    return InteractCallback.Result.empty();
                }
                if (!this.testPlacementAndSendError(level, childCenter, pos, dir)) {
                    return InteractCallback.Result.empty();
                }
                player.swing(hand);
                VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new PlaceSpringPacket(this.linkPos, pos, this.linkDirection, dir, hand)});
                this.reset(false);
                return new InteractCallback.Result(true);
            }
        }
        return InteractCallback.Result.empty();
    }

    private boolean testExceedsRange(Level level, BlockPos childPos, BlockPos parentPos) {
        return Sable.HELPER.distanceSquaredWithSubLevels(level, (double)childPos.getX() + 0.5, (double)childPos.getY() + 0.5, (double)childPos.getZ() + 0.5, (double)parentPos.getX() + 0.5, (double)parentPos.getY() + 0.5, (double)parentPos.getZ() + 0.5) > 81.0;
    }

    private boolean testPlacementAndSendError(Level level, BlockPos relative, BlockPos pos, Direction dir) {
        if (!level.getBlockState(relative).canBeReplaced()) {
            this.sendMessage("block_exists", SimColors.NUH_UH_RED);
            return false;
        }
        if (!Block.canSupportCenter((LevelReader)level, (BlockPos)pos, (Direction)dir)) {
            this.sendMessage("not_enough_support", SimColors.NUH_UH_RED);
            return false;
        }
        return true;
    }

    @Nullable
    public InteractionHand getHandOrNull(LocalPlayer player) {
        ItemStack mainItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offHandItem = player.getItemInHand(InteractionHand.OFF_HAND);
        InteractionHand hand = null;
        if (mainItem.getItem() instanceof SpringItem) {
            hand = InteractionHand.MAIN_HAND;
        } else if (offHandItem.getItem() instanceof SpringItem) {
            hand = InteractionHand.OFF_HAND;
        }
        return hand;
    }

    public void reset(boolean sayMessage) {
        if (sayMessage && this.linkPos != null) {
            this.sendMessage("connection_terminated", SimColors.NUH_UH_RED);
        }
        this.linkPos = null;
        this.linkDirection = null;
    }

    public void sendMessage(String message, int color) {
        SimLang.translate("spring." + message, new Object[0]).color(color).sendStatus(SimDistUtil.getClientPlayer());
    }

    @Override
    public void clientTick(Level level, LocalPlayer player) {
        if (!player.getMainHandItem().is(SimItems.SPRING) && !player.getOffhandItem().is(SimItems.SPRING)) {
            this.reset(true);
            return;
        }
        if (this.linkPos != null) {
            Vec3 linkVec = new Vec3((double)this.linkDirection.getStepX(), (double)this.linkDirection.getStepY(), (double)this.linkDirection.getStepZ());
            AABB linkAABB = new AABB(this.linkPos).inflate(-0.3).move(linkVec.scale(0.65));
            Outliner.getInstance().showAABB((Object)(String.valueOf(this.linkPos) + "Spring"), linkAABB).colored(SimColors.SUCCESS_LIME).lineWidth(0.0625f);
            HitResult clientHit = Minecraft.getInstance().hitResult;
            if (clientHit != null && clientHit.getType() != HitResult.Type.MISS && clientHit instanceof BlockHitResult) {
                BlockHitResult hit = (BlockHitResult)clientHit;
                BlockPos pos = hit.getBlockPos();
                Direction dir = hit.getDirection();
                BlockPos childCenter = pos.relative(dir);
                BlockPos parentCenter = this.linkPos.relative(this.linkDirection);
                int color = SimColors.SUCCESS_LIME;
                if (!level.getBlockState(pos.relative(dir)).canBeReplaced() || !Block.canSupportCenter((LevelReader)level, (BlockPos)pos, (Direction)dir) || this.linkPos.relative(this.linkDirection).equals((Object)pos.relative(dir)) || this.testExceedsRange(level, childCenter, parentCenter)) {
                    color = SimColors.NUH_UH_RED;
                }
                AABB hitAABB = new AABB(pos).inflate(-0.3).move(new Vec3((double)dir.getStepX(), (double)dir.getStepY(), (double)dir.getStepZ()).scale(0.65));
                Vec3 globalFirstPoint = Sable.HELPER.projectOutOfSubLevel(level, linkAABB.getCenter());
                Vec3 globalTarget = Sable.HELPER.projectOutOfSubLevel(level, hitAABB.getCenter());
                DustParticleOptions data = new DustParticleOptions(new Color(color).asVectorF(), 1.0f);
                double totalFlyingTicks = 10.0;
                int segments = 4;
                for (int i = 0; i < 4; ++i) {
                    Vec3 vec = globalFirstPoint.lerp(globalTarget, (double)level.getRandom().nextFloat());
                    level.addParticle((ParticleOptions)data, vec.x, vec.y, vec.z, 0.0, 0.0, 0.0);
                }
                Outliner.getInstance().showAABB((Object)(String.valueOf(this.linkPos) + " Spring Selection"), hitAABB).colored(color).lineWidth(0.0625f);
            }
        }
    }
}
