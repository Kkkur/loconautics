/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.trains.entity;

public static enum TravellingPoint.SteerDirection {
    NONE(0.0f),
    LEFT(-1.0f),
    RIGHT(1.0f);

    final float targetDot;

    private TravellingPoint.SteerDirection(float targetDot) {
        this.targetDot = targetDot;
    }
}
