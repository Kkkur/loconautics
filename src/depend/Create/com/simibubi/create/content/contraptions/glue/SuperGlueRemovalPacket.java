/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 */
package com.simibubi.create.content.contraptions.glue;

import com.simibubi.create.AllPackets;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public record SuperGlueRemovalPacket(int entityId, BlockPos soundSource) implements ServerboundPacketPayload
{
    public static final StreamCodec<ByteBuf, SuperGlueRemovalPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.INT, SuperGlueRemovalPacket::entityId, (StreamCodec)BlockPos.STREAM_CODEC, SuperGlueRemovalPacket::soundSource, SuperGlueRemovalPacket::new);

    public void handle(ServerPlayer player) {
        Entity entity = player.level().getEntity(this.entityId);
        if (!(entity instanceof SuperGlueEntity)) {
            return;
        }
        SuperGlueEntity superGlue = (SuperGlueEntity)entity;
        double range = 32.0;
        if (player.distanceToSqr(superGlue.position()) > range * range) {
            return;
        }
        AllSoundEvents.SLIME_ADDED.play(player.level(), null, (Vec3i)this.soundSource, 0.5f, 0.5f);
        superGlue.spawnParticles();
        entity.discard();
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.GLUE_REMOVED;
    }
}
