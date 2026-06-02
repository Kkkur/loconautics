/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.codec.StreamCodec
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.foundation.utility;

import com.simibubi.create.AllPackets;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public static enum ServerSpeedProvider.Packet implements ClientboundPacketPayload
{
    INSTANCE;

    public static final StreamCodec<ByteBuf, ServerSpeedProvider.Packet> STREAM_CODEC;

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        if (!initialized) {
            initialized = true;
            clientTimer = 0;
            return;
        }
        float target = (float)ServerSpeedProvider.getSyncInterval().intValue() / (float)Math.max(clientTimer, 1);
        modifier.chase((double)Math.min(target, 1.0f), 0.25, LerpedFloat.Chaser.EXP);
        clientTimer = -1;
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.SERVER_SPEED;
    }

    static {
        STREAM_CODEC = StreamCodec.unit((Object)((Object)INSTANCE));
    }
}
