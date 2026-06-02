/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.LevelAccessor
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains;

import com.simibubi.create.AllPackets;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.TrainHUDUpdatePacket;
import com.simibubi.create.content.trains.entity.Train;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

public static class TrainHUDUpdatePacket.Serverbound
extends TrainHUDUpdatePacket
implements ServerboundPacketPayload {
    public static final StreamCodec<ByteBuf, TrainHUDUpdatePacket.Serverbound> STREAM_CODEC = TrainHUDUpdatePacket.codec(TrainHUDUpdatePacket.Serverbound::new);

    public TrainHUDUpdatePacket.Serverbound(Train train, Double sendThrottle) {
        this(train.id, sendThrottle, 0.0, 0);
    }

    private TrainHUDUpdatePacket.Serverbound(UUID trainId, @Nullable Double throttle, double speed, int fuelTicks) {
        super(trainId, throttle, speed, fuelTicks);
    }

    public void handle(ServerPlayer player) {
        Train train = Create.RAILWAYS.sided((LevelAccessor)player.level()).trains.get(this.trainId);
        if (train == null) {
            return;
        }
        if (this.throttle != null) {
            train.throttle = this.throttle;
        }
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.C_TRAIN_HUD;
    }
}
