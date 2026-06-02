/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.IntAttached
 *  net.minecraft.core.BlockPos
 */
package com.simibubi.create.content.logistics.tableCloth;

import com.simibubi.create.content.logistics.tableCloth.ShoppingListItem;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.createmod.catnip.data.IntAttached;
import net.minecraft.core.BlockPos;

public static class ShoppingListItem.ShoppingList.Mutable {
    private final List<IntAttached<BlockPos>> purchases = new ArrayList<IntAttached<BlockPos>>();
    private final UUID shopOwner;
    private final UUID shopNetwork;

    public ShoppingListItem.ShoppingList.Mutable(ShoppingListItem.ShoppingList list) {
        this.purchases.addAll(list.purchases);
        this.shopOwner = list.shopOwner;
        this.shopNetwork = list.shopNetwork;
    }

    public void addPurchases(BlockPos clothPos, int amount) {
        for (IntAttached<BlockPos> entry : this.purchases) {
            if (!clothPos.equals(entry.getValue())) continue;
            entry.setFirst((Object)((Integer)entry.getFirst() + amount));
            return;
        }
        this.purchases.add((IntAttached<BlockPos>)IntAttached.with((int)amount, (Object)clothPos));
    }

    public ShoppingListItem.ShoppingList toImmutable() {
        return new ShoppingListItem.ShoppingList(this.purchases, this.shopOwner, this.shopNetwork);
    }
}
