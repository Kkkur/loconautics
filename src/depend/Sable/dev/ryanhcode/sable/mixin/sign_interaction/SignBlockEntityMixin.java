/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.SignBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.entity.SignBlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 */
package dev.ryanhcode.sable.mixin.sign_interaction;

import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value={SignBlockEntity.class})
public abstract class SignBlockEntityMixin
extends BlockEntity {
    public SignBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Overwrite
    public boolean isFacingFrontText(Player player) {
        BlockState state = this.getBlockState();
        Block block = state.getBlock();
        if (block instanceof SignBlock) {
            SignBlock signBlock = (SignBlock)block;
            ActiveSableCompanion helper = Sable.HELPER;
            BlockPos pos = this.getBlockPos();
            Vector3d signCenterPos = JOMLConversion.toJOML((Position)signBlock.getSignHitboxCenterPosition(state).add((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()));
            Vector3d center = helper.projectOutOfSubLevel(this.level, signCenterPos);
            Vector3d deltaDir = JOMLConversion.toJOML((Position)player.position()).sub((Vector3dc)center).normalize();
            float signYRot = signBlock.getYRotationDegrees(state);
            Vector3d signNormal = new Vector3d(0.0, 0.0, 1.0).rotateY(Math.toRadians(-signYRot));
            SubLevel subLevel = helper.getContaining(this.level, (Vec3i)pos);
            if (subLevel != null) {
                subLevel.logicalPose().transformNormal(signNormal);
            }
            return signNormal.dot(deltaDir.x, deltaDir.y, deltaDir.z) > 0.0;
        }
        return false;
    }
}
