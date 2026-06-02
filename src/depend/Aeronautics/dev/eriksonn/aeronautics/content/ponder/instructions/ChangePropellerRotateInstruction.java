/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.simulated_team.simulated.api.BearingSlowdownController
 *  dev.simulated_team.simulated.api.BearingSlowdownController$ContraptionSymmetry
 *  net.createmod.ponder.api.element.ElementLink
 *  net.createmod.ponder.api.element.WorldSectionElement
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.instruction.PonderInstruction
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.jetbrains.annotations.Nullable
 */
package dev.eriksonn.aeronautics.content.ponder.instructions;

import dev.eriksonn.aeronautics.content.blocks.propeller.bearing.propeller_bearing.PropellerBearingBlockEntity;
import dev.eriksonn.aeronautics.content.ponder.instructions.PropellerParticleSpawningInstruction;
import dev.eriksonn.aeronautics.content.ponder.instructions.PropellerRotateInstruction;
import dev.simulated_team.simulated.api.BearingSlowdownController;
import java.util.function.BiConsumer;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public abstract class ChangePropellerRotateInstruction
extends PonderInstruction {
    private final PropellerRotateInstruction instruction;
    private final BiConsumer<PropellerRotateInstruction, PonderScene> consumer;

    ChangePropellerRotateInstruction(PropellerRotateInstruction instruction, BiConsumer<PropellerRotateInstruction, PonderScene> consumer) {
        this.instruction = instruction;
        this.consumer = consumer;
    }

    public boolean isComplete() {
        return true;
    }

    public void tick(PonderScene scene) {
        this.consumer.accept(this.instruction, scene);
    }

    public static class AddSection
    extends ChangePropellerRotateInstruction {
        public AddSection(PropellerRotateInstruction instruction, ElementLink<WorldSectionElement> link) {
            super(instruction, (PropellerRotateInstruction i, PonderScene s) -> i.addSection((PonderScene)s, link));
        }
    }

    public static class StopParticles
    extends ChangePropellerRotateInstruction {
        public StopParticles(PropellerRotateInstruction instruction) {
            super(instruction, (i, s) -> {
                i.spawner = null;
            });
        }
    }

    public static class SetParticles
    extends ChangePropellerRotateInstruction {
        public SetParticles(PropellerRotateInstruction instruction, @Nullable ElementLink<WorldSectionElement> link, float particleAmount, float particleSpeed, float radius, boolean hasCollision) {
            super(instruction, (i, s) -> {
                i.particleSpeedScale = particleSpeed;
                i.particleAmountScale = particleAmount;
                i.spawner = new PropellerParticleSpawningInstruction.ParticleSpawner(link, i.pos.offset(i.direction.getNormal()), i.direction, 0.0f, 0.0f, radius, hasCollision);
            });
        }

        public SetParticles(PropellerRotateInstruction instruction, BlockPos location, @Nullable ElementLink<WorldSectionElement> link, float particleAmount, float particleSpeed, float radius, boolean hasCollision) {
            super(instruction, (i, s) -> {
                i.particleSpeedScale = particleSpeed;
                i.particleAmountScale = particleAmount;
                i.spawner = new PropellerParticleSpawningInstruction.ParticleSpawner(link, location, i.direction, 0.0f, 0.0f, radius, hasCollision);
            });
        }
    }

    public static class StopRotation
    extends ChangePropellerRotateInstruction {
        public StopRotation(PropellerRotateInstruction instruction, float duration) {
            super(instruction, (i, s) -> {
                BlockEntity patt0$temp = s.getWorld().getBlockEntity(i.pos);
                if (patt0$temp instanceof PropellerBearingBlockEntity) {
                    PropellerBearingBlockEntity bearing = (PropellerBearingBlockEntity)patt0$temp;
                    float angle = bearing.getInterpolatedAngle(0.0f);
                    i.slowdownController = new BearingSlowdownController();
                    i.slowdownController.generate(duration, angle, i.currentSpeed, BearingSlowdownController.ContraptionSymmetry.QUARTER);
                }
            });
        }
    }

    public static class SetRotationRate
    extends ChangePropellerRotateInstruction {
        public SetRotationRate(PropellerRotateInstruction instruction, float targetSpeed) {
            super(instruction, (i, s) -> {
                i.targetSpeed = targetSpeed;
            });
        }
    }
}
