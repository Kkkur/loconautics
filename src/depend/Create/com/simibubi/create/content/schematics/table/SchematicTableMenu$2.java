/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.SlotItemHandler
 */
package com.simibubi.create.content.schematics.table;

import com.simibubi.create.content.schematics.table.SchematicTableMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

class SchematicTableMenu.2
extends SlotItemHandler {
    SchematicTableMenu.2(SchematicTableMenu this$0, IItemHandler arg0, int arg1, int arg2, int arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public boolean mayPlace(ItemStack stack) {
        return false;
    }
}
