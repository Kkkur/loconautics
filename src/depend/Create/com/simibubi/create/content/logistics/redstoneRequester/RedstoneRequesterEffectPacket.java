/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.logistics.redstoneRequester;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterBlockEntity;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record RedstoneRequesterEffectPacket(BlockPos pos, boolean success) implements ClientboundPacketPayload
{
    public static final StreamCodec<ByteBuf, RedstoneRequesterEffectPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, RedstoneRequesterEffectPacket::pos, (StreamCodec)ByteBufCodecs.BOOL, RedstoneRequesterEffectPacket::success, RedstoneRequesterEffectPacket::new);

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.REDSTONE_REQUESTER_EFFECT;
    }

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(this.pos);
        if (blockEntity instanceof RedstoneRequesterBlockEntity) {
            RedstoneRequesterBlockEntity plbe = (RedstoneRequesterBlockEntity)blockEntity;
            plbe.playEffect(this.success);
        }
    }
}
