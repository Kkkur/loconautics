/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.handler.PacketContext
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.UUIDUtil
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  org.joml.Vector3d
 */
package dev.simulated_team.simulated.network.packets.lodestone_compass;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.navigation_targets.lodestone_compass_compatability.ClientLodestonePositions;
import foundry.veil.api.network.handler.PacketContext;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.joml.Vector3d;

public record UpdateClientLodestonePositionPacket(UUID id, Vector3d sentPosition) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<UpdateClientLodestonePositionPacket> TYPE = new CustomPacketPayload.Type(Simulated.path("update_client_lodestone"));
    public static final StreamCodec<ByteBuf, UpdateClientLodestonePositionPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)UUIDUtil.STREAM_CODEC, UpdateClientLodestonePositionPacket::id, (StreamCodec)StreamCodec.of((byteBuf, p) -> {
        byteBuf.writeDouble(p.x);
        byteBuf.writeDouble(p.y);
        byteBuf.writeDouble(p.z);
    }, byteBuf -> new Vector3d(byteBuf.readDouble(), byteBuf.readDouble(), byteBuf.readDouble())), UpdateClientLodestonePositionPacket::sentPosition, UpdateClientLodestonePositionPacket::new);

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(PacketContext context) {
        Level level = context.level();
        if (level instanceof ClientLevel) {
            ClientLevel cl = (ClientLevel)level;
            ClientLodestonePositions positions = (ClientLodestonePositions)ClientLodestonePositions.clientPositions.get((LevelAccessor)cl);
            positions.CLIENT_LODESTONE_MAP.put((Object)this.id, (Object)this.sentPosition);
        }
    }
}
