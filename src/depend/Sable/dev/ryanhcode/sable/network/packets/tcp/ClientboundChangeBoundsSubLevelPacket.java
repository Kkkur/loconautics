/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  dev.ryanhcode.sable.companion.math.BoundingBox3ic
 *  foundry.veil.api.network.handler.PacketContext
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 */
package dev.ryanhcode.sable.network.packets.tcp;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.network.tcp.SableTCPPacket;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.util.SableBufferUtils;
import foundry.veil.api.network.handler.PacketContext;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public record ClientboundChangeBoundsSubLevelPacket(long plotCoordinate, BoundingBox3ic bounds) implements SableTCPPacket
{
    public static final CustomPacketPayload.Type<ClientboundChangeBoundsSubLevelPacket> TYPE = new CustomPacketPayload.Type(Sable.sablePath("change_bounds_sublevel"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundChangeBoundsSubLevelPacket> CODEC = StreamCodec.of((buf, value) -> value.write((FriendlyByteBuf)buf), ClientboundChangeBoundsSubLevelPacket::read);

    private static ClientboundChangeBoundsSubLevelPacket read(FriendlyByteBuf buf) {
        return new ClientboundChangeBoundsSubLevelPacket(buf.readLong(), (BoundingBox3ic)SableBufferUtils.read((ByteBuf)buf, new BoundingBox3i()));
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeLong(this.plotCoordinate);
        SableBufferUtils.write((ByteBuf)buf, this.bounds);
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(PacketContext context) {
        Level level = context.level();
        SubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container == null) {
            Sable.LOGGER.error("Received a sub-level tracking packet for a level without a sub-level container");
            return;
        }
        SubLevel subLevel = container.getSubLevel(ChunkPos.getX((long)this.plotCoordinate), ChunkPos.getZ((long)this.plotCoordinate));
        if (subLevel == null) {
            Sable.LOGGER.error("Cannot change bounds of nonexistent sub-level plot");
            return;
        }
        LevelPlot plot = subLevel.getPlot();
        BoundingBox3i previousBoundingBox = new BoundingBox3i(plot.getBoundingBox());
        plot.setBoundingBox(this.bounds);
        if (!Objects.equals(previousBoundingBox, this.bounds)) {
            plot.getSubLevel().onPlotBoundsChanged();
        }
    }
}
