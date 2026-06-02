/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.logistics.stockTicker;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import java.util.List;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class StockKeeperCategoryEditPacket
extends BlockEntityConfigurationPacket<StockTickerBlockEntity> {
    public static final StreamCodec<RegistryFriendlyByteBuf, StockKeeperCategoryEditPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, p -> p.pos, (StreamCodec)ItemStack.OPTIONAL_LIST_STREAM_CODEC, p -> p.schedule, StockKeeperCategoryEditPacket::new);
    private final List<ItemStack> schedule;

    public StockKeeperCategoryEditPacket(BlockPos pos, List<ItemStack> schedule) {
        super(pos);
        this.schedule = schedule;
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CONFIGURE_STOCK_KEEPER_CATEGORIES;
    }

    @Override
    protected void applySettings(ServerPlayer player, StockTickerBlockEntity be) {
        be.categories = this.schedule;
        be.notifyUpdate();
    }
}
