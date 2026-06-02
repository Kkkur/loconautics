/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package dev.simulated_team.simulated.index.neoforge;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

private static class SimNeoForgeRecipeTypes.Registers {
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER = DeferredRegister.create((Registry)BuiltInRegistries.RECIPE_SERIALIZER, (String)"simulated");
    private static final DeferredRegister<RecipeType<?>> TYPE_REGISTER = DeferredRegister.create((ResourceKey)Registries.RECIPE_TYPE, (String)"simulated");

    private SimNeoForgeRecipeTypes.Registers() {
    }
}
