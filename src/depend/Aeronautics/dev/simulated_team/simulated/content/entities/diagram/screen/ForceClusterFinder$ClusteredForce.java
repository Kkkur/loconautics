/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.MutableInt
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.entities.diagram.screen;

import org.apache.commons.lang3.mutable.MutableInt;
import org.joml.Vector3dc;

record ForceClusterFinder.ClusteredForce(Vector3dc pos, Vector3dc force, MutableInt clusterIndex) {
    int getIndex() {
        return this.clusterIndex.getValue();
    }
}
