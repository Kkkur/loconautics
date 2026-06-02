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
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.network.packets;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.handle.HandleBlockEntity;
import dev.simulated_team.simulated.content.blocks.handle.ServerHandleHoldingHandler;
import dev.simulated_team.simulated.data.advancements.SimAdvancements;
import foundry.veil.api.network.handler.ServerPacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public record UpdatePlayerUsingHandlePacket(float desiredRange, boolean remove, BlockPos interactionPos) implements CustomPacketPayload
{
    public static StreamCodec<RegistryFriendlyByteBuf, UpdatePlayerUsingHandlePacket> CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.FLOAT, UpdatePlayerUsingHandlePacket::desiredRange, (StreamCodec)ByteBufCodecs.BOOL, UpdatePlayerUsingHandlePacket::remove, (StreamCodec)BlockPos.STREAM_CODEC, UpdatePlayerUsingHandlePacket::interactionPos, UpdatePlayerUsingHandlePacket::new);
    public static CustomPacketPayload.Type<UpdatePlayerUsingHandlePacket> TYPE = new CustomPacketPayload.Type(Simulated.path("update_player_handle"));

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(ServerPacketContext ctx) {
        ServerPlayer player = ctx.player();
        Level level = ctx.level();
        BlockEntity be = level.getBlockEntity(this.interactionPos);
        if (this.remove) {
            ServerHandleHoldingHandler.stopHolding((Player)player);
        }
        if (be instanceof HandleBlockEntity) {
            HandleBlockEntity hbe = (HandleBlockEntity)be;
            if (this.remove) {
                hbe.stopGrabbingServer(player.getUUID());
            } else {
                ServerHandleHoldingHandler.startHolding((Player)player);
                hbe.startGrabbingServer(player.getUUID(), this.desiredRange);
                SimAdvancements.GET_A_GRIP.awardTo((Player)player);
                if (player.fallDistance > 64.0f) {
                    SimAdvancements.GOT_A_GRIP.awardTo((Player)player);
                }
            }
        }
    }
}
