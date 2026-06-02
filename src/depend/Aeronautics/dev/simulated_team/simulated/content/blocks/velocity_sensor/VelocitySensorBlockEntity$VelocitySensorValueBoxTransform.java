/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform$Sided
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.blocks.velocity_sensor;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.simulated_team.simulated.content.blocks.util.AbstractDirectionalAxisBlock;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

private static class VelocitySensorBlockEntity.VelocitySensorValueBoxTransform
extends ValueBoxTransform.Sided {
    private VelocitySensorBlockEntity.VelocitySensorValueBoxTransform() {
    }

    protected Vec3 getSouthLocation() {
        return VecHelper.voxelSpace((double)8.0, (double)8.0, (double)12.8);
    }

    public float getScale() {
        return 0.35f;
    }

    protected boolean isSideActive(BlockState state, Direction direction) {
        return AbstractDirectionalAxisBlock.getAxis(state) == direction.getAxis();
    }
}
