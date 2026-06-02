/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.state.properties.AttachFace
 */
package dev.simulated_team.simulated.content.blocks.throttle_lever;

import net.minecraft.world.level.block.state.properties.AttachFace;

static class ThrottleLeverRenderer.1 {
    static final /* synthetic */ int[] $SwitchMap$net$minecraft$world$level$block$state$properties$AttachFace;

    static {
        $SwitchMap$net$minecraft$world$level$block$state$properties$AttachFace = new int[AttachFace.values().length];
        try {
            ThrottleLeverRenderer.1.$SwitchMap$net$minecraft$world$level$block$state$properties$AttachFace[AttachFace.FLOOR.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ThrottleLeverRenderer.1.$SwitchMap$net$minecraft$world$level$block$state$properties$AttachFace[AttachFace.WALL.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
