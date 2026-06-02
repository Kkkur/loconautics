/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.block.Rotation
 */
package com.simibubi.create.content.kinetics.base;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;

static class RotatedPillarKineticBlock.1 {
    static final /* synthetic */ int[] $SwitchMap$net$minecraft$core$Direction$Axis;
    static final /* synthetic */ int[] $SwitchMap$net$minecraft$world$level$block$Rotation;

    static {
        $SwitchMap$net$minecraft$world$level$block$Rotation = new int[Rotation.values().length];
        try {
            RotatedPillarKineticBlock.1.$SwitchMap$net$minecraft$world$level$block$Rotation[Rotation.COUNTERCLOCKWISE_90.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RotatedPillarKineticBlock.1.$SwitchMap$net$minecraft$world$level$block$Rotation[Rotation.CLOCKWISE_90.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        $SwitchMap$net$minecraft$core$Direction$Axis = new int[Direction.Axis.values().length];
        try {
            RotatedPillarKineticBlock.1.$SwitchMap$net$minecraft$core$Direction$Axis[Direction.Axis.X.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RotatedPillarKineticBlock.1.$SwitchMap$net$minecraft$core$Direction$Axis[Direction.Axis.Z.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
