/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.handler.ServerPacketContext
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.network.packets;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.steering_wheel.SteeringWheelBlockEntity;
import dev.simulated_team.simulated.data.advancements.SimAdvancements;
import foundry.veil.api.network.handler.ServerPacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

public record SteeringWheelPacket(boolean shouldStop, float targetAngle, BlockPos pos) implements CustomPacketPayload
{
    public static CustomPacketPayload.Type<SteeringWheelPacket> TYPE = new CustomPacketPayload.Type(Simulated.path("steering_wheel_update"));
    public static StreamCodec<RegistryFriendlyByteBuf, SteeringWheelPacket> CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.BOOL, SteeringWheelPacket::shouldStop, (StreamCodec)ByteBufCodecs.FLOAT, SteeringWheelPacket::targetAngle, (StreamCodec)BlockPos.STREAM_CODEC, SteeringWheelPacket::pos, SteeringWheelPacket::new);

    public void handle(ServerPacketContext context) {
        ServerPlayer player = context.player();
        BlockEntity blockEntity = player.level().getBlockEntity(this.pos);
        if (blockEntity instanceof SteeringWheelBlockEntity) {
            SteeringWheelBlockEntity be = (SteeringWheelBlockEntity)blockEntity;
            be.targetAngleToUpdate = this.targetAngle();
            if (this.shouldStop()) {
                be.stopHolding();
            } else {
                be.startHolding();
                SimAdvancements.UNPOWERED_STEERING.awardTo((Player)player);
            }
        }
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
