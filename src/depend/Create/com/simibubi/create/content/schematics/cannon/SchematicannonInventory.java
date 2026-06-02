/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.neoforged.neoforge.items.ItemStackHandler
 */
package com.simibubi.create.content.schematics.cannon;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.ItemStackHandler;

public class SchematicannonInventory
extends ItemStackHandler {
    private final SchematicannonBlockEntity blockEntity;

    public SchematicannonInventory(SchematicannonBlockEntity blockEntity) {
        super(5);
        this.blockEntity = blockEntity;
    }

    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        this.blockEntity.setChanged();
    }

    public boolean isItemValid(int slot, ItemStack stack) {
        switch (slot) {
            case 0: {
                return AllItems.SCHEMATIC.isIn(stack);
            }
            case 1: {
                return false;
            }
            case 2: {
                return AllBlocks.CLIPBOARD.isIn(stack) || stack.is(Items.BOOK) || stack.is(Items.WRITTEN_BOOK);
            }
            case 3: {
                return false;
            }
            case 4: {
                return stack.is(Items.GUNPOWDER);
            }
        }
        return super.isItemValid(slot, stack);
    }
}
