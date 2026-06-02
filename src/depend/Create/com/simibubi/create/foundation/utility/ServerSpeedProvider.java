/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.foundation.utility;

import com.simibubi.create.AllPackets;
import com.simibubi.create.infrastructure.config.AllConfigs;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ServerSpeedProvider {
    private static final LerpedFloat modifier = LerpedFloat.linear();
    private static int clientTimer = 0;
    private static int serverTimer = 0;
    private static boolean initialized = false;

    public static void serverTick() {
        if (++serverTimer > ServerSpeedProvider.getSyncInterval()) {
            CatnipServices.NETWORK.sendToAllClients((CustomPacketPayload)Packet.INSTANCE);
            serverTimer = 0;
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    public static void clientTick() {
        if (Minecraft.getInstance().hasSingleplayerServer() && Minecraft.getInstance().isPaused()) {
            return;
        }
        modifier.tickChaser();
        ++clientTimer;
    }

    public static Integer getSyncInterval() {
        return (Integer)AllConfigs.server().tickrateSyncTimer.get();
    }

    public static float get() {
        return modifier.getValue();
    }

    public static enum Packet implements ClientboundPacketPayload
    {
        INSTANCE;

        public static final StreamCodec<ByteBuf, Packet> STREAM_CODEC;

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
}
