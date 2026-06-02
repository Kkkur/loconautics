/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.Rotation
 */
package dev.simulated_team.simulated.content.blocks.merging_glue;

import net.minecraft.world.level.block.Rotation;

static class MergingGlueBlockEntity.1 {
    static final /* synthetic */ int[] $SwitchMap$net$minecraft$world$level$block$Rotation;

    static {
        $SwitchMap$net$minecraft$world$level$block$Rotation = new int[Rotation.values().length];
        try {
            MergingGlueBlockEntity.1.$SwitchMap$net$minecraft$world$level$block$Rotation[Rotation.NONE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MergingGlueBlockEntity.1.$SwitchMap$net$minecraft$world$level$block$Rotation[Rotation.CLOCKWISE_90.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MergingGlueBlockEntity.1.$SwitchMap$net$minecraft$world$level$block$Rotation[Rotation.COUNTERCLOCKWISE_90.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MergingGlueBlockEntity.1.$SwitchMap$net$minecraft$world$level$block$Rotation[Rotation.CLOCKWISE_180.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
