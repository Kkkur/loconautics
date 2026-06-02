/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package com.simibubi.create.content.logistics.depot;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.depot.EjectorBlockEntity;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public record EjectorElytraPacket(BlockPos pos) implements ServerboundPacketPayload
{
    public static final StreamCodec<ByteBuf, EjectorElytraPacket> STREAM_CODEC = BlockPos.STREAM_CODEC.map(EjectorElytraPacket::new, EjectorElytraPacket::pos);

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.EJECTOR_ELYTRA;
    }

    public void handle(ServerPlayer player) {
        Level world = player.level();
        if (!world.isLoaded(this.pos)) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(this.pos);
        if (blockEntity instanceof EjectorBlockEntity) {
            ((EjectorBlockEntity)blockEntity).deployElytra((Player)player);
        }
    }
}
