/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.foundation.block.connected;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;

final class AllCTTypes.6
extends AllCTTypes {
    private static final int[][] MAPPING = new int[][]{{1, 6, 9, 4}, {14, 12, 13, 15}, {2, 10, 8, 0}, {5, 5, 5, 5}};

    private AllCTTypes.6(int sheetSize, ConnectedTextureBehaviour.ContextRequirement contextRequirement) {
    }

    @Override
    public int getTextureIndex(ConnectedTextureBehaviour.CTContext context) {
        int type = (context.up ? 2 : 0) + (context.right ? 1 : 0);
        int rot = (context.left ? 2 : 0) + (context.down ? 1 : 0);
        return MAPPING[type][rot];
    }
}
