/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.ClickType
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemStackHandler
 *  net.neoforged.neoforge.items.SlotItemHandler
 */
package com.simibubi.create.content.logistics.filter;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllMenuTypes;
import com.simibubi.create.content.logistics.filter.AbstractFilterMenu;
import com.simibubi.create.content.logistics.filter.AttributeFilterWhitelistMode;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class AttributeFilterMenu
extends AbstractFilterMenu {
    AttributeFilterWhitelistMode whitelistMode;
    List<ItemAttribute.ItemAttributeEntry> selectedAttributes;

    public AttributeFilterMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public AttributeFilterMenu(MenuType<?> type, int id, Inventory inv, ItemStack stack) {
        super(type, id, inv, stack);
    }

    public static AttributeFilterMenu create(int id, Inventory inv, ItemStack stack) {
        return new AttributeFilterMenu((MenuType)AllMenuTypes.ATTRIBUTE_FILTER.get(), id, inv, stack);
    }

    public void appendSelectedAttribute(ItemAttribute itemAttribute, boolean inverted) {
        this.selectedAttributes.add(new ItemAttribute.ItemAttributeEntry(itemAttribute, inverted));
    }

    @Override
    protected void init(Inventory inv, ItemStack contentHolder) {
        super.init(inv, contentHolder);
        ItemStack stack = new ItemStack((ItemLike)Items.NAME_TAG);
        stack.set(DataComponents.CUSTOM_NAME, (Object)Component.literal((String)"Selected Tags").withStyle(new ChatFormatting[]{ChatFormatting.RESET, ChatFormatting.BLUE}));
        this.ghostInventory.setStackInSlot(1, stack);
    }

    @Override
    protected int getPlayerInventoryXOffset() {
        return 51;
    }

    @Override
    protected int getPlayerInventoryYOffset() {
        return 107;
    }

    @Override
    protected void addFilterSlots() {
        this.addSlot((Slot)new SlotItemHandler((IItemHandler)this.ghostInventory, 0, 16, 27));
        this.addSlot((Slot)new SlotItemHandler(this, (IItemHandler)this.ghostInventory, 1, 16, 62){

            public boolean mayPickup(Player playerIn) {
                return false;
            }
        });
    }

    @Override
    protected ItemStackHandler createGhostInventory() {
        return new ItemStackHandler(2);
    }

    @Override
    public void clearContents() {
        this.selectedAttributes.clear();
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        if (slotId == 37) {
            return;
        }
        super.clicked(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public boolean canDragTo(Slot slotIn) {
        if (slotIn.index == 37) {
            return false;
        }
        return super.canDragTo(slotIn);
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slotIn) {
        if (slotIn.index == 37) {
            return false;
        }
        return super.canTakeItemForPickAll(stack, slotIn);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        if (index == 37) {
            return ItemStack.EMPTY;
        }
        if (index == 36) {
            this.ghostInventory.setStackInSlot(37, ItemStack.EMPTY);
            return ItemStack.EMPTY;
        }
        if (index < 36) {
            Slot slot = (Slot)this.slots.get(index);
            ItemStack stackToInsert = slot.getItem();
            ItemStack copy = stackToInsert.copy();
            copy.setCount(1);
            this.ghostInventory.setStackInSlot(0, copy);
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected void initAndReadInventory(ItemStack filterItem) {
        super.initAndReadInventory(filterItem);
        this.selectedAttributes = new ArrayList<ItemAttribute.ItemAttributeEntry>();
        this.whitelistMode = (AttributeFilterWhitelistMode)((Object)filterItem.getOrDefault(AllDataComponents.ATTRIBUTE_FILTER_WHITELIST_MODE, (Object)AttributeFilterWhitelistMode.WHITELIST_DISJ));
        List attributes = (List)filterItem.getOrDefault(AllDataComponents.ATTRIBUTE_FILTER_MATCHED_ATTRIBUTES, List.of());
        this.selectedAttributes.addAll(attributes);
    }

    @Override
    protected void saveData(ItemStack filterItem) {
        filterItem.set(AllDataComponents.ATTRIBUTE_FILTER_WHITELIST_MODE, (Object)this.whitelistMode);
        ArrayList attributes = new ArrayList();
        this.selectedAttributes.forEach(at -> {
            if (at == null) {
                return;
            }
            attributes.add(new ItemAttribute.ItemAttributeEntry(at.attribute(), at.inverted()));
        });
        filterItem.set(AllDataComponents.ATTRIBUTE_FILTER_MATCHED_ATTRIBUTES, attributes);
        if (attributes.isEmpty() && this.whitelistMode == AttributeFilterWhitelistMode.WHITELIST_DISJ) {
            filterItem.remove(AllDataComponents.ATTRIBUTE_FILTER_MATCHED_ATTRIBUTES);
            filterItem.remove(AllDataComponents.ATTRIBUTE_FILTER_WHITELIST_MODE);
        }
    }
}
