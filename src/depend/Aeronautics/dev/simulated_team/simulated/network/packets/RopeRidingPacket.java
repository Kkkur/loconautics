/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.chainConveyor.ServerChainConveyorHandler
 *  foundry.veil.api.network.handler.ServerPacketContext
 *  net.minecraft.core.UUIDUtil
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 */
package dev.simulated_team.simulated.network.packets;

import com.simibubi.create.content.kinetics.chainConveyor.ServerChainConveyorHandler;
import dev.simulated_team.simulated.Simulated;
import foundry.veil.api.network.handler.ServerPacketContext;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public record RopeRidingPacket(UUID uuid, boolean stop) implements CustomPacketPayload
{
    public static CustomPacketPayload.Type<RopeRidingPacket> TYPE = new CustomPacketPayload.Type(Simulated.path("ride_rope"));
    public static StreamCodec<RegistryFriendlyByteBuf, RopeRidingPacket> CODEC = StreamCodec.composite((StreamCodec)UUIDUtil.STREAM_CODEC, RopeRidingPacket::uuid, (StreamCodec)ByteBufCodecs.BOOL, RopeRidingPacket::stop, RopeRidingPacket::new);

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(ServerPacketContext ctx) {
        ServerPlayer player = ctx.player();
        player.connection.aboveGroundTickCount = 0;
        player.connection.aboveGroundVehicleTickCount = 0;
        player.fallDistance = 0.0f;
        if (this.stop) {
            ServerChainConveyorHandler.handleStopRidingPacket((Player)player);
        } else {
            ServerChainConveyorHandler.handleTTLPacket((Player)player);
        }
    }
}
