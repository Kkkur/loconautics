/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.trains.track;

import com.simibubi.create.content.trains.graph.TrackNodeLocation;

static class TrackPropagator.FrontierEntry {
    TrackNodeLocation.DiscoveredLocation prevNode;
    TrackNodeLocation.DiscoveredLocation currentNode;
    TrackNodeLocation.DiscoveredLocation parentNode;

    public TrackPropagator.FrontierEntry(TrackNodeLocation.DiscoveredLocation parent, TrackNodeLocation.DiscoveredLocation previousNode, TrackNodeLocation.DiscoveredLocation location) {
        this.parentNode = parent;
        this.prevNode = previousNode;
        this.currentNode = location;
    }
}
