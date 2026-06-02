/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.level.Level
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.contraptions.glue;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.contraptions.glue.SuperGlueItem;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record GlueEffectPacket(BlockPos pos, Direction direction, boolean fullBlock) implements ClientboundPacketPayload
{
    public static final StreamCodec<ByteBuf, GlueEffectPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, GlueEffectPacket::pos, (StreamCodec)Direction.STREAM_CODEC, GlueEffectPacket::direction, (StreamCodec)ByteBufCodecs.BOOL, GlueEffectPacket::fullBlock, GlueEffectPacket::new);

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        if (!player.blockPosition().closerThan((Vec3i)this.pos, 100.0)) {
            return;
        }
        SuperGlueItem.spawnParticles((Level)player.clientLevel, this.pos, this.direction, this.fullBlock);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.GLUE_EFFECT;
    }
}
