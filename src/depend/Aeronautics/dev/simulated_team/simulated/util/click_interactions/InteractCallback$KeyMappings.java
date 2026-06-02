/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.Options
 */
package dev.simulated_team.simulated.util.click_interactions;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;

public record InteractCallback.KeyMappings(KeyMapping use, KeyMapping attack, KeyMapping middle) {
    private static final InteractCallback.KeyMappings MAPPINGS = InteractCallback.KeyMappings.populateMappings();

    public static InteractCallback.KeyMappings getMappings() {
        return MAPPINGS;
    }

    private static InteractCallback.KeyMappings populateMappings() {
        Options options = Minecraft.getInstance().options;
        return new InteractCallback.KeyMappings(options.keyUse, options.keyAttack, options.keyPickItem);
    }
}
