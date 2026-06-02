/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 */
package com.simibubi.create.content.fluids;

import net.minecraft.core.Direction;

static class FluidMesh.1 {
    static final /* synthetic */ int[] $SwitchMap$net$minecraft$core$Direction;

    static {
        $SwitchMap$net$minecraft$core$Direction = new int[Direction.values().length];
        try {
            FluidMesh.1.$SwitchMap$net$minecraft$core$Direction[Direction.NORTH.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            FluidMesh.1.$SwitchMap$net$minecraft$core$Direction[Direction.SOUTH.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            FluidMesh.1.$SwitchMap$net$minecraft$core$Direction[Direction.WEST.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            FluidMesh.1.$SwitchMap$net$minecraft$core$Direction[Direction.EAST.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
