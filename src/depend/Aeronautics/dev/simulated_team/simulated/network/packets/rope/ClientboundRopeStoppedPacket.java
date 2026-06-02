/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  foundry.veil.api.network.handler.ClientPacketContext
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.network.packets.rope;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.rope.RopeStrandHolderBehavior;
import foundry.veil.api.network.handler.ClientPacketContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public record ClientboundRopeStoppedPacket(BlockPos ownerPos) implements CustomPacketPayload
{
    public static final StreamCodec<ByteBuf, ClientboundRopeStoppedPacket> CODEC = BlockPos.STREAM_CODEC.map(ClientboundRopeStoppedPacket::new, ClientboundRopeStoppedPacket::ownerPos);
    public static CustomPacketPayload.Type<ClientboundRopeStoppedPacket> TYPE = new CustomPacketPayload.Type(Simulated.path("rope_stopped"));

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
        ropeHolder.receiveClientStrandStopped();
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
