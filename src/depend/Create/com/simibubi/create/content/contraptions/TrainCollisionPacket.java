/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 */
package com.simibubi.create.content.contraptions;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.foundation.damageTypes.CreateDamageSources;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public record TrainCollisionPacket(int damage, int contraptionEntityId) implements ServerboundPacketPayload
{
    public static final StreamCodec<ByteBuf, TrainCollisionPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.INT, TrainCollisionPacket::damage, (StreamCodec)ByteBufCodecs.INT, TrainCollisionPacket::contraptionEntityId, TrainCollisionPacket::new);

    public void handle(ServerPlayer player) {
        Level level = player.level();
        Entity entity = level.getEntity(this.contraptionEntityId);
        if (!(entity instanceof CarriageContraptionEntity)) {
            return;
        }
        CarriageContraptionEntity cce = (CarriageContraptionEntity)entity;
        player.hurt(CreateDamageSources.runOver(level, cce), (float)this.damage);
        player.level().playSound((Player)player, entity.blockPosition(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.NEUTRAL, 1.0f, 0.75f);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.TRAIN_COLLISION;
    }
}
