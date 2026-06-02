/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 */
package dev.ryanhcode.sable.render.region;

import net.minecraft.core.Direction;

static class SimpleCulledRenderRegionBuilder.1 {
    static final /* synthetic */ int[] $SwitchMap$net$minecraft$core$Direction;

    static {
        $SwitchMap$net$minecraft$core$Direction = new int[Direction.values().length];
        try {
            SimpleCulledRenderRegionBuilder.1.$SwitchMap$net$minecraft$core$Direction[Direction.DOWN.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SimpleCulledRenderRegionBuilder.1.$SwitchMap$net$minecraft$core$Direction[Direction.UP.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SimpleCulledRenderRegionBuilder.1.$SwitchMap$net$minecraft$core$Direction[Direction.NORTH.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SimpleCulledRenderRegionBuilder.1.$SwitchMap$net$minecraft$core$Direction[Direction.SOUTH.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SimpleCulledRenderRegionBuilder.1.$SwitchMap$net$minecraft$core$Direction[Direction.WEST.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SimpleCulledRenderRegionBuilder.1.$SwitchMap$net$minecraft$core$Direction[Direction.EAST.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
