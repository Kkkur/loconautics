/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.api.physics.force.QueuedForceGroup$PointForce
 *  org.apache.commons.lang3.mutable.MutableInt
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.entities.diagram.screen;

import dev.ryanhcode.sable.api.physics.force.QueuedForceGroup;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.mutable.MutableInt;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class ForceClusterFinder {
    private static final double CLUSTER_SEPARATION_THRESHOLD = 0.4;

    public static List<Cluster> passThrough(List<QueuedForceGroup.PointForce> forces) {
        ArrayList<Cluster> clusters = new ArrayList<Cluster>(forces.size());
        for (QueuedForceGroup.PointForce force : forces) {
            clusters.add(new Cluster(new Vector3d(force.point()), new Vector3d(force.force()), new MutableInt(1)));
        }
        return clusters;
    }

    public static List<Cluster> getMergedClusters(List<QueuedForceGroup.PointForce> forces) {
        ArrayList<Cluster> clusters = new ArrayList<Cluster>();
        if (forces.isEmpty()) {
            return clusters;
        }
        ArrayList<ClusteredForce> clusteredForces = new ArrayList<ClusteredForce>();
        for (QueuedForceGroup.PointForce force : forces) {
            clusteredForces.add(new ClusteredForce(force.point(), force.force(), new MutableInt()));
        }
        while (ForceClusterFinder.tryAddCluster(clusters, clusteredForces)) {
            while (!ForceClusterFinder.groupArrows(clusters, clusteredForces)) {
                ForceClusterFinder.organizeClusters(clusters, clusteredForces);
            }
        }
        ForceClusterFinder.organizeClusters(clusters, clusteredForces);
        ForceClusterFinder.finalizeClusters(clusters, clusteredForces);
        return clusters;
    }

    static boolean tryAddCluster(List<Cluster> clusters, List<ClusteredForce> forces) {
        if (clusters.isEmpty()) {
            Cluster c = new Cluster(new Vector3d(), new Vector3d(), new MutableInt());
            for (ClusteredForce force : forces) {
                c.force.add(Math.abs(force.force.x()), Math.abs(force.force.y()), Math.abs(force.force.z()));
            }
            c.force.normalize();
            clusters.add(c);
            return true;
        }
        double maxDistance = -1.0;
        ClusteredForce index = null;
        for (ClusteredForce force : forces) {
            double d = ForceClusterFinder.getVariance((Vector3dc)clusters.get((int)force.getIndex()).force, force.force);
            if (!(d > maxDistance)) continue;
            maxDistance = d;
            index = force;
        }
        if (index != null && maxDistance > 0.16000000000000003) {
            Cluster c = new Cluster(new Vector3d(), new Vector3d(index.force), new MutableInt());
            clusters.add(c);
            return true;
        }
        return false;
    }

    static boolean groupArrows(List<Cluster> clusters, List<ClusteredForce> forces) {
        boolean done = true;
        for (ClusteredForce force : forces) {
            int previousIndex = force.getIndex();
            double minDist = 100.0;
            for (int i = 0; i < clusters.size(); ++i) {
                double dist = ForceClusterFinder.getVariance(force.force, (Vector3dc)clusters.get((int)i).force);
                if (!(dist < minDist)) continue;
                minDist = dist;
                force.clusterIndex.setValue(i);
            }
            if (previousIndex == force.getIndex()) continue;
            done = false;
        }
        return done;
    }

    static void organizeClusters(List<Cluster> clusters, List<ClusteredForce> forces) {
        for (Cluster c : clusters) {
            c.force.zero();
            c.groupSize.setValue(0);
        }
        for (ClusteredForce force : forces) {
            Cluster c = clusters.get(force.getIndex());
            c.force.add(force.force);
            c.groupSize.increment();
        }
        for (int k = clusters.size() - 1; k >= 0; --k) {
            Cluster c;
            c = clusters.get(k);
            if (c.groupSize.getValue() != 0) continue;
            clusters.remove(c);
            for (ClusteredForce force : forces) {
                if (force.clusterIndex.getValue() <= k) continue;
                force.clusterIndex.decrement();
            }
        }
    }

    static void finalizeClusters(List<Cluster> clusters, List<ClusteredForce> forces) {
        for (ClusteredForce force : forces) {
            Cluster c = clusters.get(force.getIndex());
            c.pos.fma(c.force.dot(force.force) / c.force.lengthSquared(), force.pos);
        }
    }

    static double getVariance(Vector3dc x, Vector3dc y) {
        double x2 = x.dot(x);
        double xy = x.dot(y);
        double y2 = y.dot(y);
        return 2.0 * (1.0 - xy / Math.sqrt(x2 * y2));
    }

    public record Cluster(Vector3d pos, Vector3d force, MutableInt groupSize) {
    }

    record ClusteredForce(Vector3dc pos, Vector3dc force, MutableInt clusterIndex) {
        int getIndex() {
            return this.clusterIndex.getValue();
        }
    }
}
