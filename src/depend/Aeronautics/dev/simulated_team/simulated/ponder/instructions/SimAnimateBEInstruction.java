/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.ponder.instruction.AnimateBlockEntityInstruction
 *  net.createmod.ponder.api.level.PonderLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.ponder.instructions;

import com.simibubi.create.foundation.ponder.instruction.AnimateBlockEntityInstruction;
import dev.simulated_team.simulated.content.blocks.steering_wheel.SteeringWheelBlockEntity;
import dev.simulated_team.simulated.content.blocks.torsion_spring.TorsionSpringBlockEntity;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SimAnimateBEInstruction
extends AnimateBlockEntityInstruction {
    protected SimAnimateBEInstruction(BlockPos location, float totalDelta, int ticks, BiConsumer<PonderLevel, Float> setter, Function<PonderLevel, Float> getter) {
        super(location, totalDelta, ticks, setter, getter);
    }

    public static AnimateBlockEntityInstruction torsionSpring(BlockPos location, float totalDelta, int ticks) {
        return new SimAnimateBEInstruction(location, totalDelta, ticks, (level, value) -> SimAnimateBEInstruction.castIfPresent(level, location, TorsionSpringBlockEntity.class).ifPresent(be -> be.setAngle(value.floatValue())), level -> SimAnimateBEInstruction.castIfPresent(level, location, TorsionSpringBlockEntity.class).map(TorsionSpringBlockEntity::getAngle).orElse(Float.valueOf(0.0f)));
    }

    public static AnimateBlockEntityInstruction steeringWheel(BlockPos location, float totalDelta, int ticks) {
        return new SimAnimateBEInstruction(location, totalDelta, ticks, (level, value) -> SimAnimateBEInstruction.castIfPresent(level, location, SteeringWheelBlockEntity.class).ifPresent(be -> {
            be.targetAngleToUpdate = value.floatValue();
        }), level -> SimAnimateBEInstruction.castIfPresent(level, location, SteeringWheelBlockEntity.class).map(be -> Float.valueOf(be.targetAngleToUpdate)).orElse(Float.valueOf(0.0f)));
    }

    public static <T> Optional<T> castIfPresent(PonderLevel world, BlockPos pos, Class<T> beType) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (beType.isInstance(blockEntity)) {
            return Optional.of(beType.cast(blockEntity));
        }
        return Optional.empty();
    }
}
