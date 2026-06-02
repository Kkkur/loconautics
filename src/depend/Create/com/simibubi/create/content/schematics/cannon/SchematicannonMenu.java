/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.SlotItemHandler
 */
package com.simibubi.create.content.schematics.cannon;

import com.simibubi.create.AllMenuTypes;
import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class SchematicannonMenu
extends MenuBase<SchematicannonBlockEntity> {
    public SchematicannonMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf buffer) {
        super(type, id, inv, buffer);
    }

    public SchematicannonMenu(MenuType<?> type, int id, Inventory inv, SchematicannonBlockEntity be) {
        super(type, id, inv, be);
    }

    public static SchematicannonMenu create(int id, Inventory inv, SchematicannonBlockEntity be) {
        return new SchematicannonMenu((MenuType)AllMenuTypes.SCHEMATICANNON.get(), id, inv, be);
    }

    @Override
    protected SchematicannonBlockEntity createOnClient(RegistryFriendlyByteBuf extraData) {
        ClientLevel world = Minecraft.getInstance().level;
        BlockEntity blockEntity = world.getBlockEntity(extraData.readBlockPos());
        if (blockEntity instanceof SchematicannonBlockEntity) {
            SchematicannonBlockEntity schematicannon = (SchematicannonBlockEntity)blockEntity;
            schematicannon.readClient(extraData.readNbt(), (HolderLookup.Provider)extraData.registryAccess());
            return schematicannon;
        }
        return null;
    }

    @Override
    protected void initAndReadInventory(SchematicannonBlockEntity contentHolder) {
    }

    @Override
    protected void addSlots() {
        int x = 0;
        int y = 0;
        this.addSlot((Slot)new SlotItemHandler((IItemHandler)((SchematicannonBlockEntity)this.contentHolder).inventory, 0, x + 15, y + 65));
        this.addSlot((Slot)new SlotItemHandler((IItemHandler)((SchematicannonBlockEntity)this.contentHolder).inventory, 1, x + 171, y + 65));
        this.addSlot((Slot)new SlotItemHandler((IItemHandler)((SchematicannonBlockEntity)this.contentHolder).inventory, 2, x + 134, y + 19));
        this.addSlot((Slot)new SlotItemHandler((IItemHandler)((SchematicannonBlockEntity)this.contentHolder).inventory, 3, x + 174, y + 19));
        this.addSlot((Slot)new SlotItemHandler((IItemHandler)((SchematicannonBlockEntity)this.contentHolder).inventory, 4, x + 15, y + 19));
        this.addPlayerSlots(37, 161);
    }

    @Override
    protected void saveData(SchematicannonBlockEntity contentHolder) {
    }

    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot clickedSlot = this.getSlot(index);
        if (!clickedSlot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = clickedSlot.getItem();
        if (index < 5) {
            this.moveItemStackTo(stack, 5, this.slots.size(), true);
        } else if (this.moveItemStackTo(stack, 0, 1, false) || this.moveItemStackTo(stack, 2, 3, false) || this.moveItemStackTo(stack, 4, 5, false)) {
            // empty if block
        }
        return ItemStack.EMPTY;
    }
}
