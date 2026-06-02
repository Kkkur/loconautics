/*
 * Decompiled with CFR 0.152.
 */
package dev.simulated_team.simulated.api;

public static enum BearingSlowdownController.ContraptionSymmetry {
    NONE(360.0f),
    HALF(180.0f),
    QUARTER(90.0f);

    final float angle;

    private BearingSlowdownController.ContraptionSymmetry(float angle) {
        this.angle = angle;
    }

    public float getAngle() {
        return this.angle;
    }
}
