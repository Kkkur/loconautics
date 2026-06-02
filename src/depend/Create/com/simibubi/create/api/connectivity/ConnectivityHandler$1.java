/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction$Axis
 */
package com.simibubi.create.api.connectivity;

import net.minecraft.core.Direction;

static class ConnectivityHandler.1 {
    static final /* synthetic */ int[] $SwitchMap$net$minecraft$core$Direction$Axis;

    static {
        $SwitchMap$net$minecraft$core$Direction$Axis = new int[Direction.Axis.values().length];
        try {
            ConnectivityHandler.1.$SwitchMap$net$minecraft$core$Direction$Axis[Direction.Axis.X.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ConnectivityHandler.1.$SwitchMap$net$minecraft$core$Direction$Axis[Direction.Axis.Y.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ConnectivityHandler.1.$SwitchMap$net$minecraft$core$Direction$Axis[Direction.Axis.Z.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
