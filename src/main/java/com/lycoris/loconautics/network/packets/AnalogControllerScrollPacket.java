package com.lycoris.loconautics.network.packets;

import com.lycoris.loconautics.content.analogcontroller.AnalogControllerBlockEntity;
import com.lycoris.loconautics.core.LoconauticsConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Client → Server: player scrolled while mounted, adjusting the max-power cap. */
public record AnalogControllerScrollPacket(int delta, BlockPos pos)
        implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AnalogControllerScrollPacket> TYPE =
            new CustomPacketPayload.Type<>(LoconauticsConstants.id("analog_controller_scroll"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AnalogControllerScrollPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,    AnalogControllerScrollPacket::delta,
                    BlockPos.STREAM_CODEC,    AnalogControllerScrollPacket::pos,
                    AnalogControllerScrollPacket::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(AnalogControllerScrollPacket packet, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        context.enqueueWork(() -> {
            BlockEntity be = player.level().getBlockEntity(packet.pos());
            if (be instanceof AnalogControllerBlockEntity ace)
                ace.onScroll(player.getUUID(), packet.delta());
        });
    }
}