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
 * Carries the station's {@link BlockPos}; the server scans the station's assembly range for glued carriages
 * and assembles each one as an independent Sable train (see {@link SableTrainSpawner#assembleFromStation}).
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
            SableTrainSpawner.assembleFromStation(player, packet.stationPos());
        });
    }
}
