/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.codec.StreamCodec
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
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
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public static class TrainHUDUpdatePacket.Clientbound
extends TrainHUDUpdatePacket
implements ClientboundPacketPayload {
    public static final StreamCodec<ByteBuf, TrainHUDUpdatePacket.Clientbound> STREAM_CODEC = TrainHUDUpdatePacket.codec(TrainHUDUpdatePacket.Clientbound::new);

    public TrainHUDUpdatePacket.Clientbound(Train train) {
        this(train.id, train.throttle, TrainHUDUpdatePacket.Clientbound.nonStalledSpeed(train), train.fuelTicks);
    }

    private TrainHUDUpdatePacket.Clientbound(UUID trainId, @Nullable Double throttle, double speed, int fuelTicks) {
        super(trainId, throttle, speed, fuelTicks);
    }

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        Train train = Create.RAILWAYS.sided(null).trains.get(this.trainId);
        if (train == null) {
            return;
        }
        if (this.throttle != null) {
            train.throttle = this.throttle;
        }
        train.speed = this.speed;
        train.fuelTicks = this.fuelTicks;
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.S_TRAIN_HUD;
    }

    private static double nonStalledSpeed(Train train) {
        return train.speedBeforeStall == null ? train.speed : train.speedBeforeStall;
    }
}
