/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 */
package dev.eriksonn.aeronautics.index.client;

import dev.eriksonn.aeronautics.Aeronautics;
import dev.eriksonn.aeronautics.api.CustomSituationalMusic;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public static class AeroClientRegistries.Keys {
    public static final ResourceKey<Registry<CustomSituationalMusic>> CUSTOM_SITUATIONAL_MUSIC = AeroClientRegistries.Keys.key("custom_situational_music");

    private static <T> ResourceKey<Registry<T>> key(String name) {
        return ResourceKey.createRegistryKey((ResourceLocation)Aeronautics.path(name));
    }
}
