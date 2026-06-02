/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.core.Position
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.ponder.elements.rope;

import dev.ryanhcode.sable.companion.math.JOMLConversion;
import net.minecraft.core.Position;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class PonderRopePose {
    public final Vector3d start = new Vector3d();
    public final Vector3d end = new Vector3d();
    public double length;
    public double sog;
    public double floorHeight;

    public PonderRopePose() {
    }

    public PonderRopePose(Vector3d start, Vector3d end, double length, double sog, double floorHeight) {
        this.start.set((Vector3dc)start);
        this.end.set((Vector3dc)end);
        this.length = length;
        this.sog = sog;
        this.floorHeight = floorHeight;
    }

    public void set(PonderRopePose pose) {
        this.start.set((Vector3dc)pose.start);
        this.end.set((Vector3dc)pose.end);
        this.length = pose.length;
        this.sog = pose.sog;
        this.floorHeight = pose.floorHeight;
    }

    public void lerp(PonderRopePose other, double t) {
        this.start.lerp((Vector3dc)other.start, t);
        this.end.lerp((Vector3dc)other.end, t);
        this.length = Mth.lerp((double)t, (double)this.length, (double)other.length);
        this.sog = Mth.lerp((double)t, (double)this.sog, (double)other.sog);
    }

    public void lerp(PonderRopePose a, PonderRopePose b, PonderRopePose dest, double t) {
        a.start.lerp((Vector3dc)b.start, t, dest.start);
        a.end.lerp((Vector3dc)b.end, t, dest.end);
        dest.length = Mth.lerp((double)t, (double)a.length, (double)b.length);
        dest.sog = Mth.lerp((double)t, (double)a.sog, (double)b.sog);
    }

    public void lerp(Vec3 from, Vec3 to, double length, double sog, double t) {
        this.start.lerp((Vector3dc)JOMLConversion.toJOML((Position)from), t);
        this.end.lerp((Vector3dc)JOMLConversion.toJOML((Position)to), t);
        this.length = Mth.lerp((double)t, (double)this.length, (double)length);
        this.sog = Mth.lerp((double)t, (double)this.sog, (double)sog);
    }
}
