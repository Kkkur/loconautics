/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.FloatUnaryOperator
 */
package dev.simulated_team.simulated.ponder;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;

public class SmoothMovementUtils {
    public static FloatUnaryOperator cubicSmoothing() {
        return t -> t * t * (3.0f - 2.0f * t);
    }

    public static FloatUnaryOperator linear() {
        return t -> t;
    }

    public static FloatUnaryOperator quinticSmoothing() {
        return t -> t * t * t * (10.0f - 3.0f * t * (5.0f - 2.0f * t));
    }

    public static FloatUnaryOperator quadraticJump() {
        return t -> 4.0f * t * (1.0f - t);
    }

    public static FloatUnaryOperator quadraticRise() {
        return t -> t * t;
    }

    public static FloatUnaryOperator quadraticRiseDual() {
        return t -> t * (2.0f - t);
    }

    public static FloatUnaryOperator quadraticRiseInOut() {
        return t -> (double)t < 0.5 ? 2.0f * t * t : 2.0f * t * (2.0f - t) - 1.0f;
    }

    public static FloatUnaryOperator quadraticRiseOut() {
        return t -> t * (2.0f - t);
    }

    public static FloatUnaryOperator elasticOut() {
        double c4 = 2.0943951023931953;
        return t -> (float)(Math.pow(2.0, -10.0f * t) * Math.sin(((double)(t * 10.0f) - 0.75) * 2.0943951023931953) + 1.0);
    }

    public static FloatUnaryOperator softElasticOut() {
        double c4 = 2.0943951023931953;
        return t -> (double)t < 0.5 ? 2.0f * t : (float)(Math.pow(2.0, -10.0f * t) * Math.sin(((double)t * Math.pow(t + 1.0f, 5.0) - 0.75) * 2.0943951023931953) + 1.0);
    }

    public static FloatUnaryOperator cubicRise() {
        return t -> t * t * t;
    }

    public static FloatUnaryOperator asymptoticAcceleration(float smoothing) {
        return t -> (float)(((double)(t * smoothing) + Math.exp(-smoothing * t) - 1.0) / ((double)smoothing + Math.exp(-smoothing) - 1.0));
    }
}
