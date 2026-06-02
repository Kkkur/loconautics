/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemStackHandler
 *  net.neoforged.neoforge.items.SlotItemHandler
 */
package com.simibubi.create.content.logistics.factoryBoard;

import com.simibubi.create.AllMenuTypes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.foundation.gui.menu.GhostItemMenu;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.BlockAndTintGetter;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class FactoryPanelSetItemMenu
extends GhostItemMenu<FactoryPanelBehaviour> {
    public FactoryPanelSetItemMenu(MenuType<?> type, int id, Inventory inv, FactoryPanelBehaviour contentHolder) {
        super(type, id, inv, contentHolder);
    }

    public FactoryPanelSetItemMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public static FactoryPanelSetItemMenu create(int id, Inventory inv, FactoryPanelBehaviour be) {
        return new FactoryPanelSetItemMenu((MenuType)AllMenuTypes.FACTORY_PANEL_SET_ITEM.get(), id, inv, be);
    }

    @Override
    protected ItemStackHandler createGhostInventory() {
        return new ItemStackHandler(1);
    }

    @Override
    protected boolean allowRepeats() {
        return true;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    protected FactoryPanelBehaviour createOnClient(RegistryFriendlyByteBuf extraData) {
        FactoryPanelPosition pos = (FactoryPanelPosition)FactoryPanelPosition.STREAM_CODEC.decode((Object)extraData);
        return FactoryPanelBehaviour.at((BlockAndTintGetter)Minecraft.getInstance().level, pos);
    }

    @Override
    protected void addSlots() {
        int playerX = 13;
        int playerY = 112;
        int slotX = 74;
        int slotY = 28;
        this.addPlayerSlots(playerX, playerY);
        this.addSlot((Slot)new SlotItemHandler((IItemHandler)this.ghostInventory, 0, slotX, slotY));
    }

    @Override
    protected void saveData(FactoryPanelBehaviour contentHolder) {
        if (!contentHolder.setFilter(this.ghostInventory.getStackInSlot(0))) {
            this.player.displayClientMessage((Component)CreateLang.translateDirect("logistics.filter.invalid_item", new Object[0]), true);
            AllSoundEvents.DENY.playOnServer(this.player.level(), (Vec3i)this.player.blockPosition(), 1.0f, 1.0f);
            return;
        }
        this.player.level().playSound(null, contentHolder.getPos(), SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.25f, 0.1f);
    }
}
