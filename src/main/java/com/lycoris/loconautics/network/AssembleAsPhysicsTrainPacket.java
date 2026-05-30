package com.lycoris.loconautics.network;

import com.lycoris.loconautics.core.LoconauticsConstants;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client -> Server. Sent when the player presses the "Assemble as Physics Train" button on the
 * station assembly screen. Carries the station block position.
 *
 * <p>Phase 1: the handler only validates and logs. The real assembly (capture carriage blocks,
 * create Sable sub-levels) is wired in Phase 3 via PhysicsAssemblyOrchestrator.
 */
public record AssembleAsPhysicsTrainPacket(BlockPos stationPos) implements CustomPacketPayload {

    public static final Type<AssembleAsPhysicsTrainPacket> TYPE =
            new Type<>(LoconauticsConstants.id("assemble_physics_train"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AssembleAsPhysicsTrainPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, AssembleAsPhysicsTrainPacket::stationPos,
                    AssembleAsPhysicsTrainPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /** Runs on the server, already scheduled on the main thread by {@link LoconauticsNetwork}. */
    public static void handle(AssembleAsPhysicsTrainPacket packet, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }
        BlockPos pos = packet.stationPos();

        // Basic sanity checks before we ever touch Create's assembly.
        if (!player.level().isLoaded(pos)) {
            return;
        }
        BlockEntity be = player.level().getBlockEntity(pos);
        if (be == null) {
            return;
        }

        // TODO Phase 3: hand off to PhysicsAssemblyOrchestrator.assemble(player, stationBlockEntity).
        LoconauticsConstants.LOGGER.info(
                "Received physics-train assembly request at {} from {}", pos, player.getName().getString());
    }
}
