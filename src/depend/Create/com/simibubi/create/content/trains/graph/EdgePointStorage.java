/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 */
package com.simibubi.create.content.trains.graph;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class EdgePointStorage {
    private Map<EdgePointType<?>, Map<UUID, TrackEdgePoint>> pointsByType = new HashMap();

    public <T extends TrackEdgePoint> void put(EdgePointType<T> type, TrackEdgePoint point) {
        this.getMap(type).put(point.getId(), point);
    }

    public <T extends TrackEdgePoint> T get(EdgePointType<T> type, UUID id) {
        return (T)this.getMap(type).get(id);
    }

    public <T extends TrackEdgePoint> T remove(EdgePointType<T> type, UUID id) {
        return (T)this.getMap(type).remove(id);
    }

    public <T extends TrackEdgePoint> Collection<T> values(EdgePointType<T> type) {
        return this.getMap(type).values().stream().map(e -> e).toList();
    }

    public Map<UUID, TrackEdgePoint> getMap(EdgePointType<? extends TrackEdgePoint> type) {
        return this.pointsByType.computeIfAbsent(type, t -> new HashMap());
    }

    public void tick(TrackGraph graph, boolean preTrains) {
        for (Map<UUID, TrackEdgePoint> map : this.pointsByType.values()) {
            for (TrackEdgePoint point : map.values()) {
                point.tick(graph, preTrains);
            }
        }
    }

    public void transferAll(TrackGraph target, EdgePointStorage other) {
        this.pointsByType.forEach((type, map) -> {
            other.getMap((EdgePointType<? extends TrackEdgePoint>)type).putAll((Map<UUID, TrackEdgePoint>)map);
            map.values().forEach(ep -> Create.RAILWAYS.sync.pointAdded(target, (TrackEdgePoint)ep));
        });
        this.pointsByType.clear();
    }

    public CompoundTag write(HolderLookup.Provider registries, DimensionPalette dimensions) {
        CompoundTag nbt = new CompoundTag();
        for (Map.Entry<EdgePointType<?>, Map<UUID, TrackEdgePoint>> entry : this.pointsByType.entrySet()) {
            EdgePointType<?> type = entry.getKey();
            ListTag list = NBTHelper.writeCompoundList(entry.getValue().values(), edgePoint -> {
                CompoundTag tag = new CompoundTag();
                edgePoint.write(tag, registries, dimensions);
                return tag;
            });
            nbt.put(type.getId().toString(), (Tag)list);
        }
        return nbt;
    }

    public void read(CompoundTag nbt, HolderLookup.Provider registries, DimensionPalette dimensions) {
        for (EdgePointType<?> type : EdgePointType.TYPES.values()) {
            ListTag list = nbt.getList(type.getId().toString(), 10);
            Map<UUID, TrackEdgePoint> map = this.getMap(type);
            NBTHelper.iterateCompoundList((ListTag)list, tag -> {
                Object edgePoint = type.create();
                ((TrackEdgePoint)edgePoint).read((CompoundTag)tag, registries, false, dimensions);
                map.put(((TrackEdgePoint)edgePoint).getId(), (TrackEdgePoint)edgePoint);
            });
        }
    }
}
