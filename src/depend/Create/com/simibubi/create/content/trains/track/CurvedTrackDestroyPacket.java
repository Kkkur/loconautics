/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.trains.track;

import com.simibubi.create.AllPackets;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.simibubi.create.content.trains.track.TrackPropagator;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import com.simibubi.create.infrastructure.config.AllConfigs;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CurvedTrackDestroyPacket
extends BlockEntityConfigurationPacket<TrackBlockEntity> {
    public static final StreamCodec<ByteBuf, CurvedTrackDestroyPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, packet -> packet.pos, (StreamCodec)BlockPos.STREAM_CODEC, packet -> packet.targetPos, (StreamCodec)BlockPos.STREAM_CODEC, packet -> packet.soundSource, (StreamCodec)ByteBufCodecs.BOOL, packet -> packet.wrench, CurvedTrackDestroyPacket::new);
    private final BlockPos targetPos;
    private final BlockPos soundSource;
    private final boolean wrench;

    public CurvedTrackDestroyPacket(BlockPos pos, BlockPos targetPos, BlockPos soundSource, boolean wrench) {
        super(pos);
        this.targetPos = targetPos;
        this.soundSource = soundSource;
        this.wrench = wrench;
    }

    @Override
    protected void applySettings(ServerPlayer player, TrackBlockEntity be) {
        int verifyDistance = (Integer)AllConfigs.server().trains.maxTrackPlacementLength.get() * 4;
        if (!player.canInteractWithBlock(be.getBlockPos(), (double)verifyDistance)) {
            Create.LOGGER.warn("{} too far away from destroyed Curve track", (Object)player.getScoreboardName());
            return;
        }
        Level level = be.getLevel();
        BezierConnection bezierConnection = be.getConnections().get(this.targetPos);
        be.removeConnection(this.targetPos);
        BlockEntity blockEntity = level.getBlockEntity(this.targetPos);
        if (blockEntity instanceof TrackBlockEntity) {
            TrackBlockEntity other = (TrackBlockEntity)blockEntity;
            other.removeConnection(this.pos);
        }
        BlockState blockState = be.getBlockState();
        TrackPropagator.onRailRemoved((LevelAccessor)level, this.pos, blockState);
        if (this.wrench) {
            AllSoundEvents.WRENCH_REMOVE.playOnServer(player.level(), (Vec3i)this.soundSource, 1.0f, level.random.nextFloat() * 0.5f + 0.5f);
            if (!player.isCreative() && bezierConnection != null) {
                bezierConnection.addItemsToPlayer((Player)player);
            }
        } else if (!player.isCreative() && bezierConnection != null) {
            bezierConnection.spawnItems(level);
        }
        bezierConnection.spawnDestroyParticles(level);
        SoundType soundtype = blockState.getSoundType((LevelReader)level, this.pos, (Entity)player);
        if (soundtype == null) {
            return;
        }
        level.playSound(null, this.soundSource, soundtype.getBreakSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0f) / 2.0f, soundtype.getPitch() * 0.8f);
    }

    @Override
    protected int maxRange() {
        return (Integer)AllConfigs.server().trains.maxTrackPlacementLength.get() + 16;
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.DESTROY_CURVED_TRACK;
    }
}
