/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder
 *  net.createmod.ponder.api.element.ElementLink
 *  net.createmod.ponder.api.element.PonderElement
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.element.ElementLinkImpl
 *  net.createmod.ponder.foundation.instruction.FadeOutOfSceneInstruction
 *  net.minecraft.core.Direction
 */
package dev.simulated_team.simulated.ponder.instructions;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.simulated_team.simulated.ponder.elements.rope.RopeStrandElement;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.PonderElement;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.element.ElementLinkImpl;
import net.createmod.ponder.foundation.instruction.FadeOutOfSceneInstruction;
import net.minecraft.core.Direction;

public class RemoveRopeStrandInstruction
extends FadeOutOfSceneInstruction<RopeStrandElement> {
    public RemoveRopeStrandInstruction(int fadeInTicks, Direction fadeInFrom, RopeStrandElement element, CreateSceneBuilder scene) {
        super(fadeInTicks, fadeInFrom, RemoveRopeStrandInstruction.createLink(element, scene));
    }

    public RemoveRopeStrandInstruction(RopeStrandElement element, CreateSceneBuilder scene) {
        super(0, Direction.DOWN, RemoveRopeStrandInstruction.createLink(element, scene));
    }

    private static ElementLink<RopeStrandElement> createLink(RopeStrandElement element, CreateSceneBuilder scene) {
        ElementLinkImpl link = new ElementLinkImpl(RopeStrandElement.class);
        scene.addInstruction(arg_0 -> RemoveRopeStrandInstruction.lambda$createLink$0(element, (ElementLink)link, arg_0));
        return link;
    }

    private static /* synthetic */ void lambda$createLink$0(RopeStrandElement element, ElementLink link, PonderScene ponderScene) {
        ponderScene.linkElement((PonderElement)element, link);
    }
}
