/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  it.unimi.dsi.fastutil.Function
 */
package dev.ryanhcode.sable.mixinterface.clip_overwrite;

import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import it.unimi.dsi.fastutil.Function;

public interface LevelPoseProviderExtension {
    public void sable$pushPoseSupplier(Function<SubLevel, Pose3dc> var1);

    public void sable$popPoseSupplier();

    public Pose3dc sable$getPose(SubLevel var1);
}
