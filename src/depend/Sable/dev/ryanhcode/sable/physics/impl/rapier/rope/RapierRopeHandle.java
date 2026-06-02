/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.physics.impl.rapier.rope;

import dev.ryanhcode.sable.api.physics.object.rope.RopeHandle;
import dev.ryanhcode.sable.physics.impl.rapier.Rapier3D;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3d;
import org.joml.Vector3dc;

@ApiStatus.Internal
public record RapierRopeHandle(int sceneId, long handle) implements RopeHandle
{
    public static RapierRopeHandle create(int sceneId, double pointRadius, List<Vector3d> points) {
        double[] coordinates = new double[points.size() * 3];
        for (int i = 0; i < points.size(); ++i) {
            Vector3d point = points.get(i);
            coordinates[i * 3] = point.x;
            coordinates[i * 3 + 1] = point.y;
            coordinates[i * 3 + 2] = point.z;
        }
        long handle = Rapier3D.createRope(sceneId, pointRadius, points.get(0).distance((Vector3dc)points.get(1)), coordinates, points.size());
        return new RapierRopeHandle(sceneId, handle);
    }

    @Override
    public void readPose(List<Vector3d> dest) {
        double[] coordinates = Rapier3D.queryRope(this.sceneId, this.handle);
        for (int i = 0; i < coordinates.length; i += 3) {
            dest.get(i / 3).set(coordinates[i], coordinates[i + 1], coordinates[i + 2]);
        }
    }

    @Override
    public void remove() {
        Rapier3D.removeRope(this.sceneId, this.handle);
    }

    @Override
    public void setFirstSegmentLength(double length) {
        Rapier3D.setRopeFirstSegmentLength(this.sceneId, this.handle, length);
    }

    @Override
    public void removeFirstPoint() {
        Rapier3D.removeRopePointAtStart(this.sceneId, this.handle);
    }

    @Override
    public void addPoint(Vector3dc position) {
        Rapier3D.addRopePointAtStart(this.sceneId, this.handle, position.x(), position.y(), position.z());
    }

    @Override
    public void setAttachment(RopeHandle.AttachmentPoint attachmentPoint, Vector3dc location, ServerSubLevel subLevel) {
        Rapier3D.setRopeAttachment(this.sceneId, this.handle, subLevel == null ? -1 : Rapier3D.getID(subLevel), location.x(), location.y(), location.z(), attachmentPoint == RopeHandle.AttachmentPoint.END);
    }

    @Override
    public void wakeUp() {
        Rapier3D.wakeUpRope(this.sceneId, this.handle);
    }
}
