/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.StreamCodec
 */
package com.simibubi.create.content.logistics.packagePort;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.packagePort.PackagePortTargetSelectionHandler;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;

public record PackagePortPlacementPacket.ClientBoundRequest(BlockPos pos) implements ClientboundPacketPayload
{
    public static final StreamCodec<ByteBuf, PackagePortPlacementPacket.ClientBoundRequest> STREAM_CODEC = BlockPos.STREAM_CODEC.map(PackagePortPlacementPacket.ClientBoundRequest::new, PackagePortPlacementPacket.ClientBoundRequest::pos);

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.S_PLACE_PACKAGE_PORT;
    }

    public void handle(LocalPlayer player) {
        PackagePortTargetSelectionHandler.flushSettings(this.pos);
    }
}
