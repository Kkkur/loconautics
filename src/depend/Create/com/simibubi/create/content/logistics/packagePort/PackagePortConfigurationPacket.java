/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 */
package com.simibubi.create.content.logistics.packagePort;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.packagePort.PackagePortBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class PackagePortConfigurationPacket
extends BlockEntityConfigurationPacket<PackagePortBlockEntity> {
    public static final StreamCodec<ByteBuf, PackagePortConfigurationPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, packet -> packet.pos, (StreamCodec)ByteBufCodecs.STRING_UTF8, packet -> packet.newFilter, (StreamCodec)ByteBufCodecs.BOOL, packet -> packet.acceptPackages, PackagePortConfigurationPacket::new);
    private final String newFilter;
    private final boolean acceptPackages;

    public PackagePortConfigurationPacket(BlockPos pos, String newFilter, boolean acceptPackages) {
        super(pos);
        this.newFilter = newFilter;
        this.acceptPackages = acceptPackages;
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.PACKAGE_PORT_CONFIGURATION;
    }

    @Override
    protected void applySettings(ServerPlayer player, PackagePortBlockEntity be) {
        if (be.addressFilter.equals(this.newFilter) && be.acceptsPackages == this.acceptPackages) {
            return;
        }
        be.addressFilter = this.newFilter;
        be.acceptsPackages = this.acceptPackages;
        be.filterChanged();
        be.notifyUpdate();
    }
}
