/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Pair
 */
package com.simibubi.create.content.trains.entity;

import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.station.GlobalStation;
import java.util.Map;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Pair;

@FunctionalInterface
public static interface Navigation.StationTest {
    public boolean test(double var1, double var3, Map<TrackEdge, Pair<Boolean, Couple<TrackNode>>> var5, Pair<Couple<TrackNode>, TrackEdge> var6, GlobalStation var7);
}
