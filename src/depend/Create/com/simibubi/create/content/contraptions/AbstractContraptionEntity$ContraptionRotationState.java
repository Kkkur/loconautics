/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.AngleHelper
 */
package com.simibubi.create.content.contraptions;

import com.simibubi.create.foundation.collision.Matrix3d;
import net.createmod.catnip.math.AngleHelper;

public static class AbstractContraptionEntity.ContraptionRotationState {
    public static final AbstractContraptionEntity.ContraptionRotationState NONE = new AbstractContraptionEntity.ContraptionRotationState();
    public float xRotation = 0.0f;
    public float yRotation = 0.0f;
    public float zRotation = 0.0f;
    public float secondYRotation = 0.0f;
    Matrix3d matrix;

    public Matrix3d asMatrix() {
        if (this.matrix != null) {
            return this.matrix;
        }
        this.matrix = new Matrix3d().asIdentity();
        if (this.xRotation != 0.0f) {
            this.matrix.multiply(new Matrix3d().asXRotation(AngleHelper.rad((double)(-this.xRotation))));
        }
        if (this.yRotation != 0.0f) {
            this.matrix.multiply(new Matrix3d().asYRotation(AngleHelper.rad((double)(-this.yRotation))));
        }
        if (this.zRotation != 0.0f) {
            this.matrix.multiply(new Matrix3d().asZRotation(AngleHelper.rad((double)(-this.zRotation))));
        }
        return this.matrix;
    }

    public boolean hasVerticalRotation() {
        return this.xRotation != 0.0f || this.zRotation != 0.0f;
    }

    public float getYawOffset() {
        return this.secondYRotation;
    }
}
