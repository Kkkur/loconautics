package com.lycoris.loconautics.network.packets;

import com.lycoris.loconautics.allsable.SableTrainSpawner;
import com.lycoris.loconautics.core.LoconauticsConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Sent client → server when the player clicks "Assemble as Sable Train" in the station assembly screen.
 * Delegates to the same spawn path as {@code /loconautics sabletrain}: raycasts from the player's view
 * to find the cart they were looking at when they opened the station.
 */
public record AssembleSableTrainPacket(BlockPos stationPos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AssembleSableTrainPacket> TYPE =
            new CustomPacketPayload.Type<>(LoconauticsConstants.id("assemble_sable_train"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AssembleSableTrainPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, AssembleSableTrainPacket::stationPos,
                    AssembleSableTrainPacket::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(AssembleSableTrainPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            SableTrainSpawner.spawn(player, 0.0, false);
        });
    }
}
