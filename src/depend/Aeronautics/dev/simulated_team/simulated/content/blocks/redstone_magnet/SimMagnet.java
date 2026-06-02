/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaternionfc
 *  org.joml.Vector3d
 */
package dev.simulated_team.simulated.content.blocks.redstone_magnet;

import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;
import org.joml.Vector3d;

public interface SimMagnet {
    public Quaternionfc getOrientation();

    public SubLevel getLatestSubLevel();

    public Vec3 getMagnetPosition();

    public Vector3d setMagneticMoment(Vector3d var1);

    public boolean magnetActive();
}
