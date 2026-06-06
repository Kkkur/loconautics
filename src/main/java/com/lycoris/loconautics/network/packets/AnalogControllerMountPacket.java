package com.lycoris.loconautics.network.packets;

import com.lycoris.loconautics.content.analogcontroller.AnalogControllerClientHandler;
import com.lycoris.loconautics.core.LoconauticsConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server → Client: tells the client whether the local player has mounted or
 * dismounted an Analog Controller.
 *
 * On mount:  mounted=true,  pos=controller position → startControlling(pos)
 * On dismount: mounted=false, pos=BlockPos.ZERO      → stopControlling()
 */
public record AnalogControllerMountPacket(boolean mounted, BlockPos pos)
        implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AnalogControllerMountPacket> TYPE =
            new CustomPacketPayload.Type<>(LoconauticsConstants.id("analog_controller_mount"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AnalogControllerMountPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,       AnalogControllerMountPacket::mounted,
                    BlockPos.STREAM_CODEC,    AnalogControllerMountPacket::pos,
                    AnalogControllerMountPacket::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @OnlyIn(Dist.CLIENT)
    public static void handle(AnalogControllerMountPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (packet.mounted()) {
                AnalogControllerClientHandler.startControlling(packet.pos());
            } else {
                AnalogControllerClientHandler.stopControlling();
            }
        });
    }
}