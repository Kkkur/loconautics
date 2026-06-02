/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.ponder.api.element.ElementLink
 *  net.createmod.ponder.api.element.WorldSectionElement
 *  net.createmod.ponder.foundation.PonderScene
 */
package dev.eriksonn.aeronautics.content.ponder.instructions;

import dev.eriksonn.aeronautics.content.ponder.instructions.ChangePropellerRotateInstruction;
import dev.eriksonn.aeronautics.content.ponder.instructions.PropellerRotateInstruction;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.foundation.PonderScene;

public static class ChangePropellerRotateInstruction.AddSection
extends ChangePropellerRotateInstruction {
    public ChangePropellerRotateInstruction.AddSection(PropellerRotateInstruction instruction, ElementLink<WorldSectionElement> link) {
        super(instruction, (PropellerRotateInstruction i, PonderScene s) -> i.addSection((PonderScene)s, link));
    }
}
