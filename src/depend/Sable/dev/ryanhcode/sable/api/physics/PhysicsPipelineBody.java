/*
 * Decompiled with CFR 0.152.
 */
package dev.ryanhcode.sable.api.physics;

import dev.ryanhcode.sable.api.physics.mass.MassData;

public interface PhysicsPipelineBody {
    public static final int NULL_RUNTIME_ID = -1;

    public int getRuntimeId();

    public MassData getMassTracker();

    public boolean isRemoved();
}
