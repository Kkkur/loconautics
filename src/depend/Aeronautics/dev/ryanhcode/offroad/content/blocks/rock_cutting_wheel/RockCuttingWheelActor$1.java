/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction$Axis
 */
package dev.ryanhcode.offroad.content.blocks.rock_cutting_wheel;

import net.minecraft.core.Direction;

static class RockCuttingWheelActor.1 {
    static final /* synthetic */ int[] $SwitchMap$net$minecraft$core$Direction$Axis;

    static {
        $SwitchMap$net$minecraft$core$Direction$Axis = new int[Direction.Axis.values().length];
        try {
            RockCuttingWheelActor.1.$SwitchMap$net$minecraft$core$Direction$Axis[Direction.Axis.Z.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RockCuttingWheelActor.1.$SwitchMap$net$minecraft$core$Direction$Axis[Direction.Axis.Y.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RockCuttingWheelActor.1.$SwitchMap$net$minecraft$core$Direction$Axis[Direction.Axis.X.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
