/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.infrastructure.gui;

public record OpenCreateMenuButton.SingleMenuRow(String leftTextKey, String rightTextKey) {
    public OpenCreateMenuButton.SingleMenuRow(String centerTextKey) {
        this(centerTextKey, centerTextKey);
    }
}
