/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  foundry.veil.api.network.handler.PacketContext
 *  io.netty.buffer.ByteBuf
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package dev.ryanhcode.sable.network.packets;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.ClientSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.network.packets.PacketReceiveMode;
import dev.ryanhcode.sable.network.tcp.SableTCPPacket;
import dev.ryanhcode.sable.network.udp.SableUDPPacket;
import dev.ryanhcode.sable.network.udp.SableUDPPacketType;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.util.SableBufferUtils;
import foundry.veil.api.network.handler.PacketContext;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class ClientboundSableSnapshotDualPacket
implements SableUDPPacket,
SableTCPPacket {
    public static final CustomPacketPayload.Type<ClientboundSableSnapshotDualPacket> TYPE = new CustomPacketPayload.Type(Sable.sablePath("snapshot_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSableSnapshotDualPacket> CODEC = StreamCodec.of((buf, value) -> value.encode((ByteBuf)buf), ClientboundSableSnapshotDualPacket::new);
    private final int interpolationTick;
    private final List<Entry> entries;

    public ClientboundSableSnapshotDualPacket(int interpolationTick, List<Entry> entries) {
        this.interpolationTick = interpolationTick;
        this.entries = entries;
    }

    @Override
    public void handle(PacketContext context) {
        this.handleClient(context.level(), PacketReceiveMode.TCP);
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public ClientboundSableSnapshotDualPacket(ByteBuf buf) {
        this(buf.readInt(), ClientboundSableSnapshotDualPacket.readList(buf));
    }

    private static List<Entry> readList(ByteBuf byteBuf) {
        FriendlyByteBuf buf = (FriendlyByteBuf)byteBuf;
        ObjectArrayList list = new ObjectArrayList();
        int length = buf.readVarInt();
        for (int i = 0; i < length; ++i) {
            list.add(new Entry(buf.readLong(), SableBufferUtils.read((ByteBuf)buf, new Pose3d()), (Vector3fc)SableBufferUtils.read((ByteBuf)buf, new Vector3f()), (Vector3fc)SableBufferUtils.read((ByteBuf)buf, new Vector3f())));
        }
        return list;
    }

    public void encode(ByteBuf byteBuf) {
        FriendlyByteBuf buf = (FriendlyByteBuf)byteBuf;
        buf.writeInt(this.interpolationTick);
        buf.writeVarInt(this.entries.size());
        for (Entry entry : this.entries) {
            buf.writeLong(entry.plotCoordinate);
            SableBufferUtils.write((ByteBuf)buf, (Pose3dc)entry.pose);
            SableBufferUtils.write((ByteBuf)buf, entry.linearVelocity);
            SableBufferUtils.write((ByteBuf)buf, entry.angularVelocity);
        }
    }

    @Override
    public SableUDPPacketType getType() {
        return SableUDPPacketType.SNAPSHOT;
    }

    @Override
    public void handleClient(Level level) {
        this.handleClient(level, PacketReceiveMode.UDP);
    }

    private void handleClient(Level level, PacketReceiveMode packetReceiveMode) {
        SubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container == null) {
            Sable.LOGGER.error("Received a sub-level movement packet for a level without a sub-level container");
            return;
        }
        for (Entry entry : this.entries) {
            SubLevel subLevel = container.getSubLevel(ChunkPos.getX((long)entry.plotCoordinate), ChunkPos.getZ((long)entry.plotCoordinate));
            if (!(subLevel instanceof ClientSubLevel)) {
                Sable.LOGGER.error("Received a sub-level movement packet for a non-existent sub-level");
                continue;
            }
            ClientSubLevel clientSubLevel = (ClientSubLevel)subLevel;
            ((ClientSubLevelContainer)container).getInterpolation().receiveSnapshot(clientSubLevel, this.interpolationTick, (Pose3dc)entry.pose, packetReceiveMode);
        }
    }

    public List<Entry> entries() {
        return this.entries;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        ClientboundSableSnapshotDualPacket that = (ClientboundSableSnapshotDualPacket)obj;
        return Objects.equals(this.entries, that.entries);
    }

    public int hashCode() {
        return Objects.hash(this.entries);
    }

    public String toString() {
        return "ClientboundSableSnapshotDualPacket[entries=" + String.valueOf(this.entries) + "]";
    }

    public record Entry(long plotCoordinate, Pose3d pose, Vector3fc linearVelocity, Vector3fc angularVelocity) {
    }
}
