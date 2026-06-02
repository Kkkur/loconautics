/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.equipment.bell;

import com.simibubi.create.AllPackets;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.equipment.bell.SoulPulseEffect;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record SoulPulseEffectPacket(BlockPos pos, int distance, boolean canOverlap) implements ClientboundPacketPayload
{
    public static final StreamCodec<ByteBuf, SoulPulseEffectPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, SoulPulseEffectPacket::pos, (StreamCodec)ByteBufCodecs.INT, SoulPulseEffectPacket::distance, (StreamCodec)ByteBufCodecs.BOOL, SoulPulseEffectPacket::canOverlap, SoulPulseEffectPacket::new);

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        CreateClient.SOUL_PULSE_EFFECT_HANDLER.addPulse(new SoulPulseEffect(this.pos, this.distance, this.canOverlap));
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.SOUL_PULSE;
    }
}
