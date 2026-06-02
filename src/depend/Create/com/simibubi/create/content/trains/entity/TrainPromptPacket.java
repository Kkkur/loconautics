/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.ComponentSerialization
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.trains.entity;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.trains.TrainHUD;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record TrainPromptPacket(Component text, boolean shadow) implements ClientboundPacketPayload
{
    public static final StreamCodec<RegistryFriendlyByteBuf, TrainPromptPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)ComponentSerialization.STREAM_CODEC, TrainPromptPacket::text, (StreamCodec)ByteBufCodecs.BOOL, TrainPromptPacket::shadow, TrainPromptPacket::new);

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        TrainHUD.currentPrompt = this.text;
        TrainHUD.currentPromptShadow = this.shadow;
        TrainHUD.promptKeepAlive = 30;
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.S_TRAIN_PROMPT;
    }
}
