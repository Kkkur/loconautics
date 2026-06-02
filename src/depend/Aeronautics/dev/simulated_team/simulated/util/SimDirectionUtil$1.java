/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction$Axis
 */
package dev.simulated_team.simulated.util;

import net.minecraft.core.Direction;

static class SimDirectionUtil.1 {
    static final /* synthetic */ int[] $SwitchMap$net$minecraft$core$Direction$Axis;

    static {
        $SwitchMap$net$minecraft$core$Direction$Axis = new int[Direction.Axis.values().length];
        try {
            SimDirectionUtil.1.$SwitchMap$net$minecraft$core$Direction$Axis[Direction.Axis.X.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SimDirectionUtil.1.$SwitchMap$net$minecraft$core$Direction$Axis[Direction.Axis.Y.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SimDirectionUtil.1.$SwitchMap$net$minecraft$core$Direction$Axis[Direction.Axis.Z.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
