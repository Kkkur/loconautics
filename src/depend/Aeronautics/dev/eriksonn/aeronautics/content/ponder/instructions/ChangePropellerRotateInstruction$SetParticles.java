/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.ponder.api.element.ElementLink
 *  net.createmod.ponder.api.element.WorldSectionElement
 *  net.minecraft.core.BlockPos
 *  org.jetbrains.annotations.Nullable
 */
package dev.eriksonn.aeronautics.content.ponder.instructions;

import dev.eriksonn.aeronautics.content.ponder.instructions.ChangePropellerRotateInstruction;
import dev.eriksonn.aeronautics.content.ponder.instructions.PropellerParticleSpawningInstruction;
import dev.eriksonn.aeronautics.content.ponder.instructions.PropellerRotateInstruction;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public static class ChangePropellerRotateInstruction.SetParticles
extends ChangePropellerRotateInstruction {
    public ChangePropellerRotateInstruction.SetParticles(PropellerRotateInstruction instruction, @Nullable ElementLink<WorldSectionElement> link, float particleAmount, float particleSpeed, float radius, boolean hasCollision) {
        super(instruction, (i, s) -> {
            i.particleSpeedScale = particleSpeed;
            i.particleAmountScale = particleAmount;
            i.spawner = new PropellerParticleSpawningInstruction.ParticleSpawner(link, i.pos.offset(i.direction.getNormal()), i.direction, 0.0f, 0.0f, radius, hasCollision);
        });
    }

    public ChangePropellerRotateInstruction.SetParticles(PropellerRotateInstruction instruction, BlockPos location, @Nullable ElementLink<WorldSectionElement> link, float particleAmount, float particleSpeed, float radius, boolean hasCollision) {
        super(instruction, (i, s) -> {
            i.particleSpeedScale = particleSpeed;
            i.particleAmountScale = particleAmount;
            i.spawner = new PropellerParticleSpawningInstruction.ParticleSpawner(link, location, i.direction, 0.0f, 0.0f, radius, hasCollision);
        });
    }
}
