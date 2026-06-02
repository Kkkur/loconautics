/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.world.level.levelgen.feature.Feature
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package com.simibubi.create.infrastructure.worldgen;

import com.simibubi.create.infrastructure.worldgen.LayeredOreFeature;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

public class AllFeatures {
    private static final DeferredRegister<Feature<?>> REGISTER = DeferredRegister.create((Registry)BuiltInRegistries.FEATURE, (String)"create");
    public static final DeferredHolder<Feature<?>, LayeredOreFeature> LAYERED_ORE = REGISTER.register("layered_ore", () -> new LayeredOreFeature());

    @ApiStatus.Internal
    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
