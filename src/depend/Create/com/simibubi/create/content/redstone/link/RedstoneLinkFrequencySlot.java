/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
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
package com.simibubi.create.content.redstone.link;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class RedstoneLinkFrequencySlot
extends ValueBoxTransform.Dual {
    Vec3 horizontal = VecHelper.voxelSpace((double)10.0, (double)5.5, (double)2.5);
    Vec3 vertical = VecHelper.voxelSpace((double)10.0, (double)2.5, (double)5.5);

    public RedstoneLinkFrequencySlot(boolean first) {
        super(first);
    }

    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        Direction facing = (Direction)state.getValue((Property)RedstoneLinkBlock.FACING);
        Vec3 location = VecHelper.voxelSpace((double)8.0, (double)3.01f, (double)5.5);
        if (facing.getAxis().isHorizontal()) {
            location = VecHelper.voxelSpace((double)8.0, (double)5.5, (double)3.01f);
            if (this.isFirst()) {
                location = location.add(0.0, 0.3125, 0.0);
            }
            return this.rotateHorizontally(state, location);
        }
        if (this.isFirst()) {
            location = location.add(0.0, 0.0, 0.3125);
        }
        location = VecHelper.rotateCentered((Vec3)location, (double)(facing == Direction.DOWN ? 180.0 : 0.0), (Direction.Axis)Direction.Axis.X);
        return location;
    }

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        float yRot;
        Direction facing = (Direction)state.getValue((Property)RedstoneLinkBlock.FACING);
        float f = yRot = facing.getAxis().isVertical() ? 0.0f : AngleHelper.horizontalAngle((Direction)facing) + 180.0f;
        float xRot = facing == Direction.UP ? 90.0f : (facing == Direction.DOWN ? 270.0f : 0.0f);
        ((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateYDegrees(yRot)).rotateXDegrees(xRot);
    }

    @Override
    public float getScale() {
        return 0.4975f;
    }
}
