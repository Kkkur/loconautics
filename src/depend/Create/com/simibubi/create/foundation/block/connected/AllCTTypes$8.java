/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.foundation.block.connected;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;

final class AllCTTypes.8
extends AllCTTypes {
    private AllCTTypes.8(int sheetSize, ConnectedTextureBehaviour.ContextRequirement contextRequirement) {
    }

    @Override
    public int getTextureIndex(ConnectedTextureBehaviour.CTContext context) {
        int x;
        int n = context.left && context.right ? 2 : (context.left ? 3 : (x = context.right ? 1 : 0));
        int y = context.up && context.down ? 1 : (context.up ? 2 : (context.down ? 0 : 3));
        return x + y * 4;
    }
}
