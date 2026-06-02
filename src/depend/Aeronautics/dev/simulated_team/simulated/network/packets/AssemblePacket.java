/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.handler.ServerPacketContext
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.network.packets;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.physics_assembler.PhysicsAssemblerBlockEntity;
import foundry.veil.api.network.handler.ServerPacketContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

public record AssemblePacket(BlockPos pos) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<AssemblePacket> TYPE = new CustomPacketPayload.Type(Simulated.path("assemble"));
    public static final StreamCodec<ByteBuf, AssemblePacket> CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, packet -> packet.pos, AssemblePacket::new);

    public CustomPacketPayload.Type<AssemblePacket> type() {
        return TYPE;
    }

    public void handle(ServerPacketContext context) {
        ServerLevel level = (ServerLevel)context.player().level();
        BlockEntity blockEntity = level.getBlockEntity(this.pos);
        if (blockEntity instanceof PhysicsAssemblerBlockEntity) {
            PhysicsAssemblerBlockEntity assembler = (PhysicsAssemblerBlockEntity)blockEntity;
            assembler.assembleOrDisassemble();
        }
    }
}
