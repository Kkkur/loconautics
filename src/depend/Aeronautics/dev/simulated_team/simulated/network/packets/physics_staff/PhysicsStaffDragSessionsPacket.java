/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.handler.PacketContext
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.core.UUIDUtil
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.level.Level
 *  org.joml.Vector3d
 */
package dev.simulated_team.simulated.network.packets.physics_staff;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.SimulatedClient;
import dev.simulated_team.simulated.util.SimCodecUtil;
import foundry.veil.api.network.handler.PacketContext;
import java.util.List;
import java.util.UUID;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;

public record PhysicsStaffDragSessionsPacket(ResourceKey<Level> dimension, List<Pair<UUID, Vector3d>> sessions) implements CustomPacketPayload
{
    public static CustomPacketPayload.Type<PhysicsStaffDragSessionsPacket> TYPE = new CustomPacketPayload.Type(Simulated.path("physics_staff_drag_sessions"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PhysicsStaffDragSessionsPacket> CODEC = StreamCodec.composite((StreamCodec)ResourceKey.streamCodec((ResourceKey)Registries.DIMENSION), i -> i.dimension, (StreamCodec)CatnipStreamCodecBuilders.list((StreamCodec)Pair.streamCodec((StreamCodec)UUIDUtil.STREAM_CODEC, SimCodecUtil.STREAM_VECTOR3D)), i -> i.sessions, PhysicsStaffDragSessionsPacket::new);

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(PacketContext context) {
        SimulatedClient.PHYSICS_STAFF_CLIENT_HANDLER.setServerDragSessions(this.dimension, this.sessions);
    }
}
