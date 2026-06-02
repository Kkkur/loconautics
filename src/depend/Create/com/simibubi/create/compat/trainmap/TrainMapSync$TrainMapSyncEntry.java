/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.codecs.stream.CatnipLargerStreamCodecs
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.minecraft.core.UUIDUtil
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.compat.trainmap;

import com.simibubi.create.compat.trainmap.TrainMapSync;
import java.util.List;
import java.util.UUID;
import net.createmod.catnip.codecs.stream.CatnipLargerStreamCodecs;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public static class TrainMapSync.TrainMapSyncEntry {
    public static final StreamCodec<FriendlyByteBuf, TrainMapSync.TrainMapSyncEntry> STREAM_CODEC = CatnipLargerStreamCodecs.composite((StreamCodec)CatnipStreamCodecBuilders.array((StreamCodec)ByteBufCodecs.FLOAT, Float.class), packet -> packet.positions, (StreamCodec)CatnipStreamCodecBuilders.list((StreamCodec)CatnipStreamCodecBuilders.nullable((StreamCodec)ResourceKey.streamCodec((ResourceKey)Registries.DIMENSION))), packet -> packet.dimensions, TrainMapSync.TrainState.STREAM_CODEC, packet -> packet.state, TrainMapSync.SignalState.STREAM_CODEC, packet -> packet.signalState, (StreamCodec)ByteBufCodecs.BOOL, packet -> packet.fueled, (StreamCodec)ByteBufCodecs.BOOL, packet -> packet.backwards, (StreamCodec)ByteBufCodecs.VAR_INT, packet -> packet.targetStationDistance, (StreamCodec)ByteBufCodecs.STRING_UTF8, packet -> packet.ownerName, (StreamCodec)ByteBufCodecs.STRING_UTF8, packet -> packet.targetStationName, (StreamCodec)CatnipStreamCodecBuilders.nullable((StreamCodec)UUIDUtil.STREAM_CODEC), packet -> packet.waitingForTrain, TrainMapSync.TrainMapSyncEntry::new);
    public Float[] prevPositions;
    public List<ResourceKey<Level>> prevDims;
    public Float[] positions;
    public List<ResourceKey<Level>> dimensions;
    public TrainMapSync.TrainState state = TrainMapSync.TrainState.RUNNING;
    public TrainMapSync.SignalState signalState = TrainMapSync.SignalState.NOT_WAITING;
    public boolean fueled = false;
    public boolean backwards = false;
    public int targetStationDistance = 0;
    public String ownerName = "";
    public String targetStationName = "";
    public UUID waitingForTrain = null;

    public TrainMapSync.TrainMapSyncEntry() {
    }

    public TrainMapSync.TrainMapSyncEntry(Float[] positions, List<ResourceKey<Level>> dimensions, TrainMapSync.TrainState state, TrainMapSync.SignalState signalState, boolean fueled, boolean backwards, int targetStationDistance, String ownerName, String targetStationName, UUID waitingForTrain) {
        this.positions = positions;
        this.dimensions = dimensions;
        this.state = state;
        this.signalState = signalState;
        this.fueled = fueled;
        this.backwards = backwards;
        this.targetStationDistance = targetStationDistance;
        this.ownerName = ownerName;
        this.targetStationName = targetStationName;
        this.waitingForTrain = waitingForTrain;
    }

    public void updateFrom(TrainMapSync.TrainMapSyncEntry other, boolean light) {
        this.prevPositions = this.positions;
        this.prevDims = this.dimensions;
        this.positions = other.positions;
        this.dimensions = other.dimensions;
        this.state = other.state;
        this.signalState = other.signalState;
        this.fueled = other.fueled;
        this.backwards = other.backwards;
        this.targetStationDistance = other.targetStationDistance;
        if (this.prevDims != null) {
            for (int i = 0; i < Math.min(this.prevDims.size(), this.dimensions.size()); ++i) {
                if (this.prevDims.get(i) == this.dimensions.get(i)) continue;
                System.arraycopy(this.positions, i * 6, this.prevPositions, i * 6, 6);
            }
        }
        if (light) {
            return;
        }
        this.ownerName = other.ownerName;
        this.targetStationName = other.targetStationName;
        this.waitingForTrain = other.waitingForTrain;
    }

    public Vec3 getPosition(int carriageIndex, boolean firstBogey, double time) {
        int startIndex = carriageIndex * 6 + (firstBogey ? 0 : 3);
        if (this.positions == null || this.positions.length <= startIndex + 2) {
            return Vec3.ZERO;
        }
        Vec3 position = new Vec3((double)this.positions[startIndex].floatValue(), (double)this.positions[startIndex + 1].floatValue(), (double)this.positions[startIndex + 2].floatValue());
        if (this.prevPositions == null || this.prevPositions.length <= startIndex + 2) {
            return position;
        }
        Vec3 prevPosition = new Vec3((double)this.prevPositions[startIndex].floatValue(), (double)this.prevPositions[startIndex + 1].floatValue(), (double)this.prevPositions[startIndex + 2].floatValue());
        return prevPosition.lerp(position, time);
    }
}
