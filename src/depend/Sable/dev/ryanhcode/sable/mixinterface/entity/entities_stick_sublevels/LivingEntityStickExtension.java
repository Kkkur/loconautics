/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 */
package dev.ryanhcode.sable.mixinterface.entity.entities_stick_sublevels;

import dev.ryanhcode.sable.mixinterface.entity.entities_stick_sublevels.EntityStickExtension;
import net.minecraft.world.phys.Vec3;

public interface LivingEntityStickExtension
extends EntityStickExtension {
    public void sable$setupLerp();

    public void sable$applyLerp();

    public Vec3 sable$getLerpTarget();
}
