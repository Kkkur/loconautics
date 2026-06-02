/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.sublevel.render.fancy.task;

import dev.ryanhcode.sable.sublevel.render.fancy.task.SubLevelTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

private record FancySubLevelTaskScheduler.Task(SubLevelTask task, double distance, @Nullable Runnable onComplete) implements Comparable<FancySubLevelTaskScheduler.Task>
{
    @Override
    public int compareTo(@NotNull FancySubLevelTaskScheduler.Task o) {
        return Double.compare(this.distance, o.distance);
    }
}
