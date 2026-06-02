/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.neoforge.items.ItemStackHandler
 */
package com.simibubi.create.content.schematics.table;

import net.neoforged.neoforge.items.ItemStackHandler;

public class SchematicTableBlockEntity.SchematicTableInventory
extends ItemStackHandler {
    public SchematicTableBlockEntity.SchematicTableInventory() {
        super(2);
    }

    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        SchematicTableBlockEntity.this.setChanged();
    }
}
