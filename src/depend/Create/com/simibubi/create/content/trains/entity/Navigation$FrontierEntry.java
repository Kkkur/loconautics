/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.trains.entity;

import com.simibubi.create.content.trains.entity.Navigation;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackNode;

private class Navigation.FrontierEntry
implements Comparable<Navigation.FrontierEntry> {
    double distance;
    int penalty;
    double remaining;
    boolean hasDestination;
    TrackNode node1;
    TrackNode node2;
    TrackEdge edge;

    public Navigation.FrontierEntry(Navigation navigation, double distance, int penalty, TrackNode node1, TrackNode node2, TrackEdge edge) {
        this.distance = distance;
        this.penalty = penalty;
        this.remaining = 0.0;
        this.hasDestination = false;
        this.node1 = node1;
        this.node2 = node2;
        this.edge = edge;
    }

    public Navigation.FrontierEntry(Navigation navigation, double distance, int penalty, double remaining, boolean hasDestination, TrackNode node1, TrackNode node2, TrackEdge edge) {
        this.distance = distance;
        this.penalty = penalty;
        this.remaining = remaining;
        this.hasDestination = hasDestination;
        this.node1 = node1;
        this.node2 = node2;
        this.edge = edge;
    }

    @Override
    public int compareTo(Navigation.FrontierEntry o) {
        return Double.compare(this.distance + (double)this.penalty + this.remaining, o.distance + (double)o.penalty + o.remaining);
    }
}
