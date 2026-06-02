/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 */
package com.simibubi.create.content.contraptions.actors.roller;

import com.simibubi.create.content.contraptions.actors.roller.RollerMovementBehaviour;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import java.util.function.BiConsumer;
import net.createmod.catnip.data.Couple;

private final class RollerMovementBehaviour.RollerTravellingPoint
extends TravellingPoint {
    public BiConsumer<TrackEdge, Couple<Double>> traversalCallback;

    private RollerMovementBehaviour.RollerTravellingPoint(RollerMovementBehaviour rollerMovementBehaviour) {
    }

    @Override
    protected Double edgeTraversedFrom(TrackGraph graph, boolean forward, TravellingPoint.IEdgePointListener edgePointListener, TravellingPoint.ITurnListener turnListener, double prevPos, double totalDistance) {
        double from = forward ? prevPos : this.position;
        double to = forward ? this.position : prevPos;
        this.traversalCallback.accept(this.edge, (Couple<Double>)Couple.create((Object)from, (Object)to));
        return super.edgeTraversedFrom(graph, forward, edgePointListener, turnListener, prevPos, totalDistance);
    }
}
