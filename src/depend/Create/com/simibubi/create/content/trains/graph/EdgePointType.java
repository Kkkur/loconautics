/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 */
package com.simibubi.create.content.trains.graph;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.observer.TrackObserver;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.content.trains.station.GlobalStation;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class EdgePointType<T extends TrackEdgePoint> {
    public static final Map<ResourceLocation, EdgePointType<?>> TYPES = new HashMap();
    private ResourceLocation id;
    private Supplier<T> factory;
    public static final EdgePointType<SignalBoundary> SIGNAL = EdgePointType.register(Create.asResource("signal"), SignalBoundary::new);
    public static final EdgePointType<GlobalStation> STATION = EdgePointType.register(Create.asResource("station"), GlobalStation::new);
    public static final EdgePointType<TrackObserver> OBSERVER = EdgePointType.register(Create.asResource("observer"), TrackObserver::new);

    public static <T extends TrackEdgePoint> EdgePointType<T> register(ResourceLocation id, Supplier<T> factory) {
        EdgePointType<T> type = new EdgePointType<T>(id, factory);
        TYPES.put(id, type);
        return type;
    }

    public EdgePointType(ResourceLocation id, Supplier<T> factory) {
        this.id = id;
        this.factory = factory;
    }

    public T create() {
        TrackEdgePoint t = (TrackEdgePoint)this.factory.get();
        t.setType(this);
        return (T)t;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public static TrackEdgePoint read(FriendlyByteBuf buffer, DimensionPalette dimensions) {
        ResourceLocation type = buffer.readResourceLocation();
        EdgePointType<?> edgePointType = TYPES.get(type);
        Object point = edgePointType.create();
        ((TrackEdgePoint)point).read(buffer, dimensions);
        return point;
    }
}
