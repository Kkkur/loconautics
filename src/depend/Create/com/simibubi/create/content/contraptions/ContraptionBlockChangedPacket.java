/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecs
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.contraptions;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record ContraptionBlockChangedPacket(int entityId, BlockPos localPos, BlockState newState) implements ClientboundPacketPayload
{
    public static final StreamCodec<ByteBuf, ContraptionBlockChangedPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.INT, ContraptionBlockChangedPacket::entityId, (StreamCodec)BlockPos.STREAM_CODEC, ContraptionBlockChangedPacket::localPos, (StreamCodec)CatnipStreamCodecs.BLOCK_STATE, ContraptionBlockChangedPacket::newState, ContraptionBlockChangedPacket::new);

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        AbstractContraptionEntity.handleBlockChangedPacket(this);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CONTRAPTION_BLOCK_CHANGED;
    }
}
