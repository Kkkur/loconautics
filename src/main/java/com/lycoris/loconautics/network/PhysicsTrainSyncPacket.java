package com.lycoris.loconautics.network;

import com.lycoris.loconautics.client.ClientPhysicsTrainRegistry;
import com.lycoris.loconautics.client.PhysicsTrainRenderInvalidator;
import com.lycoris.loconautics.core.LoconauticsConstants;
import com.lycoris.loconautics.core.PhysicsTrainTag;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server -> Client. Tells clients that a train entered ({@code remove == false}) or left
 * ({@code remove == true}) Sable physics mode, and which sub-levels back its carriages.
 *
 * <p>Client code uses {@link ClientPhysicsTrainRegistry} to decide e.g. whether to suppress the
 * vanilla carriage render (Phase 4).
 */
public record PhysicsTrainSyncPacket(PhysicsTrainTag tag, boolean remove) implements CustomPacketPayload {

    public static final Type<PhysicsTrainSyncPacket> TYPE =
            new Type<>(LoconauticsConstants.id("physics_train_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PhysicsTrainSyncPacket> STREAM_CODEC =
            StreamCodec.composite(
                    PhysicsTrainTag.STREAM_CODEC, PhysicsTrainSyncPacket::tag,
                    ByteBufCodecs.BOOL, PhysicsTrainSyncPacket::remove,
                    PhysicsTrainSyncPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /** Runs on the client, scheduled on the main thread by {@link LoconauticsNetwork}. */
    public static void handle(PhysicsTrainSyncPacket packet, IPayloadContext context) {
        if (packet.remove()) {
            ClientPhysicsTrainRegistry.remove(packet.tag().trainId());
        } else {
            ClientPhysicsTrainRegistry.put(packet.tag());
            // Force the Flywheel contraption visual to rebuild its children so the bogey wheels — built
            // once, possibly before this packet arrived — get suppressed too (see invalidator docs).
            PhysicsTrainRenderInvalidator.request(packet.tag().trainId());
        }
        LoconauticsConstants.LOGGER.debug("Physics train sync: train={} remove={}",
                packet.tag().trainId(), packet.remove());
    }
}
