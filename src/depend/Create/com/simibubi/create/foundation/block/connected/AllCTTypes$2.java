/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.foundation.block.connected;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;

final class AllCTTypes.2
extends AllCTTypes {
    private AllCTTypes.2(int sheetSize, ConnectedTextureBehaviour.ContextRequirement contextRequirement) {
    }

    @Override
    public int getTextureIndex(ConnectedTextureBehaviour.CTContext context) {
        return !context.right && !context.left ? 0 : (!context.right ? 3 : (!context.left ? 2 : 1));
    }
}
