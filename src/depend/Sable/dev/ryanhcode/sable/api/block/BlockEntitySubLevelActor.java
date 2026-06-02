/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.api.block;

import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BlockEntitySubLevelActor {
    default public void sable$tick(ServerSubLevel subLevel) {
    }

    default public void sable$physicsTick(ServerSubLevel subLevel, RigidBodyHandle handle, double timeStep) {
    }

    default public @Nullable Iterable<@NotNull SubLevel> sable$getLoadingDependencies() {
        return this.sable$getConnectionDependencies();
    }

    default public @Nullable Iterable<@NotNull SubLevel> sable$getConnectionDependencies() {
        return null;
    }
}
