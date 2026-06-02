/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 */
package com.simibubi.create.content.trains.track;

import net.minecraft.core.Direction;

static class TrackShape.1 {
    static final /* synthetic */ int[] $SwitchMap$net$minecraft$core$Direction;

    static {
        $SwitchMap$net$minecraft$core$Direction = new int[Direction.values().length];
        try {
            TrackShape.1.$SwitchMap$net$minecraft$core$Direction[Direction.EAST.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrackShape.1.$SwitchMap$net$minecraft$core$Direction[Direction.NORTH.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrackShape.1.$SwitchMap$net$minecraft$core$Direction[Direction.SOUTH.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TrackShape.1.$SwitchMap$net$minecraft$core$Direction[Direction.WEST.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
