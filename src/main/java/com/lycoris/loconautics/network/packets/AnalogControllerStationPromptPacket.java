package com.lycoris.loconautics.network.packets;

import com.lycoris.loconautics.content.analogcontroller.AnalogControllerHUD;
import com.lycoris.loconautics.core.LoconauticsConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server → Client: a station-stop banner for the Analog Controller HUD, sent each time the train's station
 * state transitions (and periodically re-sent while stopped to keep the banner alive). Mirrors Create's
 * {@code TrainPromptPacket}: it carries a pre-formatted {@link Component} and a {@code shadow} flag, and the
 * client writes them straight into {@link AnalogControllerHUD}'s prompt fields.
 */
public record AnalogControllerStationPromptPacket(Component text, boolean shadow)
        implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AnalogControllerStationPromptPacket> TYPE =
            new CustomPacketPayload.Type<>(LoconauticsConstants.id("analog_controller_station_prompt"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AnalogControllerStationPromptPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ComponentSerialization.STREAM_CODEC, AnalogControllerStationPromptPacket::text,
                    ByteBufCodecs.BOOL,                  AnalogControllerStationPromptPacket::shadow,
                    AnalogControllerStationPromptPacket::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @OnlyIn(Dist.CLIENT)
    public static void handle(AnalogControllerStationPromptPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            AnalogControllerHUD.currentPrompt = packet.text();
            AnalogControllerHUD.currentPromptShadow = packet.shadow();
            AnalogControllerHUD.promptKeepAlive = 30;
        });
    }
}
