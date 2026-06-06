package com.lycoris.loconautics.network.packets;

import com.lycoris.loconautics.content.analogcontroller.AnalogControllerBlockEntity;
import com.lycoris.loconautics.core.LoconauticsConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client → Server: player explicitly dismounted (e.g. pressed ESC).
 * Server calls toggleUser() to cleanly disconnect and update block state.
 */
public record AnalogControllerDismountPacket(BlockPos controllerPos)
        implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AnalogControllerDismountPacket> TYPE =
            new CustomPacketPayload.Type<>(LoconauticsConstants.id("analog_controller_dismount"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AnalogControllerDismountPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, AnalogControllerDismountPacket::controllerPos,
                    AnalogControllerDismountPacket::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(AnalogControllerDismountPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            BlockEntity be = player.level().getBlockEntity(packet.controllerPos());
            if (be instanceof AnalogControllerBlockEntity ace) {
                ace.toggleUser(player); // toggleUser dismounts if this player is the current user
            }
        });
    }
}