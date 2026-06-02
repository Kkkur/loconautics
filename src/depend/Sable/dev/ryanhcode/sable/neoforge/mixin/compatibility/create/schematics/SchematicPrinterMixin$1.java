/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.Rotation
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.schematics;

import net.minecraft.world.level.block.Rotation;

static class SchematicPrinterMixin.1 {
    static final /* synthetic */ int[] $SwitchMap$net$minecraft$world$level$block$Rotation;

    static {
        $SwitchMap$net$minecraft$world$level$block$Rotation = new int[Rotation.values().length];
        try {
            SchematicPrinterMixin.1.$SwitchMap$net$minecraft$world$level$block$Rotation[Rotation.NONE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SchematicPrinterMixin.1.$SwitchMap$net$minecraft$world$level$block$Rotation[Rotation.CLOCKWISE_90.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SchematicPrinterMixin.1.$SwitchMap$net$minecraft$world$level$block$Rotation[Rotation.CLOCKWISE_180.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SchematicPrinterMixin.1.$SwitchMap$net$minecraft$world$level$block$Rotation[Rotation.COUNTERCLOCKWISE_90.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
