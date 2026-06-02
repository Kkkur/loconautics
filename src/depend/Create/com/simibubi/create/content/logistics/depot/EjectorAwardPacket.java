/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 */
package com.simibubi.create.content.logistics.depot;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.depot.EjectorBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class EjectorAwardPacket
extends BlockEntityConfigurationPacket<EjectorBlockEntity> {
    public static final StreamCodec<ByteBuf, EjectorAwardPacket> STREAM_CODEC = BlockPos.STREAM_CODEC.map(EjectorAwardPacket::new, packet -> packet.pos);

    public EjectorAwardPacket(BlockPos pos) {
        super(pos);
    }

    @Override
    protected void applySettings(ServerPlayer player, EjectorBlockEntity be) {
        AllAdvancements.EJECTOR_MAXED.awardTo((Player)player);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.EJECTOR_AWARD;
    }
}
