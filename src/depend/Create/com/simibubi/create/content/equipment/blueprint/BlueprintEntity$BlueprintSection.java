/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.neoforged.neoforge.items.ItemStackHandler
 */
package com.simibubi.create.content.equipment.blueprint;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.blueprint.BlueprintItem;
import com.simibubi.create.content.equipment.blueprint.BlueprintMenu;
import com.simibubi.create.foundation.utility.IInteractionChecker;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;

class BlueprintEntity.BlueprintSection
implements MenuProvider,
IInteractionChecker {
    int index;
    Couple<ItemStack> cachedDisplayItems;
    public boolean inferredIcon = false;

    public BlueprintEntity.BlueprintSection(int index) {
        this.index = index;
    }

    public Couple<ItemStack> getDisplayItems() {
        if (this.cachedDisplayItems != null) {
            return this.cachedDisplayItems;
        }
        ItemStackHandler items = this.getItems();
        this.cachedDisplayItems = Couple.create((Object)items.getStackInSlot(9), (Object)items.getStackInSlot(10));
        return this.cachedDisplayItems;
    }

    public ItemStackHandler getItems() {
        ItemStackHandler newInv = new ItemStackHandler(11);
        CompoundTag list = BlueprintEntity.this.getOrCreateRecipeCompound();
        CompoundTag invNBT = list.getCompound("" + this.index);
        this.inferredIcon = list.getBoolean("InferredIcon");
        if (!invNBT.isEmpty()) {
            newInv.deserializeNBT((HolderLookup.Provider)BlueprintEntity.this.registryAccess(), invNBT);
        }
        return newInv;
    }

    public void save(ItemStackHandler inventory) {
        CompoundTag list = BlueprintEntity.this.getOrCreateRecipeCompound();
        list.put("" + this.index, (Tag)inventory.serializeNBT((HolderLookup.Provider)BlueprintEntity.this.registryAccess()));
        list.putBoolean("InferredIcon", this.inferredIcon);
        this.cachedDisplayItems = null;
        if (!BlueprintEntity.this.level().isClientSide) {
            BlueprintEntity.this.syncPersistentDataWithTracking((Entity)BlueprintEntity.this);
        }
    }

    public boolean isEntityAlive() {
        return BlueprintEntity.this.isAlive();
    }

    public Level getBlueprintWorld() {
        return BlueprintEntity.this.level();
    }

    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return BlueprintMenu.create(id, inv, this);
    }

    public Component getDisplayName() {
        return ((BlueprintItem)((Object)AllItems.CRAFTING_BLUEPRINT.get())).getDescription();
    }

    @Override
    public boolean canPlayerUse(Player player) {
        return BlueprintEntity.this.canPlayerUse(player);
    }
}
