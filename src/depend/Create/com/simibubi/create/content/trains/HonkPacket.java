/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.Holder
 *  net.minecraft.core.UUIDUtil
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.gameevent.GameEvent
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.trains;

import com.simibubi.create.AllPackets;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import java.util.function.BiFunction;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public abstract class HonkPacket
implements CustomPacketPayload {
    protected final UUID trainId;
    protected final boolean isHonk;

    private HonkPacket(UUID trainId, boolean isHonk) {
        this.trainId = trainId;
        this.isHonk = isHonk;
    }

    private static <T extends HonkPacket> StreamCodec<ByteBuf, T> codec(BiFunction<UUID, Boolean, T> factory) {
        return StreamCodec.composite((StreamCodec)UUIDUtil.STREAM_CODEC, packet -> packet.trainId, (StreamCodec)ByteBufCodecs.BOOL, packet -> packet.isHonk, factory);
    }

    public static class Serverbound
    extends HonkPacket
    implements ServerboundPacketPayload {
        public static final StreamCodec<ByteBuf, Serverbound> STREAM_CODEC = HonkPacket.codec(Serverbound::new);

        public Serverbound(Train train, boolean isHonk) {
            this(train.id, isHonk);
        }

        private Serverbound(UUID id, boolean isHonk) {
            super(id, isHonk);
        }

        public void handle(ServerPlayer player) {
            Train train = Create.RAILWAYS.sided((LevelAccessor)player.level()).trains.get(this.trainId);
            if (train == null) {
                return;
            }
            AllAdvancements.TRAIN_WHISTLE.awardTo((Player)player);
            CatnipServices.NETWORK.sendToAllClients((CustomPacketPayload)new Clientbound(train, this.isHonk));
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

    public static class Clientbound
    extends HonkPacket
    implements ClientboundPacketPayload {
        public static final StreamCodec<ByteBuf, Clientbound> STREAM_CODEC = HonkPacket.codec(Clientbound::new);

        public Clientbound(Train train, boolean isHonk) {
            this(train.id, isHonk);
        }

        private Clientbound(UUID id, boolean isHonk) {
            super(id, isHonk);
        }

        @OnlyIn(value=Dist.CLIENT)
        public void handle(LocalPlayer player) {
            Train train = Create.RAILWAYS.sided(null).trains.get(this.trainId);
            if (train == null) {
                return;
            }
            train.honkTicks = this.isHonk ? (train.honkTicks == 0 ? 20 : 13) : (train.honkTicks > 5 ? 6 : 0);
        }

        public BasePacketPayload.PacketTypeProvider getTypeProvider() {
            return AllPackets.S_TRAIN_HONK;
        }
    }
}
