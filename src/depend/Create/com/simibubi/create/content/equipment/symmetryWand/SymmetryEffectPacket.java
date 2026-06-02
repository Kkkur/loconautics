/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.equipment.symmetryWand;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.equipment.symmetryWand.SymmetryHandler;
import io.netty.buffer.ByteBuf;
import java.util.List;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record SymmetryEffectPacket(BlockPos mirror, List<BlockPos> positions) implements ClientboundPacketPayload
{
    public static final StreamCodec<ByteBuf, SymmetryEffectPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, SymmetryEffectPacket::mirror, (StreamCodec)CatnipStreamCodecBuilders.list((StreamCodec)BlockPos.STREAM_CODEC), SymmetryEffectPacket::positions, SymmetryEffectPacket::new);

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.SYMMETRY_EFFECT;
    }

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        if (player.position().distanceTo(Vec3.atLowerCornerOf((Vec3i)this.mirror)) > 100.0) {
            return;
        }
        for (BlockPos to : this.positions) {
            SymmetryHandler.drawEffect(this.mirror, to);
        }
    }
}
