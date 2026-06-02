/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.trains.entity;

import com.simibubi.create.content.trains.graph.TrackEdge;
import java.util.function.BiConsumer;

public static interface TravellingPoint.ITurnListener
extends BiConsumer<Double, TrackEdge> {
}
