package com.lycoris.loconautics.network;

import com.lycoris.loconautics.network.packets.AnalogControllerInputPacket;
import com.lycoris.loconautics.network.packets.SteelCableStrandPacket;
import com.lycoris.loconautics.network.packets.AnalogControllerMountPacket;
import com.lycoris.loconautics.network.packets.AnalogControllerScrollPacket;
import com.lycoris.loconautics.network.packets.AnalogControllerDismountPacket;
import com.lycoris.loconautics.core.LoconauticsConstants;

import net.minecraft.core.BlockPos;
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

        // Client -> Server: key event from a mounted Analog Controller.
        registrar.playToServer(
                AnalogControllerInputPacket.TYPE,
                AnalogControllerInputPacket.STREAM_CODEC,
                AnalogControllerInputPacket::handle
        );

        // Server -> Client: player mounted or dismounted an Analog Controller.
        registrar.playToClient(
                AnalogControllerMountPacket.TYPE,
                AnalogControllerMountPacket.STREAM_CODEC,
                AnalogControllerMountPacket::handle
        );

        // Client -> Server: scroll wheel adjusted the max-power cap.
        registrar.playToServer(
                AnalogControllerScrollPacket.TYPE,
                AnalogControllerScrollPacket.STREAM_CODEC,
                AnalogControllerScrollPacket::handle
        );

        // Client -> Server: player explicitly dismounted (ESC).
        registrar.playToServer(
                AnalogControllerDismountPacket.TYPE,
                AnalogControllerDismountPacket.STREAM_CODEC,
                AnalogControllerDismountPacket::handle
        );
        // Server -> Client: a rope strand was created by a Steel Cable item.
        registrar.playToClient(
                SteelCableStrandPacket.TYPE,
                SteelCableStrandPacket.STREAM_CODEC,
                SteelCableStrandPacket::handle
        );
    }

    // ----- Convenience senders -----

    /** Tells a specific client player they have mounted/dismounted an Analog Controller. */
    public static void sendMount(ServerPlayer player, boolean mounted, BlockPos pos) {
        PacketDistributor.sendToPlayer(player, new AnalogControllerMountPacket(mounted, pos));
    }

    /** Sends a dismount request from client to server. */
    public static void sendDismount(BlockPos pos) {
        net.createmod.catnip.platform.CatnipServices.NETWORK.sendToServer(
                new AnalogControllerDismountPacket(pos));
    }
}