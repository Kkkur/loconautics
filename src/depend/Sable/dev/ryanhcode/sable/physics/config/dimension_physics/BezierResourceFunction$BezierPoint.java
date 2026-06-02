/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package dev.ryanhcode.sable.physics.config.dimension_physics;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ryanhcode.sable.util.SableCodecUtil;

public record BezierResourceFunction.BezierPoint(double altitude, double value, double slope) {
    public static final Codec<BezierResourceFunction.BezierPoint> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.DOUBLE.fieldOf("altitude").forGetter(BezierResourceFunction.BezierPoint::altitude), (App)SableCodecUtil.positiveDouble(true).fieldOf("value").forGetter(BezierResourceFunction.BezierPoint::value), (App)Codec.DOUBLE.fieldOf("slope").forGetter(BezierResourceFunction.BezierPoint::slope)).apply((Applicative)instance, BezierResourceFunction.BezierPoint::new));
}
