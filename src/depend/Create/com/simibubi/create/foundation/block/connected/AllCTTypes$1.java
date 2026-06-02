/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.foundation.block.connected;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;

final class AllCTTypes.1
extends AllCTTypes {
    private AllCTTypes.1(int sheetSize, ConnectedTextureBehaviour.ContextRequirement contextRequirement) {
    }

    @Override
    public int getTextureIndex(ConnectedTextureBehaviour.CTContext context) {
        return (context.right ? 1 : 0) + (context.left ? 2 : 0);
    }
}
