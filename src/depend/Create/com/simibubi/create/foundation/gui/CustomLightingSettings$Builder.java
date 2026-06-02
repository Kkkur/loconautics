/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.foundation.gui;

import com.simibubi.create.foundation.gui.CustomLightingSettings;

public static class CustomLightingSettings.Builder {
    private float yRot1;
    private float xRot1;
    private float yRot2;
    private float xRot2;
    private boolean doubleLight;

    public CustomLightingSettings.Builder firstLightRotation(float yRot, float xRot) {
        this.yRot1 = yRot;
        this.xRot1 = xRot;
        return this;
    }

    public CustomLightingSettings.Builder secondLightRotation(float yRot, float xRot) {
        this.yRot2 = yRot;
        this.xRot2 = xRot;
        this.doubleLight = true;
        return this;
    }

    public CustomLightingSettings.Builder doubleLight() {
        this.doubleLight = true;
        return this;
    }

    public CustomLightingSettings build() {
        if (this.doubleLight) {
            return new CustomLightingSettings(this.yRot1, this.xRot1, this.yRot2, this.xRot2);
        }
        return new CustomLightingSettings(this.yRot1, this.xRot1);
    }
}
