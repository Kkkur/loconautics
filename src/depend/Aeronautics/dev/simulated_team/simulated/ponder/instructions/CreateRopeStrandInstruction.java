/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.ponder.api.element.AnimatedSceneElement
 *  net.createmod.ponder.foundation.instruction.FadeIntoSceneInstruction
 *  net.minecraft.core.Direction
 */
package dev.simulated_team.simulated.ponder.instructions;

import dev.simulated_team.simulated.ponder.elements.rope.RopeStrandElement;
import net.createmod.ponder.api.element.AnimatedSceneElement;
import net.createmod.ponder.foundation.instruction.FadeIntoSceneInstruction;
import net.minecraft.core.Direction;

public class CreateRopeStrandInstruction
extends FadeIntoSceneInstruction<RopeStrandElement> {
    public CreateRopeStrandInstruction(int fadeInTicks, Direction fadeInFrom, RopeStrandElement element) {
        super(fadeInTicks, fadeInFrom, (AnimatedSceneElement)element);
    }

    public CreateRopeStrandInstruction(RopeStrandElement element) {
        super(0, Direction.DOWN, (AnimatedSceneElement)element);
    }

    protected Class<RopeStrandElement> getElementClass() {
        return RopeStrandElement.class;
    }
}
