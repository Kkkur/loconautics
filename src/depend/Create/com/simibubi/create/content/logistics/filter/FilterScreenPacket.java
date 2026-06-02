/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.logistics.filter;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.filter.AbstractFilterMenu;
import com.simibubi.create.content.logistics.filter.AttributeFilterMenu;
import com.simibubi.create.content.logistics.filter.AttributeFilterWhitelistMode;
import com.simibubi.create.content.logistics.filter.FilterMenu;
import com.simibubi.create.content.logistics.filter.PackageFilterMenu;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record FilterScreenPacket(Option option, @Nullable CompoundTag data) implements ServerboundPacketPayload
{
    public static final StreamCodec<ByteBuf, FilterScreenPacket> STREAM_CODEC = StreamCodec.composite(Option.STREAM_CODEC, FilterScreenPacket::option, (StreamCodec)CatnipStreamCodecBuilders.nullable((StreamCodec)ByteBufCodecs.COMPOUND_TAG), FilterScreenPacket::data, FilterScreenPacket::new);

    public FilterScreenPacket(Option option) {
        this(option, null);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CONFIGURE_FILTER;
    }

    public void handle(ServerPlayer player) {
        AbstractFilterMenu c;
        CompoundTag tag = this.data == null ? new CompoundTag() : this.data;
        AbstractContainerMenu abstractContainerMenu = player.containerMenu;
        if (abstractContainerMenu instanceof FilterMenu) {
            c = (FilterMenu)abstractContainerMenu;
            if (this.option == Option.WHITELIST) {
                ((FilterMenu)c).blacklist = false;
            }
            if (this.option == Option.BLACKLIST) {
                ((FilterMenu)c).blacklist = true;
            }
            if (this.option == Option.RESPECT_DATA) {
                ((FilterMenu)c).respectNBT = true;
            }
            if (this.option == Option.IGNORE_DATA) {
                ((FilterMenu)c).respectNBT = false;
            }
            if (this.option == Option.UPDATE_FILTER_ITEM) {
                ((FilterMenu)c).ghostInventory.setStackInSlot(tag.getInt("Slot"), ItemStack.parseOptional((HolderLookup.Provider)player.registryAccess(), (CompoundTag)tag.getCompound("Item")));
            }
        }
        if ((abstractContainerMenu = player.containerMenu) instanceof AttributeFilterMenu) {
            c = (AttributeFilterMenu)abstractContainerMenu;
            if (this.option == Option.WHITELIST) {
                ((AttributeFilterMenu)c).whitelistMode = AttributeFilterWhitelistMode.WHITELIST_DISJ;
            }
            if (this.option == Option.WHITELIST2) {
                ((AttributeFilterMenu)c).whitelistMode = AttributeFilterWhitelistMode.WHITELIST_CONJ;
            }
            if (this.option == Option.BLACKLIST) {
                ((AttributeFilterMenu)c).whitelistMode = AttributeFilterWhitelistMode.BLACKLIST;
            }
            if (this.option == Option.ADD_TAG) {
                ((AttributeFilterMenu)c).appendSelectedAttribute(ItemAttribute.loadStatic(this.data, (HolderLookup.Provider)player.registryAccess()), false);
            }
            if (this.option == Option.ADD_INVERTED_TAG) {
                ((AttributeFilterMenu)c).appendSelectedAttribute(ItemAttribute.loadStatic(this.data, (HolderLookup.Provider)player.registryAccess()), true);
            }
        }
        if ((abstractContainerMenu = player.containerMenu) instanceof PackageFilterMenu) {
            c = (PackageFilterMenu)abstractContainerMenu;
            if (this.option == Option.UPDATE_ADDRESS) {
                ((PackageFilterMenu)c).address = tag.getString("Address");
            }
        }
    }

    public static enum Option {
        WHITELIST,
        WHITELIST2,
        BLACKLIST,
        RESPECT_DATA,
        IGNORE_DATA,
        UPDATE_FILTER_ITEM,
        ADD_TAG,
        ADD_INVERTED_TAG,
        UPDATE_ADDRESS;

        public static final StreamCodec<ByteBuf, Option> STREAM_CODEC;

        static {
            STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(Option.class);
        }
    }
}
