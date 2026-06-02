/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.SlotItemHandler
 */
package com.simibubi.create.content.logistics.filter;

import com.simibubi.create.content.logistics.filter.AttributeFilterMenu;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

class AttributeFilterMenu.1
extends SlotItemHandler {
    AttributeFilterMenu.1(AttributeFilterMenu this$0, IItemHandler arg0, int arg1, int arg2, int arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public boolean mayPickup(Player playerIn) {
        return false;
    }
}
