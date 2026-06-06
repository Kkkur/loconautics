package com.lycoris.loconautics.network.packets;

import com.lycoris.loconautics.content.analogcontroller.AnalogControllerBlockEntity;
import com.lycoris.loconautics.core.LoconauticsConstants;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Collection;
import java.util.List;

/**
 * Sent from client → server whenever keys are pressed or released while the player
 * is mounted on an {@link AnalogControllerBlockEntity}.
 *
 * Mirrors {@code ControlsInputPacket} from Create.
 */
public record AnalogControllerInputPacket(
        List<Integer> keys,
        boolean pressed,
        BlockPos controllerPos
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AnalogControllerInputPacket> TYPE =
            new CustomPacketPayload.Type<>(LoconauticsConstants.id("analog_controller_input"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AnalogControllerInputPacket> STREAM_CODEC =
            StreamCodec.composite(
                    CatnipStreamCodecBuilders.list(ByteBufCodecs.VAR_INT), AnalogControllerInputPacket::keys,
                    ByteBufCodecs.BOOL,                                      AnalogControllerInputPacket::pressed,
                    BlockPos.STREAM_CODEC,                                   AnalogControllerInputPacket::controllerPos,
                    AnalogControllerInputPacket::new
            );

    /** Convenience constructor accepting any collection. */
    public AnalogControllerInputPacket(Collection<Integer> keys, boolean pressed, BlockPos pos) {
        this(List.copyOf(keys), pressed, pos);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /** Runs on the server main thread. */
    public static void handle(AnalogControllerInputPacket packet, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        if (player.isSpectator() && packet.pressed()) return;

        BlockEntity be = player.level().getBlockEntity(packet.controllerPos());
        if (!(be instanceof AnalogControllerBlockEntity ace)) return;

        for (int key : packet.keys()) {
            ace.onKeyEvent(player.getUUID(), key, packet.pressed());
        }
    }
}