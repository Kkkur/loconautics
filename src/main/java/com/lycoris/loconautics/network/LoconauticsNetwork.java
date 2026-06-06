package com.lycoris.loconautics.network;

import com.lycoris.loconautics.network.packets.AnalogControllerInputPacket;
import com.lycoris.loconautics.core.LoconauticsConstants;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Central registration for all Loconautics network payloads. Registers on the mod event bus via
 * {@link RegisterPayloadHandlersEvent}.
 */
@EventBusSubscriber(modid = LoconauticsConstants.MOD_ID)
public final class LoconauticsNetwork {

    private LoconauticsNetwork() {
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(String.valueOf(LoconauticsConstants.NETWORK_VERSION));

        // Client -> Server: request to assemble the imminent train as a physics train.
        registrar.playToServer(
                AssembleAsPhysicsTrainPacket.TYPE,
                AssembleAsPhysicsTrainPacket.STREAM_CODEC,
                AssembleAsPhysicsTrainPacket::handle
        );

        // Client -> Server: key event from a mounted Analog Controller.
        registrar.playToServer(
                AnalogControllerInputPacket.TYPE,
                AnalogControllerInputPacket.STREAM_CODEC,
                AnalogControllerInputPacket::handle
        )
        // Server -> Client: a train entered/left physics mode.
        registrar.playToClient(
                PhysicsTrainSyncPacket.TYPE,
                PhysicsTrainSyncPacket.STREAM_CODEC,
                PhysicsTrainSyncPacket::handle
        );
    }

    // ----- Convenience senders -----

    /** Sends a physics-train sync to a single player (e.g. on login / chunk track). */
    public static void sendTo(ServerPlayer player, PhysicsTrainSyncPacket packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    /** Broadcasts a physics-train sync to every connected player. */
    public static void sendToAll(PhysicsTrainSyncPacket packet) {
        PacketDistributor.sendToAllPlayers(packet);
    }
}