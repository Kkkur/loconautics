/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 */
package dev.eriksonn.aeronautics.index;

import dev.eriksonn.aeronautics.Aeronautics;
import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.CrystalPropagationContext;
import dev.eriksonn.aeronautics.content.blocks.hot_air.lifting_gas.LiftingGasType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public static class AeroRegistries.Keys {
    public static final ResourceKey<Registry<LiftingGasType>> LIFTING_GAS_TYPE = AeroRegistries.Keys.key("lifting_gas_type");
    public static final ResourceKey<Registry<CrystalPropagationContext>> LEVITITE_CRYSTAL_PROPAGATION_CONTEXT = AeroRegistries.Keys.key("levitite_crystal_propagation_context");

    private static <T> ResourceKey<Registry<T>> key(String name) {
        return ResourceKey.createRegistryKey((ResourceLocation)Aeronautics.path(name));
    }
}
