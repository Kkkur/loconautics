/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.trains.signal;

public static enum SignalBlockEntity.SignalState {
    RED,
    YELLOW,
    GREEN,
    INVALID;


    public boolean isRedLight(float renderTime) {
        return this == RED || this == INVALID && renderTime % 40.0f < 3.0f;
    }

    public boolean isYellowLight(float renderTime) {
        return this == YELLOW;
    }

    public boolean isGreenLight(float renderTime) {
        return this == GREEN;
    }
}
