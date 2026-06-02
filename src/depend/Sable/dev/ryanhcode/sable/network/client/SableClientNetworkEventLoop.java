/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Queues
 *  com.mojang.logging.LogUtils
 */
package dev.ryanhcode.sable.network.client;

import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import dev.ryanhcode.sable.Sable;
import java.util.Queue;

public class SableClientNetworkEventLoop {
    private final Queue<Runnable> pendingRunnables = Queues.newConcurrentLinkedQueue();

    public void tell(Runnable runnable) {
        this.pendingRunnables.add(runnable);
    }

    public void runAllTasks() {
        while (this.pollTask()) {
        }
    }

    public boolean pollTask() {
        Runnable runnable = this.pendingRunnables.peek();
        if (runnable == null) {
            return false;
        }
        this.doRunTask(this.pendingRunnables.remove());
        return true;
    }

    protected void doRunTask(Runnable runnable) {
        try {
            runnable.run();
        }
        catch (Exception var3) {
            Sable.LOGGER.error(LogUtils.FATAL_MARKER, "Error executing packet handle task", (Throwable)var3);
        }
    }

    public void clear() {
        this.pendingRunnables.clear();
    }
}
