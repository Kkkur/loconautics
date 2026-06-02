/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.kinetics.belt.transport;

private static enum BeltInventory.Ending {
    UNRESOLVED(0.0f),
    EJECT(0.0f),
    INSERT(0.25f),
    FUNNEL(0.5f),
    BLOCKED(0.45f);

    private float margin;

    private BeltInventory.Ending(float f) {
        this.margin = f;
    }
}
