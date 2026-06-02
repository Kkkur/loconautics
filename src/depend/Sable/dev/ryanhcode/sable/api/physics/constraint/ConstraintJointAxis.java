/*
 * Decompiled with CFR 0.152.
 */
package dev.ryanhcode.sable.api.physics.constraint;

public enum ConstraintJointAxis {
    LINEAR_X,
    LINEAR_Y,
    LINEAR_Z,
    ANGULAR_X,
    ANGULAR_Y,
    ANGULAR_Z;

    public static final ConstraintJointAxis[] ALL;
    public static final ConstraintJointAxis[] LINEAR;
    public static final ConstraintJointAxis[] ANGULAR;

    static {
        ALL = ConstraintJointAxis.values();
        LINEAR = new ConstraintJointAxis[]{LINEAR_X, LINEAR_Y, LINEAR_Z};
        ANGULAR = new ConstraintJointAxis[]{ANGULAR_X, ANGULAR_Y, ANGULAR_Z};
    }
}
