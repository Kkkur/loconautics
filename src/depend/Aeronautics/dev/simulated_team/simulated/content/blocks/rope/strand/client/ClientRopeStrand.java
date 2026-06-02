/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.blocks.rope.strand.client;

import dev.simulated_team.simulated.content.blocks.rope.strand.client.ClientRopePoint;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.UUID;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class ClientRopeStrand {
    private final ObjectArrayList<ClientRopePoint> points = new ObjectArrayList();
    public Vec3 startAttachment = null;
    public Vec3 endAttachment = null;
    private boolean stopped;
    private final UUID uuid;

    public ClientRopeStrand(UUID uuid) {
        this.uuid = uuid;
    }

    public ObjectArrayList<ClientRopePoint> getPoints() {
        return this.points;
    }

    protected void tickInterpolation(double gameTick) {
        for (ClientRopePoint point : this.points) {
            ObjectList<ClientRopePoint.Snapshot> buffer = point.snapshots();
            point.previousPosition().set((Vector3dc)point.position());
            while (!buffer.isEmpty() && ((ClientRopePoint.Snapshot)buffer.getFirst()).interpolationTick() < gameTick - 6.0) {
                buffer.removeFirst();
            }
            if (buffer.isEmpty()) continue;
            int beforeIndex = -1;
            ClientRopePoint.Snapshot before = null;
            ClientRopePoint.Snapshot after = null;
            for (int i = 0; i < buffer.size(); ++i) {
                ClientRopePoint.Snapshot snapshot = (ClientRopePoint.Snapshot)buffer.get(i);
                if (gameTick == snapshot.interpolationTick()) {
                    point.position().set((Vector3dc)snapshot.position());
                    continue;
                }
                if (snapshot.interpolationTick() < gameTick) {
                    beforeIndex = i;
                    before = snapshot;
                    continue;
                }
                if (!(snapshot.interpolationTick() > gameTick)) continue;
                after = snapshot;
                break;
            }
            if (before == null || after == null) {
                if (before != null) {
                    point.position().set((Vector3dc)before.position());
                    int beforeBeforeIndex = beforeIndex - 1;
                    if (beforeBeforeIndex < 0 || this.stopped) continue;
                    ClientRopePoint.Snapshot beforeBefore = (ClientRopePoint.Snapshot)buffer.get(beforeBeforeIndex);
                    double deadReckoningTicks = Mth.clamp((double)(gameTick - before.interpolationTick()), (double)0.0, (double)1.0);
                    double fraction = deadReckoningTicks / (before.interpolationTick() - beforeBefore.interpolationTick());
                    point.position().set((Vector3dc)beforeBefore.position()).lerp((Vector3dc)before.position(), 1.0 + fraction);
                    continue;
                }
                if (after == null) continue;
                point.position().set((Vector3dc)after.position());
                continue;
            }
            double factor = (gameTick - before.interpolationTick()) / (after.interpolationTick() - before.interpolationTick());
            before.position().lerp((Vector3dc)after.position(), factor, point.position());
        }
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public AABB getBounds() {
        if (this.points.isEmpty()) {
            return null;
        }
        Vector3d point0 = ((ClientRopePoint)this.points.getFirst()).position();
        AABB bounds = new AABB(point0.x, point0.y, point0.z, point0.x, point0.y, point0.z);
        for (ClientRopePoint point : this.points) {
            Vector3d pos = point.position();
            bounds = bounds.minmax(new AABB(pos.x, pos.y, pos.z, pos.x, pos.y, pos.z));
        }
        return bounds;
    }

    public UUID getUuid() {
        return this.uuid;
    }
}
