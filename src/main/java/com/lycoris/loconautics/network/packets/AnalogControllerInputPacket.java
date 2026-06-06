package com.lycoris.loconautics.network.packets;

import com.lycoris.loconautics.content.analogcontroller.AnalogControllerBlockEntity;
import com.lycoris.loconautics.core.LoconauticsConstants;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

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
) implements ServerboundPacketPayload {

    public static final CustomPacketPayload.Type<AnalogControllerInputPacket> TYPE =
            new CustomPacketPayload.Type<>(LoconauticsConstants.id("analog_controller_input"));

    public static final StreamCodec<ByteBuf, AnalogControllerInputPacket> STREAM_CODEC =
            StreamCodec.composite(
                    CatnipStreamCodecBuilders.list(ByteBufCodecs.VAR_INT), AnalogControllerInputPacket::keys,
                    ByteBufCodecs.BOOL, AnalogControllerInputPacket::pressed,
                    BlockPos.STREAM_CODEC, AnalogControllerInputPacket::controllerPos,
                    AnalogControllerInputPacket::new
            );

    /** Convenience constructor accepting any collection. */
    public AnalogControllerInputPacket(Collection<Integer> keys, boolean pressed, BlockPos pos) {
        this(List.copyOf(keys), pressed, pos);
    }

    @Override
    public void handle(ServerPlayer player) {
        if (player.isSpectator() && pressed) return;

        Level level = player.getCommandSenderWorld();
        BlockEntity be = level.getBlockEntity(controllerPos);
        if (!(be instanceof AnalogControllerBlockEntity ace)) return;

        for (int key : keys) {
            ace.onKeyEvent(player.getUUID(), key, pressed);
        }
    }

    @Override
    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        // Registered via LoconauticsNetwork; return a simple provider.
        return () -> TYPE;
    }
}