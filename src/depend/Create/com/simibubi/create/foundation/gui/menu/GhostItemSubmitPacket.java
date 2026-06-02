/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.foundation.gui.menu;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperCategoryMenu;
import com.simibubi.create.foundation.gui.menu.GhostItemMenu;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public record GhostItemSubmitPacket(ItemStack item, int slot) implements ServerboundPacketPayload
{
    public static final StreamCodec<RegistryFriendlyByteBuf, GhostItemSubmitPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)ItemStack.OPTIONAL_STREAM_CODEC, GhostItemSubmitPacket::item, (StreamCodec)ByteBufCodecs.INT, GhostItemSubmitPacket::slot, GhostItemSubmitPacket::new);

    public void handle(ServerPlayer player) {
        MenuBase menu;
        AbstractContainerMenu abstractContainerMenu = player.containerMenu;
        if (abstractContainerMenu instanceof GhostItemMenu) {
            menu = (GhostItemMenu)abstractContainerMenu;
            menu.ghostInventory.setStackInSlot(this.slot, this.item);
            menu.getSlot(36 + this.slot).setChanged();
        }
        if ((abstractContainerMenu = player.containerMenu) instanceof StockKeeperCategoryMenu) {
            menu = (StockKeeperCategoryMenu)abstractContainerMenu;
            if (this.item.isEmpty() || this.item.getItem() instanceof FilterItem) {
                ((StockKeeperCategoryMenu)menu).proxyInventory.setStackInSlot(this.slot, this.item);
                menu.getSlot(36 + this.slot).setChanged();
            }
        }
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.SUBMIT_GHOST_ITEM;
    }
}
