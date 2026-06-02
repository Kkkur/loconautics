/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 */
package com.simibubi.create.content.fluids.tank;

import java.util.Arrays;
import net.minecraft.core.Direction;

private static class FluidTankModel.CullData {
    boolean[] culledFaces = new boolean[4];

    public FluidTankModel.CullData() {
        Arrays.fill(this.culledFaces, false);
    }

    void setCulled(Direction face, boolean cull) {
        if (face.getAxis().isVertical()) {
            return;
        }
        this.culledFaces[face.get2DDataValue()] = cull;
    }

    boolean isCulled(Direction face) {
        if (face.getAxis().isVertical()) {
            return false;
        }
        return this.culledFaces[face.get2DDataValue()];
    }
}
