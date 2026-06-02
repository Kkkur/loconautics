/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 */
package com.simibubi.create.foundation.block.connected;

import java.util.Arrays;
import net.minecraft.core.Direction;

private static class CTModel.CTData {
    private final int[] indices = new int[6];

    public CTModel.CTData() {
        Arrays.fill(this.indices, -1);
    }

    public void put(Direction face, int texture) {
        this.indices[face.get3DDataValue()] = texture;
    }

    public int get(Direction face) {
        return this.indices[face.get3DDataValue()];
    }
}
