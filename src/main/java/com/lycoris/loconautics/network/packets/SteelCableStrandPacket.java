package com.lycoris.loconautics.network.packets;

import com.lycoris.loconautics.content.steelcable.SteelCableTracker;
import com.lycoris.loconautics.core.LoconauticsConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

/**
 * Server → Client: tells the client that the given strand UUID was created by a
 * Steel Cable item, so {@link com.lycoris.loconautics.mixin.client.RopeConnectorRendererMixin}
 * can redirect rendering to {@link com.lycoris.loconautics.content.steelcable.SteelCableStrandRenderer}.
 */
public record SteelCableStrandPacket(UUID strandUUID) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SteelCableStrandPacket> TYPE =
            new CustomPacketPayload.Type<>(LoconauticsConstants.id("steel_cable_strand"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SteelCableStrandPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> buf.writeUUID(packet.strandUUID()),
                    buf -> new SteelCableStrandPacket(buf.readUUID())
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @OnlyIn(Dist.CLIENT)
    public static void handle(SteelCableStrandPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> SteelCableTracker.register(packet.strandUUID()));
    }
}