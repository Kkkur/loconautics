/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.state.properties.AttachFace
 */
package dev.simulated_team.simulated.data;

import net.minecraft.world.level.block.state.properties.AttachFace;

static class SimBlockStateGen.1 {
    static final /* synthetic */ int[] $SwitchMap$net$minecraft$world$level$block$state$properties$AttachFace;

    static {
        $SwitchMap$net$minecraft$world$level$block$state$properties$AttachFace = new int[AttachFace.values().length];
        try {
            SimBlockStateGen.1.$SwitchMap$net$minecraft$world$level$block$state$properties$AttachFace[AttachFace.CEILING.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SimBlockStateGen.1.$SwitchMap$net$minecraft$world$level$block$state$properties$AttachFace[AttachFace.WALL.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SimBlockStateGen.1.$SwitchMap$net$minecraft$world$level$block$state$properties$AttachFace[AttachFace.FLOOR.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
