/*
 * Decompiled with CFR 0.152.
 */
package dev.ryanhcode.sable.api.event;

import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;

@FunctionalInterface
public interface SablePostPhysicsTickEvent {
    public void postPhysicsTick(SubLevelPhysicsSystem var1, double var2);
}
