package com.lycoris.loconautics.network.packets;

import java.util.UUID;

import com.lycoris.loconautics.allsable.SableTrainSpawner;
import com.lycoris.loconautics.core.LoconauticsConstants;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client → Server: requests that the train sub-level {@code subLevelId} be relocated onto the Create track at
 * {@code blockPos}, picking the rail direction from {@code lookAngle} (and {@code bezierDirection} for curved
 * segments). Sent by {@link com.lycoris.loconautics.allsable.SableTrainRelocator} when the player confirms a valid
 * placement, exactly as Create's {@code TrainRelocationPacket} is sent when a carriage is wrench-relocated.
 *
 * <p>The server re-validates everything (player reach, the train still exists, the rail resolves) before moving
 * the car, so a spoofed packet cannot teleport a train to an invalid spot.
 */
public record SableTrainRelocatePacket(UUID subLevelId, BlockPos blockPos, Vec3 lookAngle, boolean bezierDirection)
        implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SableTrainRelocatePacket> TYPE =
            new CustomPacketPayload.Type<>(LoconauticsConstants.id("sable_train_relocate"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SableTrainRelocatePacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, p) -> {
                        buf.writeUUID(p.subLevelId());
                        buf.writeBlockPos(p.blockPos());
                        buf.writeDouble(p.lookAngle().x);
                        buf.writeDouble(p.lookAngle().y);
                        buf.writeDouble(p.lookAngle().z);
                        buf.writeBoolean(p.bezierDirection());
                    },
                    buf -> new SableTrainRelocatePacket(
                            buf.readUUID(),
                            buf.readBlockPos(),
                            new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
                            buf.readBoolean())
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SableTrainRelocatePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            SableTrainSpawner.relocate(player, packet.subLevelId(), packet.blockPos(),
                    packet.lookAngle(), packet.bezierDirection());
        });
    }
}
