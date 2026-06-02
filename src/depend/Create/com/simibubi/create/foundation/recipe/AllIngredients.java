/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceKey
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.common.crafting.IngredientType
 *  net.neoforged.neoforge.registries.DeferredRegister
 *  net.neoforged.neoforge.registries.NeoForgeRegistries$Keys
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package com.simibubi.create.foundation.recipe;

import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.ApiStatus;

public class AllIngredients {
    public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister.create((ResourceKey)NeoForgeRegistries.Keys.INGREDIENT_TYPES, (String)"create");

    @ApiStatus.Internal
    public static void register(IEventBus modEventBus) {
        INGREDIENT_TYPES.register(modEventBus);
    }
}
