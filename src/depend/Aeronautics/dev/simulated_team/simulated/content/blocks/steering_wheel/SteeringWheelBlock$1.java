/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 */
package dev.simulated_team.simulated.content.blocks.steering_wheel;

import net.minecraft.core.Direction;

static class SteeringWheelBlock.1 {
    static final /* synthetic */ int[] $SwitchMap$net$minecraft$core$Direction;

    static {
        $SwitchMap$net$minecraft$core$Direction = new int[Direction.values().length];
        try {
            SteeringWheelBlock.1.$SwitchMap$net$minecraft$core$Direction[Direction.UP.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SteeringWheelBlock.1.$SwitchMap$net$minecraft$core$Direction[Direction.DOWN.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
