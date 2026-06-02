/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.foundation.block.connected;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;

final class AllCTTypes.7
extends AllCTTypes {
    private AllCTTypes.7(int sheetSize, ConnectedTextureBehaviour.ContextRequirement contextRequirement) {
    }

    @Override
    public int getTextureIndex(ConnectedTextureBehaviour.CTContext context) {
        return (context.up ? 1 : 0) + (context.down ? 2 : 0) + (context.left ? 4 : 0) + (context.right ? 8 : 0);
    }
}
