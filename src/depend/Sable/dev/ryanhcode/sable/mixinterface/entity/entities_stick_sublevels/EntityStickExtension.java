/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.mixinterface.entity.entities_stick_sublevels;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface EntityStickExtension {
    public void sable$plotLerpTo(Vec3 var1, int var2);

    public void sable$setPlotPosition(@Nullable Vec3 var1);

    @Nullable
    public Vec3 sable$getPlotPosition();
}
