/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.createmod.catnip.outliner.Outliner
 *  net.createmod.catnip.render.BindableTexture
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.infrastructure.command;

import com.simibubi.create.AllPackets;
import com.simibubi.create.AllSpecialTextures;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.render.BindableTexture;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record HighlightPacket(BlockPos pos) implements ClientboundPacketPayload
{
    public static final StreamCodec<ByteBuf, HighlightPacket> STREAM_CODEC = BlockPos.STREAM_CODEC.map(HighlightPacket::new, p -> p.pos);

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        if (!player.clientLevel.isLoaded(this.pos)) {
            return;
        }
        Outliner.getInstance().showAABB((Object)"highlightCommand", Shapes.block().bounds().move(this.pos), 200).lineWidth(0.03125f).colored(0xEEEEEE).withFaceTexture((BindableTexture)AllSpecialTextures.SELECTION);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.BLOCK_HIGHLIGHT;
    }
}
