/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.server.level.ServerLevel
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.sublevel.tracking_points;

import dev.ryanhcode.sable.api.sublevel.SubLevelObserver;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import dev.ryanhcode.sable.sublevel.storage.holding.GlobalSavedSubLevelPointer;
import dev.ryanhcode.sable.sublevel.tracking_points.SubLevelTrackingPointSavedData;
import dev.ryanhcode.sable.sublevel.tracking_points.TrackingPoint;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class SubLevelTrackingPointObserver
implements SubLevelObserver {
    private final ServerLevel serverLevel;

    public SubLevelTrackingPointObserver(ServerLevel serverLevel) {
        this.serverLevel = serverLevel;
    }

    @Override
    public void onSubLevelRemoved(SubLevel subLevel, SubLevelRemovalReason reason) {
        if (reason == SubLevelRemovalReason.REMOVED) {
            SubLevelTrackingPointSavedData data = this.getTrackingPointData();
            List<UUID> toProject = SubLevelTrackingPointObserver.getTrackingPoints((ServerSubLevel)subLevel, data);
            for (UUID uuid : toProject) {
                TrackingPoint trackingPoint = data.getTrackingPoint(uuid);
                if (trackingPoint == null) continue;
                Vector3d point = subLevel.logicalPose().transformPosition(trackingPoint.point());
                data.setTrackingPoint(uuid, new TrackingPoint(false, null, null, new Vector3d((Vector3dc)point), null));
            }
        }
    }

    @NotNull
    private static List<UUID> getTrackingPoints(ServerSubLevel subLevel, SubLevelTrackingPointSavedData data) {
        ObjectArrayList toProject = new ObjectArrayList();
        for (Map.Entry<UUID, TrackingPoint> entry : data.getAllTrackingPoints()) {
            TrackingPoint trackingPoint = entry.getValue();
            GlobalSavedSubLevelPointer pointer = trackingPoint.lastSavedSubLevelPointer();
            if (!trackingPoint.inSubLevel() || pointer == null || !pointer.equals(subLevel.getLastSerializationPointer())) continue;
            toProject.add(entry.getKey());
        }
        return toProject;
    }

    private SubLevelTrackingPointSavedData getTrackingPointData() {
        return SubLevelTrackingPointSavedData.getOrLoad(this.serverLevel);
    }
}
