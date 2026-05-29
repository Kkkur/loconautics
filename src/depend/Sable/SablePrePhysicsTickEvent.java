/*
 * Decompiled with CFR 0.152.
 */
package dev.ryanhcode.sable.api.event;

import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;

@FunctionalInterface
public interface SablePrePhysicsTickEvent {
    public void prePhysicsTick(SubLevelPhysicsSystem var1, double var2);
}
