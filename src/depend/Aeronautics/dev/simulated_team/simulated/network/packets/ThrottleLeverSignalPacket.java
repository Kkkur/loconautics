/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.handler.ServerPacketContext
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.network.packets;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.throttle_lever.ThrottleLeverBlockEntity;
import dev.simulated_team.simulated.util.hold_interaction.BlockHoldInteraction;
import foundry.veil.api.network.handler.ServerPacketContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

public record ThrottleLeverSignalPacket(BlockPos pos, int signal) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ThrottleLeverSignalPacket> TYPE = new CustomPacketPayload.Type(Simulated.path("throttle_lever_signal"));
    public static final StreamCodec<ByteBuf, ThrottleLeverSignalPacket> CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, ThrottleLeverSignalPacket::pos, (StreamCodec)ByteBufCodecs.INT, ThrottleLeverSignalPacket::signal, ThrottleLeverSignalPacket::new);

    public CustomPacketPayload.Type<ThrottleLeverSignalPacket> type() {
        return TYPE;
    }

    public void handle(ServerPacketContext context) {
        ServerPlayer player = context.player();
        ServerLevel level = (ServerLevel)player.level();
        BlockEntity blockEntity = level.getBlockEntity(this.pos);
        if (blockEntity instanceof ThrottleLeverBlockEntity) {
            ThrottleLeverBlockEntity throttleLever = (ThrottleLeverBlockEntity)blockEntity;
            if (!BlockHoldInteraction.inInteractionRange((Player)player, (Position)this.pos.getCenter(), 1.0)) {
                return;
            }
            throttleLever.setSignal(this.signal);
        }
    }
}
