/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.MutableInt
 *  org.joml.Vector3d
 */
package dev.simulated_team.simulated.content.entities.diagram.screen;

import org.apache.commons.lang3.mutable.MutableInt;
import org.joml.Vector3d;

public record ForceClusterFinder.Cluster(Vector3d pos, Vector3d force, MutableInt groupSize) {
}
