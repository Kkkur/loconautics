/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.foundation.blockEntity;

import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;

public static interface IMultiBlockEntityContainer.Inventory
extends IMultiBlockEntityContainer {
    default public boolean hasInventory() {
        return false;
    }
}
