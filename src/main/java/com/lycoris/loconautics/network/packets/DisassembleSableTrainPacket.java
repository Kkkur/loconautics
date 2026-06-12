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
 * Sent client → server when the player clicks the station's disassemble button while a Sable train is parked there.
 * Carries the station's {@link BlockPos}; the server finds the parked Sable train and disassembles its sub-level back
 * into the world (see {@link SableTrainSpawner#disassembleFromStation}).
 */
public record DisassembleSableTrainPacket(BlockPos stationPos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<DisassembleSableTrainPacket> TYPE =
            new CustomPacketPayload.Type<>(LoconauticsConstants.id("disassemble_sable_train"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DisassembleSableTrainPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, DisassembleSableTrainPacket::stationPos,
                    DisassembleSableTrainPacket::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(DisassembleSableTrainPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            SableTrainSpawner.disassembleFromStation(player, packet.stationPos());
        });
    }
}
