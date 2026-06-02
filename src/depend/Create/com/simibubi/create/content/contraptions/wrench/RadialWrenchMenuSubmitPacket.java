/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecs
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.contraptions.wrench;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record RadialWrenchMenuSubmitPacket(BlockPos blockPos, BlockState newState) implements ServerboundPacketPayload
{
    public static final StreamCodec<ByteBuf, RadialWrenchMenuSubmitPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, RadialWrenchMenuSubmitPacket::blockPos, (StreamCodec)CatnipStreamCodecs.BLOCK_STATE, RadialWrenchMenuSubmitPacket::newState, RadialWrenchMenuSubmitPacket::new);

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.RADIAL_WRENCH_MENU_SUBMIT;
    }

    public void handle(ServerPlayer player) {
        Level level = player.level();
        if (!level.getBlockState(this.blockPos).is(this.newState.getBlock())) {
            return;
        }
        BlockState updatedState = Block.updateFromNeighbourShapes((BlockState)this.newState, (LevelAccessor)level, (BlockPos)this.blockPos);
        KineticBlockEntity.switchToBlockState(level, this.blockPos, updatedState);
        IWrenchable.playRotateSound(level, this.blockPos);
    }
}
