/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.kinetics.press;

public static enum PressingBehaviour.Mode {
    WORLD(1.0f),
    BELT(1.1875f),
    BASIN(1.375f);

    public float headOffset;

    private PressingBehaviour.Mode(float headOffset) {
        this.headOffset = headOffset;
    }
}
