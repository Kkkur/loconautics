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
 *  net.minecraft.world.entity.player.Player
 */
package com.simibubi.create.content.kinetics.chainConveyor;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ServerChainConveyorHandler;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import com.simibubi.create.infrastructure.config.AllConfigs;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class ServerboundChainConveyorRidingPacket
extends BlockEntityConfigurationPacket<ChainConveyorBlockEntity> {
    public static final StreamCodec<ByteBuf, ServerboundChainConveyorRidingPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, packet -> packet.pos, (StreamCodec)ByteBufCodecs.BOOL, packet -> packet.stop, ServerboundChainConveyorRidingPacket::new);
    private final boolean stop;

    public ServerboundChainConveyorRidingPacket(BlockPos pos, boolean stop) {
        super(pos);
        this.stop = stop;
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CHAIN_CONVEYOR_RIDING;
    }

    @Override
    protected int maxRange() {
        return (Integer)AllConfigs.server().kinetics.maxChainConveyorLength.get() * 2;
    }

    @Override
    protected void applySettings(ServerPlayer sender, ChainConveyorBlockEntity be) {
        sender.fallDistance = 0.0f;
        sender.connection.aboveGroundTickCount = 0;
        sender.connection.aboveGroundVehicleTickCount = 0;
        if (this.stop) {
            ServerChainConveyorHandler.handleStopRidingPacket((Player)sender);
        } else {
            ServerChainConveyorHandler.handleTTLPacket((Player)sender);
        }
    }
}
