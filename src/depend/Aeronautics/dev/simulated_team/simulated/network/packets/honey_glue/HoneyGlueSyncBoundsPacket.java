/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.handler.ClientPacketContext
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 */
package dev.simulated_team.simulated.network.packets.honey_glue;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.entities.honey_glue.HoneyGlueEntity;
import foundry.veil.api.network.handler.ClientPacketContext;
import java.util.UUID;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public record HoneyGlueSyncBoundsPacket(AABB bounds, int honeyGlueId, UUID uuid) implements CustomPacketPayload
{
    public static CustomPacketPayload.Type<HoneyGlueSyncBoundsPacket> TYPE = new CustomPacketPayload.Type(Simulated.path("honey_glue_sync"));
    public static StreamCodec<RegistryFriendlyByteBuf, HoneyGlueSyncBoundsPacket> CODEC = StreamCodec.of(HoneyGlueSyncBoundsPacket::writeToBuf, HoneyGlueSyncBoundsPacket::readFromBuf);

    public static void writeToBuf(RegistryFriendlyByteBuf buf, HoneyGlueSyncBoundsPacket packet) {
        HoneyGlueSyncBoundsPacket.writeAABB(buf, packet.bounds);
        buf.writeInt(packet.honeyGlueId());
        buf.writeBoolean(packet.uuid != null);
        if (packet.uuid != null) {
            buf.writeUUID(packet.uuid);
        }
    }

    public static HoneyGlueSyncBoundsPacket readFromBuf(RegistryFriendlyByteBuf buf) {
        AABB serializedBounds = new AABB(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble());
        int honeyGlueId = buf.readInt();
        UUID uuid = null;
        if (buf.readBoolean()) {
            uuid = buf.readUUID();
        }
        return new HoneyGlueSyncBoundsPacket(serializedBounds, honeyGlueId, uuid);
    }

    public static void writeAABB(RegistryFriendlyByteBuf byteBuf, AABB bb) {
        byteBuf.writeDouble(bb.minX);
        byteBuf.writeDouble(bb.minY);
        byteBuf.writeDouble(bb.minZ);
        byteBuf.writeDouble(bb.maxX);
        byteBuf.writeDouble(bb.maxY);
        byteBuf.writeDouble(bb.maxZ);
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(ClientPacketContext context) {
        if (this.uuid != null && this.uuid.equals(context.player().getUUID())) {
            return;
        }
        Level level = context.level();
        Entity entity = level.getEntity(this.honeyGlueId);
        if (entity instanceof HoneyGlueEntity) {
            HoneyGlueEntity honeyGlue = (HoneyGlueEntity)entity;
            honeyGlue.setBounds(this.bounds);
        }
    }
}
