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
 */
package com.simibubi.create.content.trains;

import com.simibubi.create.AllPackets;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.HonkPacket;
import com.simibubi.create.content.trains.entity.Train;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public static class HonkPacket.Clientbound
extends HonkPacket
implements ClientboundPacketPayload {
    public static final StreamCodec<ByteBuf, HonkPacket.Clientbound> STREAM_CODEC = HonkPacket.codec(HonkPacket.Clientbound::new);

    public HonkPacket.Clientbound(Train train, boolean isHonk) {
        this(train.id, isHonk);
    }

    private HonkPacket.Clientbound(UUID id, boolean isHonk) {
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
