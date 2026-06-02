/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.inventory.TransientCraftingContainer
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.level.Level
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemStackHandler
 *  net.neoforged.neoforge.items.SlotItemHandler
 */
package com.simibubi.create.content.equipment.blueprint;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllMenuTypes;
import com.simibubi.create.content.equipment.blueprint.BlueprintEntity;
import com.simibubi.create.foundation.gui.menu.GhostItemMenu;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class BlueprintMenu
extends GhostItemMenu<BlueprintEntity.BlueprintSection> {
    public BlueprintMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public BlueprintMenu(MenuType<?> type, int id, Inventory inv, BlueprintEntity.BlueprintSection section) {
        super(type, id, inv, section);
    }

    public static BlueprintMenu create(int id, Inventory inv, BlueprintEntity.BlueprintSection section) {
        return new BlueprintMenu((MenuType)AllMenuTypes.CRAFTING_BLUEPRINT.get(), id, inv, section);
    }

    @Override
    protected boolean allowRepeats() {
        return true;
    }

    @Override
    protected void addSlots() {
        this.addPlayerSlots(8, 131);
        int x = 29;
        int y = 21;
        int index = 0;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                this.addSlot((Slot)new BlueprintCraftSlot((IItemHandler)this.ghostInventory, index++, x + col * 18, y + row * 18));
            }
        }
        this.addSlot((Slot)new BlueprintCraftSlot((IItemHandler)this.ghostInventory, index++, 123, 40));
        this.addSlot((Slot)new SlotItemHandler((IItemHandler)this.ghostInventory, index++, 135, 57));
    }

    public void onCraftMatrixChanged() {
        Level level = ((BlueprintEntity.BlueprintSection)this.contentHolder).getBlueprintWorld();
        if (level.isClientSide) {
            return;
        }
        ServerPlayer serverplayerentity = (ServerPlayer)this.player;
        BlueprintCraftingInventory craftingInventory = new BlueprintCraftingInventory(this, this.ghostInventory);
        Optional optional = this.player.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, (RecipeInput)craftingInventory.asCraftInput(), this.player.getCommandSenderWorld());
        if (!optional.isPresent()) {
            if (this.ghostInventory.getStackInSlot(9).isEmpty()) {
                return;
            }
            if (!((BlueprintEntity.BlueprintSection)this.contentHolder).inferredIcon) {
                return;
            }
            this.ghostInventory.setStackInSlot(9, ItemStack.EMPTY);
            serverplayerentity.connection.send((Packet)new ClientboundContainerSetSlotPacket(this.containerId, this.incrementStateId(), 45, ItemStack.EMPTY));
            ((BlueprintEntity.BlueprintSection)this.contentHolder).inferredIcon = false;
            return;
        }
        CraftingRecipe icraftingrecipe = (CraftingRecipe)((RecipeHolder)optional.get()).value();
        ItemStack itemstack = icraftingrecipe.assemble((RecipeInput)craftingInventory.asCraftInput(), (HolderLookup.Provider)level.registryAccess());
        this.ghostInventory.setStackInSlot(9, itemstack);
        ((BlueprintEntity.BlueprintSection)this.contentHolder).inferredIcon = true;
        ItemStack toSend = itemstack.copy();
        toSend.set(AllDataComponents.INFERRED_FROM_RECIPE, (Object)true);
        serverplayerentity.connection.send((Packet)new ClientboundContainerSetSlotPacket(this.containerId, this.incrementStateId(), 45, toSend));
    }

    public void setItem(int slotId, int stateId, ItemStack stack) {
        if (slotId == 45) {
            ((BlueprintEntity.BlueprintSection)this.contentHolder).inferredIcon = (Boolean)stack.getOrDefault(AllDataComponents.INFERRED_FROM_RECIPE, (Object)false);
            stack.remove(AllDataComponents.INFERRED_FROM_RECIPE);
        }
        super.setItem(slotId, stateId, stack);
    }

    @Override
    protected ItemStackHandler createGhostInventory() {
        return ((BlueprintEntity.BlueprintSection)this.contentHolder).getItems();
    }

    @Override
    protected void initAndReadInventory(BlueprintEntity.BlueprintSection contentHolder) {
        super.initAndReadInventory(contentHolder);
    }

    @Override
    protected void saveData(BlueprintEntity.BlueprintSection contentHolder) {
        contentHolder.save(this.ghostInventory);
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    protected BlueprintEntity.BlueprintSection createOnClient(RegistryFriendlyByteBuf extraData) {
        int entityID = extraData.readVarInt();
        int section = extraData.readVarInt();
        Entity entityByID = Minecraft.getInstance().level.getEntity(entityID);
        if (!(entityByID instanceof BlueprintEntity)) {
            return null;
        }
        BlueprintEntity blueprintEntity = (BlueprintEntity)entityByID;
        BlueprintEntity.BlueprintSection blueprintSection = blueprintEntity.getSection(section);
        return blueprintSection;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.contentHolder != null && ((BlueprintEntity.BlueprintSection)this.contentHolder).canPlayerUse(player);
    }

    class BlueprintCraftSlot
    extends SlotItemHandler {
        private int index;

        public BlueprintCraftSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
            this.index = index;
        }

        public void setChanged() {
            super.setChanged();
            if (this.index == 9 && this.hasItem() && !((BlueprintEntity.BlueprintSection)BlueprintMenu.this.contentHolder).getBlueprintWorld().isClientSide) {
                ((BlueprintEntity.BlueprintSection)BlueprintMenu.this.contentHolder).inferredIcon = false;
                ServerPlayer serverplayerentity = (ServerPlayer)BlueprintMenu.this.player;
                serverplayerentity.connection.send((Packet)new ClientboundContainerSetSlotPacket(BlueprintMenu.this.containerId, BlueprintMenu.this.incrementStateId(), 45, this.getItem()));
            }
            if (this.index < 9) {
                BlueprintMenu.this.onCraftMatrixChanged();
            }
        }
    }

    static class BlueprintCraftingInventory
    extends TransientCraftingContainer {
        public BlueprintCraftingInventory(AbstractContainerMenu menu, ItemStackHandler items) {
            super(menu, 3, 3);
            for (int y = 0; y < 3; ++y) {
                for (int x = 0; x < 3; ++x) {
                    ItemStack stack = items.getStackInSlot(y * 3 + x);
                    this.setItem(y * 3 + x, stack == null ? ItemStack.EMPTY : stack.copy());
                }
            }
        }
    }
}
