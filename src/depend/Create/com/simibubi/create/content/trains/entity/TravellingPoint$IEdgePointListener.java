/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Pair
 */
package com.simibubi.create.content.trains.entity;

import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import java.util.function.BiPredicate;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Pair;

public static interface TravellingPoint.IEdgePointListener
extends BiPredicate<Double, Pair<TrackEdgePoint, Couple<TrackNode>>> {
}
