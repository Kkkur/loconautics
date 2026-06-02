/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package dev.ryanhcode.sable.physics.config.dimension_physics;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ryanhcode.sable.util.SableCodecUtil;
import java.util.ArrayList;
import java.util.List;

public class BezierResourceFunction {
    public static final Codec<BezierResourceFunction> CODEC = BezierPoint.CODEC.listOf().flatXmap(bezierPoints -> DataResult.success((Object)new BezierResourceFunction((List<BezierPoint>)bezierPoints)), bezierResourceFunction -> DataResult.success(bezierResourceFunction.getPoints()));
    private final List<BezierPoint> points;

    public BezierResourceFunction(List<BezierPoint> points) {
        this.points = points;
    }

    public BezierResourceFunction() {
        this.points = new ArrayList<BezierPoint>();
    }

    public List<BezierPoint> getPoints() {
        return this.points;
    }

    public void addPoint(BezierPoint point) {
        this.points.add(point);
    }

    public int pointSize() {
        return this.points.size();
    }

    public double evaluateFunction(double position) {
        if (this.points.isEmpty()) {
            return 1.0;
        }
        if (this.points.size() == 1) {
            return this.points.get((int)0).value;
        }
        int index = -1;
        for (BezierPoint point : this.points) {
            if (position < point.altitude()) break;
            ++index;
        }
        if (index == -1) {
            return this.points.get((int)0).value;
        }
        if (index >= this.points.size() - 1) {
            return this.points.get((int)(this.points.size() - 1)).value;
        }
        BezierPoint point1 = this.points.get(index);
        BezierPoint point2 = this.points.get(index + 1);
        double relativeX = point2.altitude - point1.altitude;
        double relativeY = point2.value - point1.value;
        double slope1 = point1.slope;
        double slope2 = point2.slope;
        double t = (position - point1.altitude) / relativeX;
        double cubicFactor = (slope1 + slope2) * relativeX - 2.0 * relativeY;
        double quadraticFactor = 3.0 * relativeY - (2.0 * slope1 + slope2) * relativeX;
        double linearFactor = relativeX * slope1;
        return Math.max(((cubicFactor * t + quadraticFactor) * t + linearFactor) * t + point1.value, 0.0);
    }

    public record BezierPoint(double altitude, double value, double slope) {
        public static final Codec<BezierPoint> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.DOUBLE.fieldOf("altitude").forGetter(BezierPoint::altitude), (App)SableCodecUtil.positiveDouble(true).fieldOf("value").forGetter(BezierPoint::value), (App)Codec.DOUBLE.fieldOf("slope").forGetter(BezierPoint::slope)).apply((Applicative)instance, BezierPoint::new));
    }
}
