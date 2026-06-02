/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform$Sided
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.blocks.gimbal_sensor;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.simulated_team.simulated.content.blocks.gimbal_sensor.GimbalSensorBlock;
import dev.simulated_team.simulated.content.blocks.gimbal_sensor.GimbalSensorBlockEntity;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public static class GimbalSensorBlockEntity.GimbalSensorValueBox
extends ValueBoxTransform.Sided {
    GimbalSensorBlockEntity be;

    public GimbalSensorBlockEntity.GimbalSensorValueBox(GimbalSensorBlockEntity be) {
        this.be = be;
    }

    public ValueBoxTransform.Sided fromSide(Direction direction) {
        this.direction = direction;
        this.be.axisBehaviour.lastSide = direction;
        return this;
    }

    protected boolean isSideActive(BlockState state, Direction direction) {
        boolean a = direction.getAxis() == state.getValue(GimbalSensorBlock.HORIZONTAL_AXIS);
        return direction.getAxis().isHorizontal();
    }

    protected Vec3 getSouthLocation() {
        return VecHelper.voxelSpace((double)8.0, (double)8.0, (double)16.0);
    }

    public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
        Vec3 offset = this.getLocalOffset(level, pos, state);
        if (offset == null) {
            return false;
        }
        return localHit.distanceTo(offset) < (double)(this.scale / 1.5f);
    }
}
