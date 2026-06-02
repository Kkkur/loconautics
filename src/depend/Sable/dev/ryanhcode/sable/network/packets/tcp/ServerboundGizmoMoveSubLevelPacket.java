/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.handler.PacketContext
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.server.level.ServerLevel
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.network.packets.tcp;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.network.tcp.SableTCPPacket;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.util.SableBufferUtils;
import foundry.veil.api.network.handler.PacketContext;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public record ServerboundGizmoMoveSubLevelPacket(UUID subLevel, Vector3d position) implements SableTCPPacket
{
    public static final CustomPacketPayload.Type<ServerboundGizmoMoveSubLevelPacket> TYPE = new CustomPacketPayload.Type(Sable.sablePath("gizmo_move_sub_level"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundGizmoMoveSubLevelPacket> CODEC = StreamCodec.of((buf, value) -> value.write((FriendlyByteBuf)buf), ServerboundGizmoMoveSubLevelPacket::read);

    private static ServerboundGizmoMoveSubLevelPacket read(FriendlyByteBuf buf) {
        return new ServerboundGizmoMoveSubLevelPacket(buf.readUUID(), SableBufferUtils.read((ByteBuf)buf, new Vector3d()));
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeUUID(this.subLevel);
        SableBufferUtils.write((ByteBuf)buf, (Vector3dc)this.position);
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(PacketContext context) {
        ServerLevel level = (ServerLevel)context.level();
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        if (!context.player().hasPermissions(1)) {
            Sable.LOGGER.warn("Player {} tried to move a sub-level with gizmo without permission", (Object)context.player().getGameProfile().getName());
            return;
        }
        if (container == null) {
            Sable.LOGGER.error("Received a gizmo movement packet for a level without a sub-level container");
            return;
        }
        SubLevel subLevel = container.getSubLevel(this.subLevel);
        container.physicsSystem().getPipeline().teleport((ServerSubLevel)subLevel, (Vector3dc)this.position, (Quaterniondc)subLevel.logicalPose().orientation());
    }
}
