/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.instruction.PonderInstruction
 */
package dev.simulated_team.simulated.ponder.instructions;

import dev.simulated_team.simulated.mixin_interface.ponder.PonderSceneExtension;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.PonderInstruction;

public class CustomToggleBaseShadowInstruction
extends PonderInstruction {
    public boolean isComplete() {
        return true;
    }

    public void tick(PonderScene scene) {
        ((PonderSceneExtension)scene).simulated$toggleRenderBasePlateShadow();
    }
}
