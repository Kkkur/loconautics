package com.lycoris.loconautics.network.packets;

import com.lycoris.loconautics.allsable.SableTrainClientRegistry;
import com.lycoris.loconautics.core.LoconauticsConstants;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server → Client: announces whether a Sable train is parked (stopped) at the station at {@code pos}. The station
 * screen mixin reads this to surface Create's disassemble button for Sable trains.
 *
 * <p>We push this explicitly rather than piggy-backing on the station block entity's NBT client-sync: that path
 * only flushes on the BE's own change events and was not reliably delivering our flag. A tiny dedicated packet,
 * broadcast on change and periodically refreshed while parked, is deterministic.
 */
public record StationParkedSyncPacket(BlockPos pos, boolean parked) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<StationParkedSyncPacket> TYPE =
            new CustomPacketPayload.Type<>(LoconauticsConstants.id("station_parked_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, StationParkedSyncPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, StationParkedSyncPacket::pos,
                    net.minecraft.network.codec.ByteBufCodecs.BOOL, StationParkedSyncPacket::parked,
                    StationParkedSyncPacket::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @OnlyIn(Dist.CLIENT)
    public static void handle(StationParkedSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() ->
                SableTrainClientRegistry.setStationParked(packet.pos(), packet.parked()));
    }

    /** Broadcasts a station's current parked state to every connected client. */
    public static void broadcast(BlockPos pos, boolean parked) {
        PacketDistributor.sendToAllPlayers(new StationParkedSyncPacket(pos.immutable(), parked));
    }
}
