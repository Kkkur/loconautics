/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.FloatUnaryOperator
 *  net.createmod.ponder.api.level.PonderLevel
 *  net.createmod.ponder.foundation.PonderScene
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.ponder.instructions;

import dev.simulated_team.simulated.content.blocks.altitude_sensor.AltitudeSensorBlockEntity;
import dev.simulated_team.simulated.ponder.instructions.AltitudeSensorVisualHeightInstruction;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.foundation.PonderScene;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public static class AltitudeSensorVisualHeightInstruction.Linear
extends AltitudeSensorVisualHeightInstruction {
    public AltitudeSensorVisualHeightInstruction.Linear(BlockPos location, int ticks, float startValue, float endValue, FloatUnaryOperator interpolation) {
        super(location, ticks, startValue, endValue, interpolation);
    }

    protected void firstTick(PonderScene scene) {
        super.firstTick(scene);
        PonderLevel world = scene.getWorld();
        BlockEntity blockEntity = world.getBlockEntity(this.location);
        if (blockEntity instanceof AltitudeSensorBlockEntity) {
            AltitudeSensorBlockEntity be = (AltitudeSensorBlockEntity)blockEntity;
            be.updateVisualHeight = true;
            be.previousVisualHeight = this.location.getY();
            be.visualHeight = this.location.getY();
        }
    }

    public void tick(PonderScene scene) {
        super.tick(scene);
        PonderLevel world = scene.getWorld();
        BlockEntity blockEntity = world.getBlockEntity(this.location);
        if (blockEntity instanceof AltitudeSensorBlockEntity) {
            AltitudeSensorBlockEntity be = (AltitudeSensorBlockEntity)blockEntity;
            float targetValue = this.getLerpedValue();
            be.lowSignal = be.toNormalHeight((float)this.location.getY() - targetValue);
            be.highSignal = be.toNormalHeight((float)this.location.getY() - targetValue + 1.0f);
        }
    }
}
