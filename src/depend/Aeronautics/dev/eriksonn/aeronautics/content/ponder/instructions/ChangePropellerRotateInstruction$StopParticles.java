/*
 * Decompiled with CFR 0.152.
 */
package dev.eriksonn.aeronautics.content.ponder.instructions;

import dev.eriksonn.aeronautics.content.ponder.instructions.ChangePropellerRotateInstruction;
import dev.eriksonn.aeronautics.content.ponder.instructions.PropellerRotateInstruction;

public static class ChangePropellerRotateInstruction.StopParticles
extends ChangePropellerRotateInstruction {
    public ChangePropellerRotateInstruction.StopParticles(PropellerRotateInstruction instruction) {
        super(instruction, (i, s) -> {
            i.spawner = null;
        });
    }
}
