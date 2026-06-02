/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.Rotation
 */
package com.simibubi.create.content.trains.bogey;

import net.minecraft.world.level.block.Rotation;

static class AbstractBogeyBlock.1 {
    static final /* synthetic */ int[] $SwitchMap$net$minecraft$world$level$block$Rotation;

    static {
        $SwitchMap$net$minecraft$world$level$block$Rotation = new int[Rotation.values().length];
        try {
            AbstractBogeyBlock.1.$SwitchMap$net$minecraft$world$level$block$Rotation[Rotation.COUNTERCLOCKWISE_90.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AbstractBogeyBlock.1.$SwitchMap$net$minecraft$world$level$block$Rotation[Rotation.CLOCKWISE_90.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
