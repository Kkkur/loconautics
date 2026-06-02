/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllSpecialTextures
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  foundry.veil.api.network.VeilPacketManager
 *  net.createmod.catnip.outliner.Outliner
 *  net.createmod.catnip.render.BindableTexture
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.items.merging_glue;

import com.simibubi.create.AllSpecialTextures;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.index.SimTags;
import dev.simulated_team.simulated.network.packets.PlaceMergingGluePacket;
import dev.simulated_team.simulated.service.SimConfigService;
import dev.simulated_team.simulated.util.SimColors;
import dev.simulated_team.simulated.util.SimDistUtil;
import foundry.veil.api.network.VeilPacketManager;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.render.BindableTexture;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MergingGlueItemHandler {
    public BlockPos firstPos;
    public Direction firstDirection;

    public boolean onItemUseBlock(Level level, Player player, ItemStack itemStack, InteractionHand hand) {
        BlockHitResult hit;
        if (itemStack.isEmpty() || !itemStack.is(SimTags.Items.MERGING_GLUE)) {
            return false;
        }
        if (player.isShiftKeyDown()) {
            this.reset(true);
            return false;
        }
        HitResult clientHit = Minecraft.getInstance().hitResult;
        if (clientHit instanceof BlockHitResult && (hit = (BlockHitResult)clientHit).getType() != HitResult.Type.MISS) {
            Direction normal = hit.getDirection();
            BlockPos pos = hit.getBlockPos();
            if (this.firstPos != null && Sable.HELPER.getContaining(level, (Vec3i)this.firstPos) == null) {
                MergingGlueItemHandler.sendMessage("only_between_sub_levels", SimColors.NUH_UH_RED);
                return false;
            }
            SubLevel subLevel = Sable.HELPER.getContaining(level, (Vec3i)pos);
            if (subLevel == null) {
                MergingGlueItemHandler.sendMessage("only_between_sub_levels", SimColors.NUH_UH_RED);
                return false;
            }
            float maxRange = SimConfigService.INSTANCE.server().assembly.mergingGlueRange.getF();
            if (this.firstPos != null && Sable.HELPER.distanceSquaredWithSubLevels(level, (Position)Vec3.atCenterOf((Vec3i)pos), (Position)Vec3.atCenterOf((Vec3i)this.firstPos)) > (double)(maxRange * maxRange)) {
                MergingGlueItemHandler.sendMessage("out_of_range", SimColors.NUH_UH_RED);
                return false;
            }
            BlockPos relative = pos.relative(normal);
            if (this.firstPos != null && this.firstPos.relative(this.firstDirection).equals((Object)relative)) {
                MergingGlueItemHandler.sendMessage("same_block", SimColors.NUH_UH_RED);
                return false;
            }
            if (!level.getBlockState(relative).canBeReplaced()) {
                MergingGlueItemHandler.sendMessage("block_exists", SimColors.NUH_UH_RED);
                return false;
            }
            if (this.firstDirection != null && (this.firstDirection.getAxis().isHorizontal() != normal.getAxis().isHorizontal() || normal.getAxis().isVertical() && normal == this.firstDirection)) {
                MergingGlueItemHandler.sendMessage("invalid_directions", SimColors.NUH_UH_RED);
                return false;
            }
            if (this.firstPos != null && subLevel == Sable.HELPER.getContaining(level, (Vec3i)this.firstPos)) {
                MergingGlueItemHandler.sendMessage("same_sub_level", SimColors.NUH_UH_RED);
                return false;
            }
            if (!MergingGlueItemHandler.canSupportGlue(level, pos, normal)) {
                if (this.firstPos == null) {
                    this.firstPos = pos;
                    this.firstDirection = normal;
                    return true;
                }
                if (!this.firstPos.relative(this.firstDirection).equals((Object)relative)) {
                    player.swing(hand);
                    VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new PlaceMergingGluePacket(this.firstPos, pos, this.firstDirection, normal, hand)});
                    this.reset(false);
                    return true;
                }
            } else if (this.firstPos != null) {
                MergingGlueItemHandler.sendMessage("not_enough_support", SimColors.NUH_UH_RED);
            }
        }
        return false;
    }

    public void resetWhenShiftRC(Player player, ItemStack stack) {
        if (player.isShiftKeyDown() && stack.is(SimTags.Items.MERGING_GLUE)) {
            this.reset(true);
        }
    }

    public void clientTick(Level level, LocalPlayer player) {
        if (!player.getMainHandItem().is(SimTags.Items.MERGING_GLUE) && !player.getOffhandItem().is(SimTags.Items.MERGING_GLUE)) {
            this.reset(true);
            return;
        }
        if (this.firstPos != null) {
            BlockHitResult hit;
            Vec3 linkVec = new Vec3((double)this.firstDirection.getStepX(), (double)this.firstDirection.getStepY(), (double)this.firstDirection.getStepZ());
            AABB linkAABB = new AABB(this.firstPos).contract(-linkVec.x, -linkVec.y, -linkVec.z).inflate(-0.1);
            Outliner.getInstance().showAABB((Object)(String.valueOf(this.firstPos) + "MergingGlue"), linkAABB).colored(SimColors.SUCCESS_LIME).withFaceTexture((BindableTexture)AllSpecialTextures.GLUE).lineWidth(0.0625f);
            HitResult clientHit = Minecraft.getInstance().hitResult;
            if (clientHit.getType() != HitResult.Type.MISS && clientHit instanceof BlockHitResult && Sable.HELPER.getContaining(level, (Vec3i)(hit = (BlockHitResult)clientHit).getBlockPos()) != null) {
                SubLevel subLevel;
                boolean invalid;
                BlockPos pos = hit.getBlockPos();
                Direction normal = hit.getDirection();
                float maxRange = SimConfigService.INSTANCE.server().assembly.mergingGlueRange.getF();
                int color = SimColors.SUCCESS_LIME;
                BlockState replaceState = level.getBlockState(pos.relative(normal));
                boolean bl = invalid = !replaceState.canBeReplaced() || MergingGlueItemHandler.canSupportGlue(level, pos, normal) || this.firstPos.relative(this.firstDirection).equals((Object)pos.relative(normal)) || Sable.HELPER.distanceSquaredWithSubLevels(level, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, (double)this.firstPos.getX() + 0.5, (double)this.firstPos.getY() + 0.5, (double)this.firstPos.getZ() + 0.5) > (double)(maxRange * maxRange);
                if (this.firstDirection != null && (this.firstDirection.getAxis().isHorizontal() != normal.getAxis().isHorizontal() || normal.getAxis().isVertical() && normal == this.firstDirection)) {
                    invalid = true;
                }
                if ((subLevel = Sable.HELPER.getContaining(level, (Vec3i)pos)) == null || subLevel == Sable.HELPER.getContaining(level, (Vec3i)this.firstPos)) {
                    invalid = true;
                }
                if (invalid) {
                    color = SimColors.NUH_UH_RED;
                }
                AABB hitAABB = new AABB(pos).contract((double)(-normal.getStepX()), (double)(-normal.getStepY()), (double)(-normal.getStepZ())).inflate(-0.1);
                Vec3 globalFirstPoint = Sable.HELPER.projectOutOfSubLevel(level, linkAABB.getCenter());
                Vec3 globalTarget = Sable.HELPER.projectOutOfSubLevel(level, hitAABB.getCenter());
                DustParticleOptions data = new DustParticleOptions(new Color(color).asVectorF(), 1.0f);
                boolean segments = true;
                for (int i = 0; i < 1; ++i) {
                    Vec3 vec = globalFirstPoint.lerp(globalTarget, (double)level.getRandom().nextFloat() * 0.8 + 0.1);
                    float variation = 0.8f;
                    vec = vec.add((double)(0.8f * (level.getRandom().nextFloat() * 0.5f - 0.25f)), (double)(0.8f * (level.getRandom().nextFloat() * 0.5f - 0.25f)), (double)(0.8f * (level.getRandom().nextFloat() * 0.5f - 0.25f)));
                    level.addParticle((ParticleOptions)data, vec.x, vec.y, vec.z, 0.0, 0.0, 0.0);
                }
                Outliner.getInstance().showAABB((Object)(String.valueOf(this.firstPos) + " Merging Glue Selection"), hitAABB).colored(color).withFaceTexture((BindableTexture)AllSpecialTextures.GLUE).lineWidth(0.0625f);
            }
        }
    }

    public void reset(boolean sayMessage) {
        if (sayMessage && this.firstPos != null) {
            MergingGlueItemHandler.sendMessage("connection_terminated", SimColors.DISCARDABLE_ORANGE);
        }
        this.firstPos = null;
        this.firstDirection = null;
    }

    public static void sendMessage(String message, int color) {
        SimLang.translate("merging_glue." + message, new Object[0]).color(color).sendStatus(SimDistUtil.getClientPlayer());
    }

    private static boolean canSupportGlue(Level level, BlockPos pos, Direction normal) {
        return level.getBlockState(pos).getBlockSupportShape((BlockGetter)level, pos).getFaceShape(normal).isEmpty();
    }
}
