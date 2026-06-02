/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.logistics.stockTicker;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import java.util.List;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record LogisticalStockResponsePacket(boolean lastPacket, BlockPos pos, List<BigItemStack> items) implements ClientboundPacketPayload
{
    public static final StreamCodec<RegistryFriendlyByteBuf, LogisticalStockResponsePacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.BOOL, LogisticalStockResponsePacket::lastPacket, (StreamCodec)BlockPos.STREAM_CODEC, LogisticalStockResponsePacket::pos, (StreamCodec)CatnipStreamCodecBuilders.list(BigItemStack.STREAM_CODEC), LogisticalStockResponsePacket::items, LogisticalStockResponsePacket::new);

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.LOGISTICS_STOCK_RESPONSE;
    }

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(this.pos);
        if (blockEntity instanceof StockTickerBlockEntity) {
            StockTickerBlockEntity stbe = (StockTickerBlockEntity)blockEntity;
            stbe.receiveStockPacket(this.items, this.lastPacket);
        }
    }
}
