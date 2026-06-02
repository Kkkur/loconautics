/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.KeyMapping
 */
package dev.simulated_team.simulated.util.click_interactions;

import net.minecraft.client.KeyMapping;

public record InteractCallback.Input(boolean mouse, int key, int scanCode) {
    public static InteractCallback.Input mouse(int key) {
        return new InteractCallback.Input(true, key, -1);
    }

    public static InteractCallback.Input key(int key, int scanCode) {
        return new InteractCallback.Input(false, key, scanCode);
    }

    public boolean matches(KeyMapping mapping) {
        if (this.mouse) {
            return mapping.matchesMouse(this.key);
        }
        return mapping.matches(this.key, this.scanCode);
    }
}
