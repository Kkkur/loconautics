/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.logistics.packagerLink;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.packagerLink.PackagerLinkBlockEntity;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record WiFiEffectPacket(BlockPos pos) implements ClientboundPacketPayload
{
    public static final StreamCodec<ByteBuf, WiFiEffectPacket> STREAM_CODEC = BlockPos.STREAM_CODEC.map(WiFiEffectPacket::new, WiFiEffectPacket::pos);

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.PACKAGER_LINK_EFFECT;
    }

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        SmartBlockEntity plbe;
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(this.pos);
        if (blockEntity instanceof PackagerLinkBlockEntity) {
            plbe = (PackagerLinkBlockEntity)blockEntity;
            ((PackagerLinkBlockEntity)plbe).playEffect();
        }
        if (blockEntity instanceof StockTickerBlockEntity) {
            plbe = (StockTickerBlockEntity)blockEntity;
            ((StockTickerBlockEntity)plbe).playEffect();
        }
    }

    public static void send(Level level, BlockPos pos) {
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            CatnipServices.NETWORK.sendToClientsAround(serverLevel, (Vec3i)pos, 32.0, (CustomPacketPayload)new WiFiEffectPacket(pos));
        }
    }
}
