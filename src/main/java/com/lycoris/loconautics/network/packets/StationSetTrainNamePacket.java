package com.lycoris.loconautics.network.packets;

import com.lycoris.loconautics.allsable.SableTrainSpawner;
import com.lycoris.loconautics.core.LoconauticsConstants;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Sent client → server when the player commits a name in the station's train-name box while a Sable train is parked
 * there. Carries the station {@link BlockPos} and the typed name; the server names the whole parked consist (the
 * frontmost car gets the base name, each car coupled behind gets "{name} Carriage N") via
 * {@link SableTrainSpawner#setTrainNameFromStation}.
 */
public record StationSetTrainNamePacket(BlockPos stationPos, String name) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<StationSetTrainNamePacket> TYPE =
            new CustomPacketPayload.Type<>(LoconauticsConstants.id("station_set_train_name"));

    public static final StreamCodec<RegistryFriendlyByteBuf, StationSetTrainNamePacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, StationSetTrainNamePacket::stationPos,
                    ByteBufCodecs.STRING_UTF8, StationSetTrainNamePacket::name,
                    StationSetTrainNamePacket::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(StationSetTrainNamePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            SableTrainSpawner.setTrainNameFromStation(player, packet.stationPos(), packet.name());
        });
    }
}
