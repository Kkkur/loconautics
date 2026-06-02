/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.neoforge.items.ItemStackHandler
 */
package com.simibubi.create.content.logistics.vault;

import net.neoforged.neoforge.items.ItemStackHandler;

class ItemVaultBlockEntity.1
extends ItemStackHandler {
    ItemVaultBlockEntity.1(int arg0) {
        super(arg0);
    }

    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        ItemVaultBlockEntity.this.updateComparators();
        ItemVaultBlockEntity.this.level.blockEntityChanged(ItemVaultBlockEntity.this.worldPosition);
    }
}
