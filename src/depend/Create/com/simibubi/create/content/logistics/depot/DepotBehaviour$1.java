/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.neoforge.items.ItemStackHandler
 */
package com.simibubi.create.content.logistics.depot;

import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;

class DepotBehaviour.1
extends ItemStackHandler {
    final /* synthetic */ SmartBlockEntity val$be;

    DepotBehaviour.1(DepotBehaviour this$0, int arg0, SmartBlockEntity smartBlockEntity) {
        this.val$be = smartBlockEntity;
        super(arg0);
    }

    protected void onContentsChanged(int slot) {
        this.val$be.notifyUpdate();
    }
}
