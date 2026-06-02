/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.kinetics.mechanicalArm;

public static enum ArmInteractionPoint.Mode {
    DEPOSIT("mechanical_arm.deposit_to", 14532966),
    TAKE("mechanical_arm.extract_from", 8375776);

    private final String translationKey;
    private final int color;

    private ArmInteractionPoint.Mode(String translationKey, int color) {
        this.translationKey = translationKey;
        this.color = color;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public int getColor() {
        return this.color;
    }
}
