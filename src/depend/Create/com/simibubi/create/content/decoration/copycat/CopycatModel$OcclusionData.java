/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 */
package com.simibubi.create.content.decoration.copycat;

import net.minecraft.core.Direction;

private static class CopycatModel.OcclusionData {
    private final boolean[] occluded = new boolean[6];

    public void occlude(Direction face) {
        this.occluded[face.get3DDataValue()] = true;
    }

    public boolean isOccluded(Direction face) {
        return face != null && this.occluded[face.get3DDataValue()];
    }
}
