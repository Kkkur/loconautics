/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.items.ItemStackHandler
 */
package com.simibubi.create.content.logistics.depot.storage;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public static final class DepotMountedStorage.Handler
extends ItemStackHandler {
    private Runnable onChange = () -> {};

    private DepotMountedStorage.Handler(ItemStack stack) {
        super(1);
        this.setStackInSlot(0, stack);
    }

    protected void onContentsChanged(int slot) {
        this.onChange.run();
    }
}
