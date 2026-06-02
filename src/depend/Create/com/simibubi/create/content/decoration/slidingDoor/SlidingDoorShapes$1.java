/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 */
package com.simibubi.create.content.decoration.slidingDoor;

import net.minecraft.core.Direction;

static class SlidingDoorShapes.1 {
    static final /* synthetic */ int[] $SwitchMap$net$minecraft$core$Direction;

    static {
        $SwitchMap$net$minecraft$core$Direction = new int[Direction.values().length];
        try {
            SlidingDoorShapes.1.$SwitchMap$net$minecraft$core$Direction[Direction.SOUTH.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SlidingDoorShapes.1.$SwitchMap$net$minecraft$core$Direction[Direction.WEST.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SlidingDoorShapes.1.$SwitchMap$net$minecraft$core$Direction[Direction.NORTH.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
