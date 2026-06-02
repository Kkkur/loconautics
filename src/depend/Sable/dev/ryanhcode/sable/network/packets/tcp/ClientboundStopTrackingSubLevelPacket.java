/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.handler.PacketContext
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
import dev.ryanhcode.sable.network.tcp.SableTCPPacket;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import foundry.veil.api.network.handler.PacketContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public record ClientboundStopTrackingSubLevelPacket(long plotCoordinate) implements SableTCPPacket
{
    public static final CustomPacketPayload.Type<ClientboundStopTrackingSubLevelPacket> TYPE = new CustomPacketPayload.Type(Sable.sablePath("stop_tracking_sub_level"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundStopTrackingSubLevelPacket> CODEC = StreamCodec.of((buf, value) -> value.write((FriendlyByteBuf)buf), ClientboundStopTrackingSubLevelPacket::read);

    private static ClientboundStopTrackingSubLevelPacket read(FriendlyByteBuf buf) {
        return new ClientboundStopTrackingSubLevelPacket(buf.readLong());
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeLong(this.plotCoordinate);
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(PacketContext context) {
        int chunkZ;
        Level level = context.level();
        SubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container == null) {
            Sable.LOGGER.error("Received a sub-level tracking packet for a level without a sub-level container");
            return;
        }
        int chunkX = ChunkPos.getX((long)this.plotCoordinate);
        if (container.getSubLevel(chunkX, chunkZ = ChunkPos.getZ((long)this.plotCoordinate)) == null) {
            Sable.LOGGER.error("Received a sub-level tracking removal packet for unknown sub-level: {}, {}", (Object)chunkX, (Object)chunkZ);
            return;
        }
        container.removeSubLevel(chunkX, chunkZ, SubLevelRemovalReason.REMOVED);
    }
}
