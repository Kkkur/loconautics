/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.logistics.itemHatch;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.content.logistics.itemHatch.ItemHatchBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class HatchFilterSlot
extends ValueBoxTransform {
    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        return VecHelper.rotateCentered((Vec3)VecHelper.voxelSpace((double)8.0, (double)5.15, (double)9.5), (double)this.angle(state), (Direction.Axis)Direction.Axis.Y);
    }

    @Override
    public float getScale() {
        return super.getScale() * 0.965f;
    }

    @Override
    public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
        return localHit.distanceTo(this.getLocalOffset(level, pos, state).subtract(0.0, 0.125, 0.0)) < (double)(this.scale / 2.0f);
    }

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        ms.mulPose(Axis.YP.rotationDegrees(this.angle(state)));
        ms.mulPose(Axis.XP.rotationDegrees(-45.0f));
    }

    private float angle(BlockState state) {
        return AngleHelper.horizontalAngle((Direction)state.getOptionalValue((Property)ItemHatchBlock.FACING).orElse(Direction.NORTH));
    }
}
