/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.level.levelgen.placement.PlacementModifierType
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package com.simibubi.create.infrastructure.worldgen;

import com.simibubi.create.infrastructure.worldgen.ConfigPlacementFilter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

public class AllPlacementModifiers {
    private static final DeferredRegister<PlacementModifierType<?>> REGISTER = DeferredRegister.create((ResourceKey)Registries.PLACEMENT_MODIFIER_TYPE, (String)"create");
    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<ConfigPlacementFilter>> CONFIG_FILTER = REGISTER.register("config_filter", () -> () -> ConfigPlacementFilter.CODEC);

    @ApiStatus.Internal
    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
