/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 */
package com.simibubi.create.content.contraptions.elevator;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.elevator.ElevatorContraption;
import com.simibubi.create.content.contraptions.elevator.ElevatorFloorListPacket;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public record ElevatorFloorListPacket.RequestFloorList(int entityId) implements ServerboundPacketPayload
{
    public static final StreamCodec<ByteBuf, ElevatorFloorListPacket.RequestFloorList> STREAM_CODEC = ByteBufCodecs.INT.map(ElevatorFloorListPacket.RequestFloorList::new, ElevatorFloorListPacket.RequestFloorList::entityId);

    public ElevatorFloorListPacket.RequestFloorList(AbstractContraptionEntity entity) {
        this(entity.getId());
    }

    public void handle(ServerPlayer sender) {
        Entity entityByID = sender.level().getEntity(this.entityId);
        if (!(entityByID instanceof AbstractContraptionEntity)) {
            return;
        }
        AbstractContraptionEntity ace = (AbstractContraptionEntity)entityByID;
        Contraption contraption = ace.getContraption();
        if (!(contraption instanceof ElevatorContraption)) {
            return;
        }
        ElevatorContraption ec = (ElevatorContraption)contraption;
        CatnipServices.NETWORK.sendToClient(sender, (CustomPacketPayload)new ElevatorFloorListPacket(ace, ec.namesList));
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.REQUEST_FLOOR_LIST;
    }
}
