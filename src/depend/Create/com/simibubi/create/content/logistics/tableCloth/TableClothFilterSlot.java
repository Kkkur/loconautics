/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.logistics.tableCloth;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.logistics.tableCloth.TableClothBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

class TableClothFilterSlot
extends ValueBoxTransform {
    private TableClothBlockEntity be;

    public TableClothFilterSlot(TableClothBlockEntity be) {
        this.be = be;
    }

    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        Vec3 v = this.be.sideOccluded ? VecHelper.voxelSpace((double)8.0, (double)0.75, (double)15.25) : VecHelper.voxelSpace((double)12.0, (double)-2.75, (double)16.75);
        return VecHelper.rotateCentered((Vec3)v, (double)(-this.be.facing.toYRot()), (Direction.Axis)Direction.Axis.Y);
    }

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        ((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateYDegrees(180.0f - this.be.facing.toYRot())).rotateXDegrees(this.be.sideOccluded ? 90.0f : 0.0f);
    }
}
