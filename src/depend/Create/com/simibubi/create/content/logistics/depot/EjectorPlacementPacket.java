/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.logistics.depot;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.depot.EjectorBlock;
import com.simibubi.create.content.logistics.depot.EjectorBlockEntity;
import com.simibubi.create.content.logistics.depot.EjectorTargetHandler;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record EjectorPlacementPacket(int h, int v, BlockPos pos, Direction facing) implements ServerboundPacketPayload
{
    public static final StreamCodec<ByteBuf, EjectorPlacementPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.INT, EjectorPlacementPacket::h, (StreamCodec)ByteBufCodecs.INT, EjectorPlacementPacket::v, (StreamCodec)BlockPos.STREAM_CODEC, EjectorPlacementPacket::pos, (StreamCodec)Direction.STREAM_CODEC, EjectorPlacementPacket::facing, EjectorPlacementPacket::new);

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.PLACE_EJECTOR;
    }

    public void handle(ServerPlayer player) {
        Level world = player.level();
        if (!world.isLoaded(this.pos)) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(this.pos);
        BlockState state = world.getBlockState(this.pos);
        if (blockEntity instanceof EjectorBlockEntity) {
            ((EjectorBlockEntity)blockEntity).setTarget(this.h, this.v);
        }
        if (AllBlocks.WEIGHTED_EJECTOR.has(state)) {
            world.setBlockAndUpdate(this.pos, (BlockState)state.setValue(EjectorBlock.HORIZONTAL_FACING, (Comparable)this.facing));
        }
    }

    public record ClientBoundRequest(BlockPos pos) implements ClientboundPacketPayload
    {
        public static final StreamCodec<ByteBuf, ClientBoundRequest> STREAM_CODEC = BlockPos.STREAM_CODEC.map(ClientBoundRequest::new, ClientBoundRequest::pos);

        public BasePacketPayload.PacketTypeProvider getTypeProvider() {
            return AllPackets.S_PLACE_EJECTOR;
        }

        @OnlyIn(value=Dist.CLIENT)
        public void handle(LocalPlayer player) {
            EjectorTargetHandler.flushSettings(this.pos);
        }
    }
}
