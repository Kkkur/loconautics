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
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.fluids.pipes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.fluids.pipes.SmartFluidPipeBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

static class SmartFluidPipeBlockEntity.SmartPipeFilterSlot
extends ValueBoxTransform {
    SmartFluidPipeBlockEntity.SmartPipeFilterSlot() {
    }

    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        float y;
        AttachFace face = (AttachFace)state.getValue((Property)SmartFluidPipeBlock.FACE);
        float f = face == AttachFace.CEILING ? 0.55f : (y = face == AttachFace.WALL ? 11.4f : 15.45f);
        float z = face == AttachFace.CEILING ? 4.6f : (face == AttachFace.WALL ? 0.55f : 4.625f);
        return VecHelper.rotateCentered((Vec3)VecHelper.voxelSpace((double)8.0, (double)y, (double)z), (double)this.angleY(state), (Direction.Axis)Direction.Axis.Y);
    }

    @Override
    public float getScale() {
        return super.getScale() * 1.02f;
    }

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        AttachFace face = (AttachFace)state.getValue((Property)SmartFluidPipeBlock.FACE);
        ((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateYDegrees(this.angleY(state))).rotateXDegrees(face == AttachFace.CEILING ? -45.0f : 45.0f);
    }

    protected float angleY(BlockState state) {
        AttachFace face = (AttachFace)state.getValue((Property)SmartFluidPipeBlock.FACE);
        float horizontalAngle = AngleHelper.horizontalAngle((Direction)((Direction)state.getValue((Property)SmartFluidPipeBlock.FACING)));
        if (face == AttachFace.WALL) {
            horizontalAngle += 180.0f;
        }
        return horizontalAngle;
    }
}
