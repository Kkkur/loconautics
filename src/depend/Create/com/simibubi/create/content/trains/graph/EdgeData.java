/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.graph;

import com.google.common.base.Objects;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackEdgeIntersection;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.signal.SignalEdgeGroup;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class EdgeData {
    public static final UUID passiveGroup = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private UUID singleSignalGroup;
    private List<TrackEdgePoint> points;
    private List<TrackEdgeIntersection> intersections;
    private TrackEdge edge;

    public EdgeData(TrackEdge edge) {
        this.edge = edge;
        this.points = new ArrayList<TrackEdgePoint>();
        this.intersections = new ArrayList<TrackEdgeIntersection>();
        this.singleSignalGroup = passiveGroup;
    }

    public boolean hasSignalBoundaries() {
        return this.singleSignalGroup == null;
    }

    public UUID getSingleSignalGroup() {
        return this.singleSignalGroup;
    }

    public void setSingleSignalGroup(@Nullable TrackGraph graph, UUID singleSignalGroup) {
        if (graph != null && !Objects.equal((Object)singleSignalGroup, (Object)this.singleSignalGroup)) {
            this.refreshIntersectingSignalGroups(graph);
        }
        this.singleSignalGroup = singleSignalGroup;
    }

    public void refreshIntersectingSignalGroups(TrackGraph graph) {
        Map<UUID, SignalEdgeGroup> groups = Create.RAILWAYS.signalEdgeGroups;
        for (TrackEdgeIntersection intersection : this.intersections) {
            SignalEdgeGroup group;
            if (intersection.groupId == null || (group = groups.get(intersection.groupId)) == null) continue;
            group.removeIntersection(intersection.id);
        }
        if (this.hasIntersections()) {
            graph.deferIntersectionUpdate(this.edge);
        }
    }

    public boolean hasPoints() {
        return !this.points.isEmpty();
    }

    public boolean hasIntersections() {
        return !this.intersections.isEmpty();
    }

    public List<TrackEdgeIntersection> getIntersections() {
        return this.intersections;
    }

    public void addIntersection(TrackGraph graph, UUID id, double position, TrackNode target1, TrackNode target2, double targetPosition) {
        TrackNodeLocation loc1 = target1.getLocation();
        TrackNodeLocation loc2 = target2.getLocation();
        for (TrackEdgeIntersection existing : this.intersections) {
            if (!existing.isNear(position) || !existing.targets(loc1, loc2)) continue;
            return;
        }
        TrackEdgeIntersection intersection = new TrackEdgeIntersection();
        intersection.id = id;
        intersection.location = position;
        intersection.target = Couple.create((Object)((Object)loc1), (Object)((Object)loc2));
        intersection.targetLocation = targetPosition;
        this.intersections.add(intersection);
        graph.deferIntersectionUpdate(this.edge);
    }

    public void removeIntersection(TrackGraph graph, UUID id) {
        this.refreshIntersectingSignalGroups(graph);
        Iterator<TrackEdgeIntersection> iterator = this.intersections.iterator();
        while (iterator.hasNext()) {
            TrackEdgeIntersection existing = iterator.next();
            if (!existing.id.equals(id)) continue;
            iterator.remove();
        }
    }

    public UUID getGroupAtPosition(TrackGraph graph, double position) {
        if (!this.hasSignalBoundaries()) {
            return this.getEffectiveEdgeGroupId(graph);
        }
        SignalBoundary firstSignal = this.next(EdgePointType.SIGNAL, 0.0);
        if (firstSignal == null) {
            return null;
        }
        UUID currentGroup = firstSignal.getGroup(this.edge.node1);
        for (TrackEdgePoint trackEdgePoint : this.getPoints()) {
            if (!(trackEdgePoint instanceof SignalBoundary)) continue;
            SignalBoundary sb = (SignalBoundary)trackEdgePoint;
            if (sb.getLocationOn(this.edge) >= position) {
                return currentGroup;
            }
            currentGroup = sb.getGroup(this.edge.node2);
        }
        return currentGroup;
    }

    public List<TrackEdgePoint> getPoints() {
        return this.points;
    }

    public UUID getEffectiveEdgeGroupId(TrackGraph graph) {
        return this.singleSignalGroup == null ? null : (this.singleSignalGroup.equals(passiveGroup) ? graph.id : this.singleSignalGroup);
    }

    public void removePoint(TrackGraph graph, TrackEdgePoint point) {
        this.points.remove(point);
        if (point.getType() == EdgePointType.SIGNAL) {
            boolean noSignalsRemaining = this.next(point.getType(), 0.0) == null;
            this.setSingleSignalGroup(graph, noSignalsRemaining ? passiveGroup : null);
        }
    }

    public <T extends TrackEdgePoint> void addPoint(TrackGraph graph, TrackEdgePoint point) {
        int i;
        if (point.getType() == EdgePointType.SIGNAL) {
            this.setSingleSignalGroup(graph, null);
        }
        double locationOn = point.getLocationOn(this.edge);
        for (i = 0; i < this.points.size() && !(this.points.get(i).getLocationOn(this.edge) > locationOn); ++i) {
        }
        this.points.add(i, point);
    }

    @Nullable
    public <T extends TrackEdgePoint> T next(EdgePointType<T> type, double minPosition) {
        for (TrackEdgePoint point : this.points) {
            if (point.getType() != type || !(point.getLocationOn(this.edge) > minPosition)) continue;
            return (T)point;
        }
        return null;
    }

    @Nullable
    public TrackEdgePoint next(double minPosition) {
        for (TrackEdgePoint point : this.points) {
            if (!(point.getLocationOn(this.edge) > minPosition)) continue;
            return point;
        }
        return null;
    }

    @Nullable
    public <T extends TrackEdgePoint> T get(EdgePointType<T> type, double exactPosition) {
        T next = this.next(type, exactPosition - 0.5);
        if (next != null && Mth.equal((double)((TrackEdgePoint)next).getLocationOn(this.edge), (double)exactPosition)) {
            return next;
        }
        return null;
    }

    public CompoundTag write(DimensionPalette dimensions) {
        CompoundTag nbt = new CompoundTag();
        if (this.singleSignalGroup == passiveGroup) {
            NBTHelper.putMarker((CompoundTag)nbt, (String)"PassiveGroup");
        } else if (this.singleSignalGroup != null) {
            nbt.putUUID("SignalGroup", this.singleSignalGroup);
        }
        if (this.hasPoints()) {
            nbt.put("Points", (Tag)NBTHelper.writeCompoundList(this.points, point -> {
                CompoundTag tag = new CompoundTag();
                tag.putUUID("Id", point.id);
                tag.putString("Type", point.getType().getId().toString());
                return tag;
            }));
        }
        if (this.hasIntersections()) {
            nbt.put("Intersections", (Tag)NBTHelper.writeCompoundList(this.intersections, tei -> tei.write(dimensions)));
        }
        return nbt;
    }

    public static EdgeData read(CompoundTag nbt, TrackEdge edge, TrackGraph graph, DimensionPalette dimensions) {
        EdgeData data = new EdgeData(edge);
        if (nbt.contains("SignalGroup")) {
            data.singleSignalGroup = nbt.getUUID("SignalGroup");
        } else if (!nbt.contains("PassiveGroup")) {
            data.singleSignalGroup = null;
        }
        if (nbt.contains("Points")) {
            NBTHelper.iterateCompoundList((ListTag)nbt.getList("Points", 10), tag -> {
                ResourceLocation location = ResourceLocation.parse((String)tag.getString("Type"));
                EdgePointType<?> type = EdgePointType.TYPES.get(location);
                if (type == null || !tag.contains("Id")) {
                    return;
                }
                Object point = graph.getPoint(type, tag.getUUID("Id"));
                if (point != null) {
                    data.points.add((TrackEdgePoint)point);
                }
            });
        }
        if (nbt.contains("Intersections")) {
            data.intersections = NBTHelper.readCompoundList((ListTag)nbt.getList("Intersections", 10), c -> TrackEdgeIntersection.read(c, dimensions));
        }
        return data;
    }
}
