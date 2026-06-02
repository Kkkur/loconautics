/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.FloatUnaryOperator
 *  net.createmod.ponder.api.level.PonderLevel
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.instruction.TickingInstruction
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.ponder.instructions;

import dev.simulated_team.simulated.content.blocks.altitude_sensor.AltitudeSensorBlockEntity;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.TickingInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AltitudeSensorVisualHeightInstruction
extends TickingInstruction {
    protected final BlockPos location;
    protected final float startValue;
    protected final float endValue;
    protected final FloatUnaryOperator interpolation;

    public AltitudeSensorVisualHeightInstruction(BlockPos location, int ticks, float startValue, float endValue, FloatUnaryOperator interpolation) {
        super(false, ticks);
        this.location = location;
        this.startValue = startValue;
        this.endValue = endValue;
        this.interpolation = interpolation;
    }

    public float getLerpedValue() {
        if (this.totalTicks != 0) {
            return this.startValue + (this.endValue - this.startValue) * this.interpolation.apply(1.0f - (float)this.remainingTicks / (float)this.totalTicks);
        }
        return this.endValue;
    }

    public static class Radial
    extends AltitudeSensorVisualHeightInstruction {
        public Radial(BlockPos location, int ticks, float startValue, float endValue, FloatUnaryOperator interpolation) {
            super(location, ticks, startValue, endValue, interpolation);
        }

        protected void firstTick(PonderScene scene) {
            super.firstTick(scene);
            PonderLevel world = scene.getWorld();
            BlockEntity blockEntity = world.getBlockEntity(this.location);
            if (blockEntity instanceof AltitudeSensorBlockEntity) {
                AltitudeSensorBlockEntity be = (AltitudeSensorBlockEntity)blockEntity;
                be.updateVisualHeight = false;
                be.previousVisualHeight = this.startValue;
                be.visualHeight = this.endValue;
            }
        }

        public void tick(PonderScene scene) {
            super.tick(scene);
            PonderLevel world = scene.getWorld();
            BlockEntity blockEntity = world.getBlockEntity(this.location);
            if (blockEntity instanceof AltitudeSensorBlockEntity) {
                float targetValue;
                AltitudeSensorBlockEntity be = (AltitudeSensorBlockEntity)blockEntity;
                be.visualHeight = targetValue = this.getLerpedValue();
            }
        }
    }

    public static class Linear
    extends AltitudeSensorVisualHeightInstruction {
        public Linear(BlockPos location, int ticks, float startValue, float endValue, FloatUnaryOperator interpolation) {
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
}
