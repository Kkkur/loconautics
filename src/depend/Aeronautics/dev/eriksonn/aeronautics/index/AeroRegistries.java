/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.platform.registry.RegistrationProvider
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 */
package dev.eriksonn.aeronautics.index;

import dev.eriksonn.aeronautics.Aeronautics;
import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.CrystalPropagationContext;
import dev.eriksonn.aeronautics.content.blocks.hot_air.lifting_gas.LiftingGasType;
import foundry.veil.platform.registry.RegistrationProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class AeroRegistries {
    public static final RegistrationProvider<LiftingGasType> LIFTING_GAS_TYPE = AeroRegistries.registry(Keys.LIFTING_GAS_TYPE);
    public static final RegistrationProvider<CrystalPropagationContext> LEVITITE_CRYSTAL_PROPAGATION_CONTEXT = AeroRegistries.registry(Keys.LEVITITE_CRYSTAL_PROPAGATION_CONTEXT);

    private static <T> RegistrationProvider<T> registry(ResourceKey<Registry<T>> registryKey) {
        return RegistrationProvider.get(registryKey, (String)"aeronautics");
    }

    public static void init() {
    }

    public static class Keys {
        public static final ResourceKey<Registry<LiftingGasType>> LIFTING_GAS_TYPE = Keys.key("lifting_gas_type");
        public static final ResourceKey<Registry<CrystalPropagationContext>> LEVITITE_CRYSTAL_PROPAGATION_CONTEXT = Keys.key("levitite_crystal_propagation_context");

        private static <T> ResourceKey<Registry<T>> key(String name) {
            return ResourceKey.createRegistryKey((ResourceLocation)Aeronautics.path(name));
        }
    }
}
