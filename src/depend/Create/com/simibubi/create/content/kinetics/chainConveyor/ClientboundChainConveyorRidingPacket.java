/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.UUIDUtil
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.kinetics.chainConveyor;

import com.simibubi.create.AllPackets;
import com.simibubi.create.foundation.render.PlayerSkyhookRenderer;
import io.netty.buffer.ByteBuf;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record ClientboundChainConveyorRidingPacket(Collection<UUID> uuids) implements ClientboundPacketPayload
{
    public static final StreamCodec<ByteBuf, ClientboundChainConveyorRidingPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.collection(HashSet::new, (StreamCodec)UUIDUtil.STREAM_CODEC), ClientboundChainConveyorRidingPacket::uuids, ClientboundChainConveyorRidingPacket::new);

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CLIENTBOUND_CHAIN_CONVEYOR;
    }

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        PlayerSkyhookRenderer.updatePlayerList(this.uuids);
    }
}
