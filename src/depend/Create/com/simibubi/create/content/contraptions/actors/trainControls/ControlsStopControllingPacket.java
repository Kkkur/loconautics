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
package com.simibubi.create.content.contraptions.actors.trainControls;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsHandler;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public enum ControlsStopControllingPacket implements ClientboundPacketPayload
{
    INSTANCE;

    public static final StreamCodec<ByteBuf, ControlsStopControllingPacket> STREAM_CODEC;

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        ControlsHandler.stopControlling();
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CONTROLS_ABORT;
    }

    static {
        STREAM_CODEC = StreamCodec.unit((Object)((Object)INSTANCE));
    }
}
