/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
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
import com.simibubi.create.content.schematics.table.SchematicTableBlockEntity;
import com.simibubi.create.content.schematics.table.SchematicTableMenu;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public record SchematicUploadPacket(int code, long size, String schematic, byte[] data) implements ServerboundPacketPayload
{
    public static final int BEGIN = 0;
    public static final int WRITE = 1;
    public static final int FINISH = 2;
    public static final StreamCodec<ByteBuf, SchematicUploadPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.VAR_INT, SchematicUploadPacket::code, (StreamCodec)ByteBufCodecs.VAR_LONG, SchematicUploadPacket::size, (StreamCodec)CatnipStreamCodecBuilders.nullable((StreamCodec)ByteBufCodecs.stringUtf8((int)256)), SchematicUploadPacket::schematic, (StreamCodec)CatnipStreamCodecBuilders.nullable((StreamCodec)ByteBufCodecs.byteArray((int)Integer.MAX_VALUE)), SchematicUploadPacket::data, SchematicUploadPacket::new);

    public static SchematicUploadPacket begin(String schematic, long size) {
        return new SchematicUploadPacket(0, size, schematic, null);
    }

    public static SchematicUploadPacket write(String schematic, byte[] data) {
        return new SchematicUploadPacket(1, 0L, schematic, data);
    }

    public static SchematicUploadPacket finish(String schematic) {
        return new SchematicUploadPacket(2, 0L, schematic, null);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.UPLOAD_SCHEMATIC;
    }

    public void handle(ServerPlayer player) {
        if (this.code == 0) {
            BlockPos pos = ((SchematicTableBlockEntity)((SchematicTableMenu)player.containerMenu).contentHolder).getBlockPos();
            Create.SCHEMATIC_RECEIVER.handleNewUpload(player, this.schematic, this.size, pos);
        }
        if (this.code == 1) {
            Create.SCHEMATIC_RECEIVER.handleWriteRequest(player, this.schematic, this.data);
        }
        if (this.code == 2) {
            Create.SCHEMATIC_RECEIVER.handleFinishedUpload(player, this.schematic);
        }
    }
}
