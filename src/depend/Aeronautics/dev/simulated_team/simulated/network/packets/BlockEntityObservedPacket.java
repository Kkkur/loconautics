/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.handler.ServerPacketContext
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.network.packets;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.util.Observable;
import foundry.veil.api.network.handler.ServerPacketContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public record BlockEntityObservedPacket(BlockPos pos) implements CustomPacketPayload
{
    public static CustomPacketPayload.Type<BlockEntityObservedPacket> TYPE = new CustomPacketPayload.Type(Simulated.path("be_observed"));
    public static StreamCodec<ByteBuf, BlockEntityObservedPacket> CODEC = BlockPos.STREAM_CODEC.map(BlockEntityObservedPacket::new, BlockEntityObservedPacket::pos);

    @NotNull
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(ServerPacketContext context) {
        Level level = context.level();
        ServerPlayer player = context.player();
        if (!player.canInteractWithBlock(this.pos, 4.0)) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(this.pos);
        if (blockEntity instanceof Observable) {
            Observable observable = (Observable)blockEntity;
            observable.onObserved((Player)player);
        }
    }
}
