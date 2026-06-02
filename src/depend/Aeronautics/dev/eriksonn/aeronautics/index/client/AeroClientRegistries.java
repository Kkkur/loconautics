/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.platform.registry.RegistrationProvider
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 */
package dev.eriksonn.aeronautics.index.client;

import dev.eriksonn.aeronautics.Aeronautics;
import dev.eriksonn.aeronautics.api.CustomSituationalMusic;
import foundry.veil.platform.registry.RegistrationProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class AeroClientRegistries {
    public static RegistrationProvider<CustomSituationalMusic> CUSTOM_SITUATIONAL_MUSIC = AeroClientRegistries.registry(Keys.CUSTOM_SITUATIONAL_MUSIC);

    private static <T> RegistrationProvider<T> registry(ResourceKey<Registry<T>> registryKey) {
        return RegistrationProvider.get(registryKey, (String)"aeronautics");
    }

    public static void init() {
    }

    public static class Keys {
        public static final ResourceKey<Registry<CustomSituationalMusic>> CUSTOM_SITUATIONAL_MUSIC = Keys.key("custom_situational_music");

        private static <T> ResourceKey<Registry<T>> key(String name) {
            return ResourceKey.createRegistryKey((ResourceLocation)Aeronautics.path(name));
        }
    }
}
