/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Pair
 */
package com.simibubi.create.content.trains.entity;

import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import net.createmod.catnip.data.Pair;

public static interface TravellingPoint.ITrackSelector
extends BiFunction<TrackGraph, Pair<Boolean, List<Map.Entry<TrackNode, TrackEdge>>>, Map.Entry<TrackNode, TrackEdge>> {
}
