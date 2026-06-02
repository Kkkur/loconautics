/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.NotNull
 */
package dev.ryanhcode.sable.network.client;

import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Comparator;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class SubLevelSnapshotInterpolator {
    public final ObjectArrayList<Snapshot> buffer = new ObjectArrayList();
    private final Pose3d runningSnapshot = new Pose3d();
    private boolean stopped;

    public SubLevelSnapshotInterpolator(Pose3d pose) {
        this.runningSnapshot.set((Pose3dc)pose);
    }

    public void getSampleAt(double gameTick, Pose3d dest) {
        int beforeIndex = -1;
        Snapshot before = null;
        Snapshot after = null;
        for (int i = 0; i < this.buffer.size(); ++i) {
            Snapshot snapshot = (Snapshot)this.buffer.get(i);
            if ((double)snapshot.gameTick == gameTick) {
                dest.set(snapshot.pose);
                return;
            }
            if ((double)snapshot.gameTick < gameTick) {
                beforeIndex = i;
                before = snapshot;
                continue;
            }
            if (!((double)snapshot.gameTick > gameTick)) continue;
            after = snapshot;
            break;
        }
        if (before == null || after == null) {
            if (before != null) {
                dest.set(before.pose);
                int beforeBeforeIndex = beforeIndex - 1;
                if (beforeBeforeIndex >= 0 && !this.stopped) {
                    Snapshot beforeBefore = (Snapshot)this.buffer.get(beforeBeforeIndex);
                    double deadReckoningTicks = Mth.clamp((double)(gameTick - (double)before.gameTick), (double)0.0, (double)1.0);
                    double fraction = deadReckoningTicks / (double)(before.gameTick - beforeBefore.gameTick);
                    dest.set(beforeBefore.pose).lerp(before.pose, 1.0 + fraction);
                }
            } else if (after != null) {
                dest.set(after.pose);
            }
        } else {
            double factor = (gameTick - (double)before.gameTick) / (double)(after.gameTick - before.gameTick);
            before.pose.lerp(after.pose, factor, dest);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void receiveSnapshot(int gameTick, Pose3dc data) {
        ObjectArrayList<Snapshot> objectArrayList = this.buffer;
        synchronized (objectArrayList) {
            if (this.buffer.isEmpty() || ((Snapshot)this.buffer.getLast()).gameTick != gameTick) {
                this.buffer.add((Object)new Snapshot(gameTick, data));
            }
        }
        this.stopped = false;
    }

    public void setFirstPoses(Pose3dc poseA, Pose3dc poseB) {
        this.runningSnapshot.rotationPoint().set(poseA.rotationPoint());
        this.runningSnapshot.position().set(poseB.position());
    }

    public Pose3dc getInterpolatedPose() {
        return this.runningSnapshot;
    }

    public void receiveStop() {
        this.stopped = true;
    }

    public void splitFrom(SubLevelSnapshotInterpolator other, @NotNull Pose3dc pose) {
        for (Snapshot otherSnapshot : other.buffer) {
            if (otherSnapshot.gameTick >= ((Snapshot)this.buffer.getFirst()).gameTick) continue;
            Pose3dc containingPose = otherSnapshot.pose;
            Pose3d madeUpPastPose = new Pose3d(pose);
            madeUpPastPose.orientation().set(containingPose.orientation());
            containingPose.transformPosition(madeUpPastPose.position());
            this.buffer.add((Object)new Snapshot(otherSnapshot.gameTick, (Pose3dc)madeUpPastPose));
        }
        this.buffer.sort(Comparator.comparingDouble(a -> a.gameTick));
    }

    public void tick(double backTick) {
        int bufferStartTime = (int)(backTick - 6.0);
        while (!this.buffer.isEmpty() && ((Snapshot)this.buffer.getFirst()).gameTick < bufferStartTime) {
            this.buffer.removeFirst();
        }
        if (this.buffer.isEmpty()) {
            return;
        }
        this.getSampleAt(backTick, this.runningSnapshot);
    }

    public record Snapshot(int gameTick, Pose3dc pose) {
    }
}
