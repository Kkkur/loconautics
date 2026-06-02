/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.SlotItemHandler
 */
package com.simibubi.create.content.schematics.table;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllMenuTypes;
import com.simibubi.create.content.schematics.table.SchematicTableBlockEntity;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class SchematicTableMenu
extends MenuBase<SchematicTableBlockEntity> {
    private Slot inputSlot;
    private Slot outputSlot;

    public SchematicTableMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public SchematicTableMenu(MenuType<?> type, int id, Inventory inv, SchematicTableBlockEntity be) {
        super(type, id, inv, be);
    }

    public static SchematicTableMenu create(int id, Inventory inv, SchematicTableBlockEntity be) {
        return new SchematicTableMenu((MenuType)AllMenuTypes.SCHEMATIC_TABLE.get(), id, inv, be);
    }

    public boolean canWrite() {
        return this.inputSlot.hasItem() && !this.outputSlot.hasItem();
    }

    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot clickedSlot = this.getSlot(index);
        if (!clickedSlot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = clickedSlot.getItem();
        if (index < 2) {
            this.moveItemStackTo(stack, 2, this.slots.size(), true);
        } else {
            this.moveItemStackTo(stack, 0, 1, false);
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected SchematicTableBlockEntity createOnClient(RegistryFriendlyByteBuf extraData) {
        ClientLevel world = Minecraft.getInstance().level;
        BlockEntity blockEntity = world.getBlockEntity(extraData.readBlockPos());
        if (blockEntity instanceof SchematicTableBlockEntity) {
            SchematicTableBlockEntity schematicTable = (SchematicTableBlockEntity)blockEntity;
            schematicTable.readClient(extraData.readNbt(), (HolderLookup.Provider)extraData.registryAccess());
            return schematicTable;
        }
        return null;
    }

    @Override
    protected void initAndReadInventory(SchematicTableBlockEntity contentHolder) {
    }

    @Override
    protected void addSlots() {
        this.inputSlot = new SlotItemHandler(this, (IItemHandler)((SchematicTableBlockEntity)this.contentHolder).inventory, 0, 21, 59){

            public boolean mayPlace(ItemStack stack) {
                return AllItems.EMPTY_SCHEMATIC.isIn(stack) || AllItems.SCHEMATIC_AND_QUILL.isIn(stack) || AllItems.SCHEMATIC.isIn(stack);
            }
        };
        this.outputSlot = new SlotItemHandler(this, (IItemHandler)((SchematicTableBlockEntity)this.contentHolder).inventory, 1, 166, 59){

            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        };
        this.addSlot(this.inputSlot);
        this.addSlot(this.outputSlot);
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot((Container)this.player.getInventory(), col + row * 9 + 9, 38 + col * 18, 107 + row * 18));
            }
        }
        for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
            this.addSlot(new Slot((Container)this.player.getInventory(), hotbarSlot, 38 + hotbarSlot * 18, 165));
        }
    }

    @Override
    protected void saveData(SchematicTableBlockEntity contentHolder) {
    }
}
