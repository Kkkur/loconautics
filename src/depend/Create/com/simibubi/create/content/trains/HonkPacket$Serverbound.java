/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.Holder
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.gameevent.GameEvent
 */
package com.simibubi.create.content.trains;

import com.simibubi.create.AllPackets;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.HonkPacket;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;

public static class HonkPacket.Serverbound
extends HonkPacket
implements ServerboundPacketPayload {
    public static final StreamCodec<ByteBuf, HonkPacket.Serverbound> STREAM_CODEC = HonkPacket.codec(HonkPacket.Serverbound::new);

    public HonkPacket.Serverbound(Train train, boolean isHonk) {
        this(train.id, isHonk);
    }

    private HonkPacket.Serverbound(UUID id, boolean isHonk) {
        super(id, isHonk);
    }

    public void handle(ServerPlayer player) {
        Train train = Create.RAILWAYS.sided((LevelAccessor)player.level()).trains.get(this.trainId);
        if (train == null) {
            return;
        }
        AllAdvancements.TRAIN_WHISTLE.awardTo((Player)player);
        CatnipServices.NETWORK.sendToAllClients((CustomPacketPayload)new HonkPacket.Clientbound(train, this.isHonk));
        CarriageContraptionEntity entity = train.carriages.get(0).anyAvailableEntity();
        if (entity == null) {
            entity = player;
        }
        player.level().gameEvent((Entity)entity, (Holder)GameEvent.RESONATE_15, player.position());
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.C_TRAIN_HONK;
    }
}
