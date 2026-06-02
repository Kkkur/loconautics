/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.state.properties.AttachFace
 */
package com.simibubi.create.content.redstone;

import net.minecraft.world.level.block.state.properties.AttachFace;

static class DirectedDirectionalBlock.1 {
    static final /* synthetic */ int[] $SwitchMap$net$minecraft$world$level$block$state$properties$AttachFace;

    static {
        $SwitchMap$net$minecraft$world$level$block$state$properties$AttachFace = new int[AttachFace.values().length];
        try {
            DirectedDirectionalBlock.1.$SwitchMap$net$minecraft$world$level$block$state$properties$AttachFace[AttachFace.CEILING.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            DirectedDirectionalBlock.1.$SwitchMap$net$minecraft$world$level$block$state$properties$AttachFace[AttachFace.FLOOR.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
