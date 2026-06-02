/*
 * Decompiled with CFR 0.152.
 */
package dev.eriksonn.aeronautics.content.ponder.instructions;

import dev.eriksonn.aeronautics.content.ponder.instructions.ChangePropellerRotateInstruction;
import dev.eriksonn.aeronautics.content.ponder.instructions.PropellerRotateInstruction;

public static class ChangePropellerRotateInstruction.SetRotationRate
extends ChangePropellerRotateInstruction {
    public ChangePropellerRotateInstruction.SetRotationRate(PropellerRotateInstruction instruction, float targetSpeed) {
        super(instruction, (i, s) -> {
            i.targetSpeed = targetSpeed;
        });
    }
}
