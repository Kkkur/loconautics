/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.BlockAndTintGetter
 */
package com.simibubi.create.content.logistics.factoryBoard;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.BlockAndTintGetter;

public class FactoryPanelConnectionPacket
extends BlockEntityConfigurationPacket<FactoryPanelBlockEntity> {
    public static final StreamCodec<ByteBuf, FactoryPanelConnectionPacket> STREAM_CODEC = StreamCodec.composite(FactoryPanelPosition.STREAM_CODEC, packet -> packet.fromPos, FactoryPanelPosition.STREAM_CODEC, packet -> packet.toPos, (StreamCodec)ByteBufCodecs.BOOL, packet -> packet.relocate, FactoryPanelConnectionPacket::new);
    private final FactoryPanelPosition fromPos;
    private final FactoryPanelPosition toPos;
    private boolean relocate;

    public FactoryPanelConnectionPacket(FactoryPanelPosition fromPos, FactoryPanelPosition toPos, boolean relocate) {
        super(toPos.pos());
        this.fromPos = fromPos;
        this.toPos = toPos;
        this.relocate = relocate;
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CONNECT_FACTORY_PANEL;
    }

    @Override
    protected void applySettings(ServerPlayer player, FactoryPanelBlockEntity be) {
        FactoryPanelBehaviour behaviour = FactoryPanelBehaviour.at((BlockAndTintGetter)be.getLevel(), this.toPos);
        if (behaviour != null) {
            if (this.relocate) {
                behaviour.moveTo(this.fromPos, player);
            } else {
                behaviour.addConnection(this.fromPos);
            }
        }
    }

    @Override
    protected int maxRange() {
        return super.maxRange() * 2;
    }
}
