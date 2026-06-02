/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.math.Axis
 *  net.createmod.catnip.gui.ILightingSettings
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package com.simibubi.create.foundation.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.createmod.catnip.gui.ILightingSettings;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class CustomLightingSettings
implements ILightingSettings {
    private Vector3f light1;
    private Vector3f light2;

    protected CustomLightingSettings(float yRot, float xRot) {
        this.init(yRot, xRot, 0.0f, 0.0f, false);
    }

    protected CustomLightingSettings(float yRot1, float xRot1, float yRot2, float xRot2) {
        this.init(yRot1, xRot1, yRot2, xRot2, true);
    }

    protected void init(float yRot1, float xRot1, float yRot2, float xRot2, boolean doubleLight) {
        this.light1 = new Vector3f(0.0f, 0.0f, 1.0f);
        this.light1.rotate((Quaternionfc)Axis.YP.rotationDegrees(yRot1));
        this.light1.rotate((Quaternionfc)Axis.XN.rotationDegrees(xRot1));
        if (doubleLight) {
            this.light2 = new Vector3f(0.0f, 0.0f, 1.0f);
            this.light2.rotate((Quaternionfc)Axis.YP.rotationDegrees(yRot2));
            this.light2.rotate((Quaternionfc)Axis.XN.rotationDegrees(xRot2));
        } else {
            this.light2 = new Vector3f();
        }
    }

    public void applyLighting() {
        RenderSystem.setShaderLights((Vector3f)this.light1, (Vector3f)this.light2);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private float yRot1;
        private float xRot1;
        private float yRot2;
        private float xRot2;
        private boolean doubleLight;

        public Builder firstLightRotation(float yRot, float xRot) {
            this.yRot1 = yRot;
            this.xRot1 = xRot;
            return this;
        }

        public Builder secondLightRotation(float yRot, float xRot) {
            this.yRot2 = yRot;
            this.xRot2 = xRot;
            this.doubleLight = true;
            return this;
        }

        public Builder doubleLight() {
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
}
