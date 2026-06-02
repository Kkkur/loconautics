/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  dev.ryanhcode.sable.companion.math.BoundingBox3ic
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  foundry.veil.api.network.handler.PacketContext
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.network.packets.tcp;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.ClientSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.network.client.ClientSableInterpolationState;
import dev.ryanhcode.sable.network.client.SubLevelSnapshotInterpolator;
import dev.ryanhcode.sable.network.tcp.SableTCPPacket;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.util.SableBufferUtils;
import foundry.veil.api.network.handler.PacketContext;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record ClientboundStartTrackingSubLevelPacket(long plotCoordinate, UUID subLevelID, Pose3dc lastPose, Pose3d pose, BoundingBox3ic bounds, @Nullable String name, int gameTick) implements SableTCPPacket
{
    public static final CustomPacketPayload.Type<ClientboundStartTrackingSubLevelPacket> TYPE = new CustomPacketPayload.Type(Sable.sablePath("start_tracking_sub_level"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundStartTrackingSubLevelPacket> CODEC = StreamCodec.of((buf, value) -> value.write((FriendlyByteBuf)buf), ClientboundStartTrackingSubLevelPacket::read);

    private void write(FriendlyByteBuf buf) {
        buf.writeLong(this.plotCoordinate);
        buf.writeUUID(this.subLevelID);
        SableBufferUtils.write((ByteBuf)buf, this.lastPose);
        SableBufferUtils.write((ByteBuf)buf, (Pose3dc)this.pose);
        SableBufferUtils.write((ByteBuf)buf, this.bounds);
        buf.writeBoolean(this.name != null);
        if (this.name != null) {
            buf.writeUtf(this.name);
        }
        buf.writeInt(this.gameTick);
    }

    private static ClientboundStartTrackingSubLevelPacket read(FriendlyByteBuf buf) {
        return new ClientboundStartTrackingSubLevelPacket(buf.readLong(), buf.readUUID(), (Pose3dc)SableBufferUtils.read((ByteBuf)buf, new Pose3d()), SableBufferUtils.read((ByteBuf)buf, new Pose3d()), (BoundingBox3ic)SableBufferUtils.read((ByteBuf)buf, new BoundingBox3i()), buf.readBoolean() ? buf.readUtf() : null, buf.readInt());
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(PacketContext context) {
        Level level = context.level();
        SubLevelContainer container = SubLevelContainer.getContainer(level);
        if (!(container instanceof ClientSubLevelContainer)) {
            Sable.LOGGER.error("Received a sub-level tracking packet for a level without a sub-level container");
            return;
        }
        ClientSubLevelContainer clientContainer = (ClientSubLevelContainer)container;
        ClientSubLevel subLevel = (ClientSubLevel)clientContainer.allocateSubLevel(this.subLevelID, ChunkPos.getX((long)this.plotCoordinate), ChunkPos.getZ((long)this.plotCoordinate), new Pose3d(this.lastPose));
        SubLevelSnapshotInterpolator interpolator = subLevel.getInterpolator();
        interpolator.receiveSnapshot(this.gameTick - 1, this.lastPose);
        interpolator.receiveSnapshot(this.gameTick, (Pose3dc)this.pose);
        ClientSableInterpolationState interpolationState = clientContainer.getInterpolation();
        if (!interpolationState.isStopped()) {
            subLevel.setInitialPosesFrom(interpolationState);
        }
        interpolator.setFirstPoses((Pose3dc)this.pose, this.lastPose);
        subLevel.getPlot().setBoundingBox(this.bounds);
        subLevel.forceUpdateBounds();
        subLevel.updateRenderData();
        if (this.name != null) {
            subLevel.setName(this.name);
        }
    }
}
