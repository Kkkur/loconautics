/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.AABB
 */
package com.simibubi.create.content.contraptions.glue;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import com.simibubi.create.content.contraptions.glue.SuperGlueSelectionHelper;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import io.netty.buffer.ByteBuf;
import java.util.Set;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public record SuperGlueSelectionPacket(BlockPos from, BlockPos to) implements ServerboundPacketPayload
{
    public static final StreamCodec<ByteBuf, SuperGlueSelectionPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, SuperGlueSelectionPacket::from, (StreamCodec)BlockPos.STREAM_CODEC, SuperGlueSelectionPacket::to, SuperGlueSelectionPacket::new);

    public void handle(ServerPlayer player) {
        if (!player.canInteractWithBlock(this.to, 2.0)) {
            return;
        }
        if (!this.to.closerThan((Vec3i)this.from, 25.0)) {
            return;
        }
        Set<BlockPos> group = SuperGlueSelectionHelper.searchGlueGroup(player.level(), this.from, this.to, false);
        if (group == null) {
            return;
        }
        if (!group.contains(this.to)) {
            return;
        }
        if (!SuperGlueSelectionHelper.collectGlueFromInventory((Player)player, 1, true)) {
            return;
        }
        AABB bb = SuperGlueEntity.span(this.from, this.to);
        SuperGlueSelectionHelper.collectGlueFromInventory((Player)player, 1, false);
        SuperGlueEntity entity = new SuperGlueEntity(player.level(), bb);
        player.level().addFreshEntity((Entity)entity);
        entity.spawnParticles();
        AllAdvancements.SUPER_GLUE.awardTo((Player)player);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.GLUE_IN_AREA;
    }
}
