/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.platform.registry.RegistrationProvider
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 */
package dev.simulated_team.simulated.index;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.client.BlockPropertiesTooltip;
import dev.simulated_team.simulated.content.blocks.nav_table.navigation_target.NavigationTarget;
import foundry.veil.platform.registry.RegistrationProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class SimRegistries {
    public static final Registry<NavigationTarget> NAVIGATION_TARGET = SimRegistries.registry(Keys.NAVIGATION_TARGET);
    public static final Registry<BlockPropertiesTooltip.Entry> PROPERTY_TOOLTIP = SimRegistries.registry(Keys.PROPERTY_TOOLTIP);

    private static <T> Registry<T> registry(ResourceKey<Registry<T>> registryKey) {
        RegistrationProvider provider = RegistrationProvider.get(registryKey, (String)"simulated");
        return provider.asVanillaRegistry();
    }

    public static void register() {
    }

    public static class Keys {
        public static final ResourceKey<Registry<NavigationTarget>> NAVIGATION_TARGET = Keys.key("navigation_target");
        public static final ResourceKey<Registry<BlockPropertiesTooltip.Entry>> PROPERTY_TOOLTIP = Keys.key("property_tooltip");

        private static <T> ResourceKey<Registry<T>> key(String name) {
            return ResourceKey.createRegistryKey((ResourceLocation)Simulated.path(name));
        }
    }
}
