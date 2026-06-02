/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.state.properties.RailShape
 */
package com.simibubi.create.content.redstone.rail;

import net.minecraft.world.level.block.state.properties.RailShape;

static class ControllerRailGenerator.1 {
    static final /* synthetic */ int[] $SwitchMap$net$minecraft$world$level$block$state$properties$RailShape;

    static {
        $SwitchMap$net$minecraft$world$level$block$state$properties$RailShape = new int[RailShape.values().length];
        try {
            ControllerRailGenerator.1.$SwitchMap$net$minecraft$world$level$block$state$properties$RailShape[RailShape.EAST_WEST.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ControllerRailGenerator.1.$SwitchMap$net$minecraft$world$level$block$state$properties$RailShape[RailShape.ASCENDING_WEST.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ControllerRailGenerator.1.$SwitchMap$net$minecraft$world$level$block$state$properties$RailShape[RailShape.ASCENDING_EAST.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ControllerRailGenerator.1.$SwitchMap$net$minecraft$world$level$block$state$properties$RailShape[RailShape.ASCENDING_SOUTH.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
