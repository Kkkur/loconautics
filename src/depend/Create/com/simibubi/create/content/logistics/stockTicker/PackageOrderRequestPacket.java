/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 */
package com.simibubi.create.content.logistics.stockTicker;

import com.simibubi.create.AllPackets;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import com.simibubi.create.content.logistics.packagerLink.WiFiEffectPacket;
import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterBlock;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class PackageOrderRequestPacket
extends BlockEntityConfigurationPacket<StockTickerBlockEntity> {
    public static final StreamCodec<RegistryFriendlyByteBuf, PackageOrderRequestPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, packet -> packet.pos, PackageOrderWithCrafts.STREAM_CODEC, packet -> packet.order, (StreamCodec)ByteBufCodecs.STRING_UTF8, packet -> packet.address, (StreamCodec)ByteBufCodecs.BOOL, packet -> packet.encodeRequester, PackageOrderRequestPacket::new);
    private final PackageOrderWithCrafts order;
    private final String address;
    private final boolean encodeRequester;

    public PackageOrderRequestPacket(BlockPos pos, PackageOrderWithCrafts order, String address, boolean encodeRequester) {
        super(pos);
        this.order = order;
        this.address = address;
        this.encodeRequester = encodeRequester;
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.LOGISTICS_PACKAGE_REQUEST;
    }

    @Override
    protected void applySettings(ServerPlayer player, StockTickerBlockEntity be) {
        if (this.encodeRequester) {
            if (!this.order.isEmpty()) {
                AllSoundEvents.CONFIRM.playOnServer(be.getLevel(), (Vec3i)this.pos);
            }
            player.closeContainer();
            RedstoneRequesterBlock.programRequester(player, be, this.order, this.address);
            return;
        }
        if (!this.order.isEmpty()) {
            AllSoundEvents.STOCK_TICKER_REQUEST.playOnServer(be.getLevel(), (Vec3i)this.pos);
            AllAdvancements.STOCK_TICKER.awardTo((Player)player);
            WiFiEffectPacket.send(player.level(), this.pos);
        }
        be.broadcastPackageRequest(LogisticallyLinkedBehaviour.RequestType.PLAYER, this.order, null, this.address);
    }
}
