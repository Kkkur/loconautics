/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.trains.track;

import com.simibubi.create.content.trains.track.BezierConnection;
import java.util.Iterator;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.world.phys.Vec3;

private static class BezierConnection.Bezierator
implements Iterator<BezierConnection.Segment> {
    private final BezierConnection.Segment segment;
    private final Vec3 end1;
    private final Vec3 end2;
    private final Vec3 finish1;
    private final Vec3 finish2;
    private final Vec3 faceNormal1;
    private final Vec3 faceNormal2;
    private final BezierConnection.Runtime runtime;

    private BezierConnection.Bezierator(BezierConnection bc, Vec3 offset) {
        this.runtime = bc.resolve();
        this.end1 = ((Vec3)bc.starts.getFirst()).add(offset);
        this.end2 = ((Vec3)bc.starts.getSecond()).add(offset);
        this.finish1 = ((Vec3)bc.axes.getFirst()).scale(this.runtime.handleLength).add(this.end1);
        this.finish2 = ((Vec3)bc.axes.getSecond()).scale(this.runtime.handleLength).add(this.end2);
        this.faceNormal1 = (Vec3)bc.normals.getFirst();
        this.faceNormal2 = (Vec3)bc.normals.getSecond();
        this.segment = new BezierConnection.Segment();
        this.segment.index = -1;
    }

    @Override
    public boolean hasNext() {
        return this.segment.index + 1 <= this.runtime.segments;
    }

    @Override
    public BezierConnection.Segment next() {
        ++this.segment.index;
        float t = this.runtime.getSegmentT(this.segment.index);
        this.segment.position = VecHelper.bezier((Vec3)this.end1, (Vec3)this.end2, (Vec3)this.finish1, (Vec3)this.finish2, (float)t);
        this.segment.derivative = VecHelper.bezierDerivative((Vec3)this.end1, (Vec3)this.end2, (Vec3)this.finish1, (Vec3)this.finish2, (float)t).normalize();
        this.segment.faceNormal = this.faceNormal1.equals((Object)this.faceNormal2) ? this.faceNormal1 : VecHelper.slerp((float)t, (Vec3)this.faceNormal1, (Vec3)this.faceNormal2);
        this.segment.normal = this.segment.faceNormal.cross(this.segment.derivative).normalize();
        return this.segment;
    }
}
