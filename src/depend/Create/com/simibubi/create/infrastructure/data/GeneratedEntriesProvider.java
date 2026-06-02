/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.RegistrySetBuilder
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.data.PackOutput
 *  net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider
 *  net.neoforged.neoforge.registries.NeoForgeRegistries$Keys
 */
package com.simibubi.create.infrastructure.data;

import com.simibubi.create.AllDamageTypes;
import com.simibubi.create.AllEnchantments;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.equipment.potatoCannon.AllPotatoProjectileTypes;
import com.simibubi.create.infrastructure.worldgen.AllBiomeModifiers;
import com.simibubi.create.infrastructure.worldgen.AllConfiguredFeatures;
import com.simibubi.create.infrastructure.worldgen.AllPlacedFeatures;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class GeneratedEntriesProvider
extends DatapackBuiltinEntriesProvider {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder().add(Registries.ENCHANTMENT, AllEnchantments::bootstrap).add(Registries.DAMAGE_TYPE, AllDamageTypes::bootstrap).add(Registries.CONFIGURED_FEATURE, AllConfiguredFeatures::bootstrap).add(Registries.PLACED_FEATURE, AllPlacedFeatures::bootstrap).add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, AllBiomeModifiers::bootstrap).add(CreateRegistries.POTATO_PROJECTILE_TYPE, AllPotatoProjectileTypes::bootstrap);

    public GeneratedEntriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of("create"));
    }

    public String getName() {
        return "Create's Generated Registry Entries";
    }
}
