/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.IntAttached
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.contraptions.elevator;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.elevator.ElevatorContraption;
import io.netty.buffer.ByteBuf;
import java.util.List;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.IntAttached;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record ElevatorFloorListPacket(int entityId, List<IntAttached<Couple<String>>> floors) implements ClientboundPacketPayload
{
    public static final StreamCodec<ByteBuf, ElevatorFloorListPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.INT, ElevatorFloorListPacket::entityId, (StreamCodec)CatnipStreamCodecBuilders.list((StreamCodec)IntAttached.streamCodec((StreamCodec)Couple.streamCodec((StreamCodec)ByteBufCodecs.STRING_UTF8))), ElevatorFloorListPacket::floors, ElevatorFloorListPacket::new);

    public ElevatorFloorListPacket(AbstractContraptionEntity entity, List<IntAttached<Couple<String>>> floors) {
        this(entity.getId(), floors);
    }

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        Entity entityByID = player.clientLevel.getEntity(this.entityId);
        if (!(entityByID instanceof AbstractContraptionEntity)) {
            return;
        }
        AbstractContraptionEntity ace = (AbstractContraptionEntity)entityByID;
        Contraption contraption = ace.getContraption();
        if (!(contraption instanceof ElevatorContraption)) {
            return;
        }
        ElevatorContraption ec = (ElevatorContraption)contraption;
        ec.namesList = this.floors;
        ec.syncControlDisplays();
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.UPDATE_ELEVATOR_FLOORS;
    }

    public record RequestFloorList(int entityId) implements ServerboundPacketPayload
    {
        public static final StreamCodec<ByteBuf, RequestFloorList> STREAM_CODEC = ByteBufCodecs.INT.map(RequestFloorList::new, RequestFloorList::entityId);

        public RequestFloorList(AbstractContraptionEntity entity) {
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
}
