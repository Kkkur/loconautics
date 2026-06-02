/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 */
package dev.ryanhcode.sable.mixinterface.particle;

import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.world.phys.Vec3;

public interface ParticleExtension {
    public void sable$initialKickOut();

    public void sable$moveWithInheritedVelocity();

    public void sable$setTrackingSubLevel(ClientSubLevel var1, Vec3 var2);

    public SubLevel sable$getTrackingSubLevel();
}
