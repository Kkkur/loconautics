/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 */
package com.simibubi.create.content.schematics.packet;

import com.simibubi.create.AllPackets;
import com.simibubi.create.Create;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public record InstantSchematicPacket(String name, BlockPos origin, BlockPos bounds) implements ServerboundPacketPayload
{
    public static final StreamCodec<ByteBuf, InstantSchematicPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.STRING_UTF8, InstantSchematicPacket::name, (StreamCodec)BlockPos.STREAM_CODEC, InstantSchematicPacket::origin, (StreamCodec)BlockPos.STREAM_CODEC, InstantSchematicPacket::bounds, InstantSchematicPacket::new);

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.INSTANT_SCHEMATIC;
    }

    public void handle(ServerPlayer player) {
        Create.SCHEMATIC_RECEIVER.handleInstantSchematic(player, this.name, player.level(), this.origin, this.bounds);
    }
}
