/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  foundry.veil.api.network.handler.PacketContext
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.UUIDUtil
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.ChunkPos
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.network.packets.physics_staff;

import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.physics_staff.PhysicsStaffAction;
import dev.simulated_team.simulated.content.physics_staff.PhysicsStaffServerHandler;
import dev.simulated_team.simulated.network.packets.physics_staff.PhysicsStaffBeamPacket;
import dev.simulated_team.simulated.util.SimCodecUtil;
import foundry.veil.api.network.handler.PacketContext;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class PhysicsStaffActionPacket
implements CustomPacketPayload {
    public static CustomPacketPayload.Type<PhysicsStaffActionPacket> TYPE = new CustomPacketPayload.Type(Simulated.path("physics_staff_action"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PhysicsStaffActionPacket> CODEC = StreamCodec.composite(PhysicsStaffAction.STREAM_CODEC, packet -> packet.action, (StreamCodec)UUIDUtil.STREAM_CODEC, packet -> packet.subLevel, SimCodecUtil.STREAM_VECTOR3D, packet -> packet.location, PhysicsStaffActionPacket::new);
    protected final PhysicsStaffAction action;
    protected final UUID subLevel;
    protected final Vector3d location;

    public PhysicsStaffActionPacket(PhysicsStaffAction action, UUID subLevel, Vector3dc location) {
        this.action = action;
        this.subLevel = subLevel;
        this.location = new Vector3d(location);
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(PacketContext context) {
        ServerLevel level = (ServerLevel)context.level();
        if (this.action == PhysicsStaffAction.LOCK) {
            PhysicsStaffServerHandler.get(level).toggleLock(this.subLevel);
        }
        if (this.action == PhysicsStaffAction.STOP_DRAG) {
            PhysicsStaffServerHandler.get(level).stopDragging(context.player().getUUID());
        }
        if (this.action == PhysicsStaffAction.LOCK) {
            Player player = context.player();
            Vector3d beamStart = JOMLConversion.toJOML((Position)player.getEyePosition());
            Vector3d beamEnd = new Vector3d((Vector3dc)this.location);
            ChunkPos chunk = new ChunkPos(BlockPos.containing((double)this.location.x(), (double)this.location.y(), (double)this.location.z()));
            PhysicsStaffBeamPacket beamPacket = new PhysicsStaffBeamPacket(player.getUUID(), beamStart, beamEnd);
            for (ServerPlayer otherPlayer : level.getChunkSource().chunkMap.getPlayers(chunk, false)) {
                if (otherPlayer == player) continue;
                otherPlayer.connection.send((Packet)new ClientboundCustomPayloadPacket((CustomPacketPayload)beamPacket));
            }
        }
    }
}
