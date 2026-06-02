/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.logistics.factoryBoard;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnection;
import java.util.ArrayList;
import net.minecraft.world.item.ItemStack;

public static class FactoryPanelBehaviour.ItemStackConnections
extends ArrayList<FactoryPanelConnection> {
    public ItemStack item;
    public int totalAmount;

    public FactoryPanelBehaviour.ItemStackConnections(ItemStack item) {
        this.item = item;
    }
}
