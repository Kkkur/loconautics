/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  dev.ryanhcode.sable.util.SableBufferUtils
 *  foundry.veil.api.network.handler.ClientPacketContext
 *  io.netty.buffer.ByteBuf
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.network.packets.rope;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.ryanhcode.sable.util.SableBufferUtils;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.rope.RopeStrandHolderBehavior;
import foundry.veil.api.network.handler.ClientPacketContext;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public record ClientboundRopeDataPacket(int interpolationTick, BlockPos ownerPos, UUID uuid, List<Vector3d> points, @Nullable BlockPos startAttachmentPos, @Nullable BlockPos endAttachmentPos) implements CustomPacketPayload
{
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundRopeDataPacket> CODEC = StreamCodec.of((buf, value) -> value.write((RegistryFriendlyByteBuf)buf), ClientboundRopeDataPacket::read);
    public static CustomPacketPayload.Type<ClientboundRopeDataPacket> TYPE = new CustomPacketPayload.Type(Simulated.path("rope_data"));

    private static ClientboundRopeDataPacket read(RegistryFriendlyByteBuf buf) {
        int interpolationTick = buf.readInt();
        BlockPos ownerPos = buf.readBlockPos();
        UUID uuid = buf.readUUID();
        int size = buf.readInt();
        ObjectArrayList points = new ObjectArrayList(size);
        for (int i = 0; i < size; ++i) {
            points.add(SableBufferUtils.read((ByteBuf)buf, (Vector3d)new Vector3d()));
        }
        BlockPos startAttachment = buf.readBoolean() ? buf.readBlockPos() : null;
        BlockPos endAttachment = buf.readBoolean() ? buf.readBlockPos() : null;
        return new ClientboundRopeDataPacket(interpolationTick, ownerPos, uuid, (List<Vector3d>)points, startAttachment, endAttachment);
    }

    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.interpolationTick);
        buf.writeBlockPos(this.ownerPos);
        buf.writeUUID(this.uuid);
        buf.writeInt(this.points.size());
        for (Vector3dc vector3dc : this.points) {
            SableBufferUtils.write((ByteBuf)buf, (Vector3dc)vector3dc);
        }
        buf.writeBoolean(this.startAttachmentPos != null);
        if (this.startAttachmentPos != null) {
            buf.writeBlockPos(this.startAttachmentPos);
        }
        buf.writeBoolean(this.endAttachmentPos != null);
        if (this.endAttachmentPos != null) {
            buf.writeBlockPos(this.endAttachmentPos);
        }
    }

    public void handle(ClientPacketContext context) {
        LocalPlayer player = context.player();
        Level level = player.level();
        BlockEntity blockEntity = level.getBlockEntity(this.ownerPos);
        if (!(blockEntity instanceof SmartBlockEntity)) {
            return;
        }
        SmartBlockEntity smartBlockEntity = (SmartBlockEntity)blockEntity;
        RopeStrandHolderBehavior ropeHolder = (RopeStrandHolderBehavior)smartBlockEntity.getBehaviour(RopeStrandHolderBehavior.TYPE);
        if (ropeHolder == null) {
            return;
        }
        ropeHolder.receiveClientStrand(this.interpolationTick, this.points, this.uuid, this.startAttachmentPos, this.endAttachmentPos);
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
