/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.ClickType
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.neoforge.items.IItemHandler
 */
package com.simibubi.create.content.equipment.toolbox;

import com.simibubi.create.AllMenuTypes;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.equipment.toolbox.ToolboxInventory;
import com.simibubi.create.content.equipment.toolbox.ToolboxSlot;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.animatedContainer.AnimatedContainerBehaviour;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;

public class ToolboxMenu
extends MenuBase<ToolboxBlockEntity> {
    public boolean renderPass;

    public ToolboxMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public ToolboxMenu(MenuType<?> type, int id, Inventory inv, ToolboxBlockEntity be) {
        super(type, id, inv, be);
        BlockEntityBehaviour.get(be, AnimatedContainerBehaviour.TYPE).startOpen(this.player);
    }

    public static ToolboxMenu create(int id, Inventory inv, ToolboxBlockEntity be) {
        return new ToolboxMenu((MenuType)AllMenuTypes.TOOLBOX.get(), id, inv, be);
    }

    @Override
    protected ToolboxBlockEntity createOnClient(RegistryFriendlyByteBuf extraData) {
        BlockPos readBlockPos = extraData.readBlockPos();
        CompoundTag readNbt = extraData.readNbt();
        ClientLevel world = Minecraft.getInstance().level;
        BlockEntity blockEntity = world.getBlockEntity(readBlockPos);
        if (blockEntity instanceof ToolboxBlockEntity) {
            ToolboxBlockEntity toolbox = (ToolboxBlockEntity)blockEntity;
            toolbox.readClient(readNbt, (HolderLookup.Provider)extraData.registryAccess());
            return toolbox;
        }
        return null;
    }

    public ItemStack quickMoveStack(Player player, int index) {
        boolean success;
        Slot clickedSlot = this.getSlot(index);
        if (!clickedSlot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = clickedSlot.getItem();
        int size = ((ToolboxBlockEntity)this.contentHolder).inventory.getSlots();
        if (index < size) {
            success = !this.moveItemStackTo(stack, size, this.slots.size(), true);
            ((ToolboxBlockEntity)this.contentHolder).inventory.onContentsChanged(index);
        } else {
            success = !this.moveItemStackTo(stack, 0, size, false);
        }
        return success ? ItemStack.EMPTY : stack;
    }

    @Override
    protected void initAndReadInventory(ToolboxBlockEntity contentHolder) {
    }

    public void clicked(int index, int flags, ClickType type, Player player) {
        int size = ((ToolboxBlockEntity)this.contentHolder).inventory.getSlots();
        if (index >= 0 && index < size) {
            int subIndex;
            ItemStack itemInClickedSlot = this.getSlot(index).getItem();
            ItemStack carried = this.getCarried();
            if (type == ClickType.PICKUP && !carried.isEmpty() && !itemInClickedSlot.isEmpty() && ToolboxInventory.canItemsShareCompartment(itemInClickedSlot, carried) && (subIndex = index % 4) != 3) {
                this.clicked(index - subIndex + 4 - 1, flags, type, player);
                return;
            }
            if (type == ClickType.PICKUP && carried.isEmpty() && itemInClickedSlot.isEmpty() && !player.level().isClientSide) {
                ((ToolboxBlockEntity)this.contentHolder).inventory.filters.set(index / 4, ItemStack.EMPTY);
                ((ToolboxBlockEntity)this.contentHolder).sendData();
            }
        }
        super.clicked(index, flags, type, player);
    }

    public boolean canDragTo(Slot slot) {
        return slot.index > ((ToolboxBlockEntity)this.contentHolder).inventory.getSlots() && super.canDragTo(slot);
    }

    public ItemStack getFilter(int compartment) {
        return ((ToolboxBlockEntity)this.contentHolder).inventory.filters.get(compartment);
    }

    public int totalCountInCompartment(int compartment) {
        int count = 0;
        int baseSlot = compartment * 4;
        for (int i = 0; i < 4; ++i) {
            count += this.getSlot(baseSlot + i).getItem().getCount();
        }
        return count;
    }

    @Override
    protected void addSlots() {
        ToolboxInventory inventory = ((ToolboxBlockEntity)this.contentHolder).inventory;
        int x = 79;
        int y = 37;
        int[] xOffsets = new int[]{x, x + 33, x + 66, x + 66 + 6, x + 66, x + 33, x, x - 6};
        int[] yOffsets = new int[]{y, y - 6, y, y + 33, y + 66, y + 66 + 6, y + 66, y + 33};
        for (int compartment = 0; compartment < 8; ++compartment) {
            int baseIndex = compartment * 4;
            this.addSlot((Slot)new ToolboxSlot(this, (IItemHandler)inventory, baseIndex, xOffsets[compartment], yOffsets[compartment], true));
            for (int i = 1; i < 4; ++i) {
                this.addSlot((Slot)new ToolboxSlot(this, (IItemHandler)inventory, baseIndex + i, -10000, -10000, false));
            }
        }
        this.addPlayerSlots(8, 165);
    }

    @Override
    protected void saveData(ToolboxBlockEntity contentHolder) {
    }

    @Override
    public void removed(Player playerIn) {
        super.removed(playerIn);
        if (!playerIn.level().isClientSide) {
            BlockEntityBehaviour.get((BlockEntity)this.contentHolder, AnimatedContainerBehaviour.TYPE).stopOpen(playerIn);
        }
    }
}
