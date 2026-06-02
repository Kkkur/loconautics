/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 */
package com.simibubi.create.content.logistics.stockTicker;

import com.simibubi.create.content.logistics.stockTicker.StockKeeperCategoryMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class StockTickerBlockEntity.CategoryMenuProvider
implements MenuProvider {
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return StockKeeperCategoryMenu.create(pContainerId, pPlayerInventory, StockTickerBlockEntity.this);
    }

    public Component getDisplayName() {
        return Component.empty();
    }
}
