/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.handler.PacketContext
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.minecraft.core.UUIDUtil
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.level.Level
 */
package dev.simulated_team.simulated.network.packets.physics_staff;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.SimulatedClient;
import foundry.veil.api.network.handler.PacketContext;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class PhysicsStaffLocksPacket
implements CustomPacketPayload {
    public static CustomPacketPayload.Type<PhysicsStaffLocksPacket> TYPE = new CustomPacketPayload.Type(Simulated.path("physics_staff_locks"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PhysicsStaffLocksPacket> CODEC = StreamCodec.composite((StreamCodec)ResourceKey.streamCodec((ResourceKey)Registries.DIMENSION), packet -> packet.dimension, (StreamCodec)CatnipStreamCodecBuilders.list((StreamCodec)UUIDUtil.STREAM_CODEC), packet -> packet.locks, PhysicsStaffLocksPacket::new);
    protected final List<UUID> locks;
    private final ResourceKey<Level> dimension;

    public PhysicsStaffLocksPacket(ResourceKey<Level> dimension, Collection<UUID> locks) {
        this.dimension = dimension;
        this.locks = new ObjectArrayList(locks);
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(PacketContext context) {
        SimulatedClient.PHYSICS_STAFF_CLIENT_HANDLER.setLocks(this.dimension, this.locks);
    }
}
