/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 */
package dev.simulated_team.simulated.index;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.client.BlockPropertiesTooltip;
import dev.simulated_team.simulated.content.blocks.nav_table.navigation_target.NavigationTarget;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public static class SimRegistries.Keys {
    public static final ResourceKey<Registry<NavigationTarget>> NAVIGATION_TARGET = SimRegistries.Keys.key("navigation_target");
    public static final ResourceKey<Registry<BlockPropertiesTooltip.Entry>> PROPERTY_TOOLTIP = SimRegistries.Keys.key("property_tooltip");

    private static <T> ResourceKey<Registry<T>> key(String name) {
        return ResourceKey.createRegistryKey((ResourceLocation)Simulated.path(name));
    }
}
