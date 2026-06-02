/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.advancements.Advancement$Builder
 *  net.minecraft.advancements.AdvancementHolder
 *  net.minecraft.data.recipes.RecipeOutput
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.crafting.Recipe
 *  net.neoforged.neoforge.common.conditions.ICondition
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.data.recipe;

import com.simibubi.create.foundation.data.recipe.CreateStandardRecipeGen;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
private record CreateStandardRecipeGen.ModdedCookingRecipeOutput(RecipeOutput wrapped, ResourceLocation outputOverride) implements RecipeOutput
{
    public Advancement.Builder advancement() {
        return this.wrapped.advancement();
    }

    public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition ... conditions) {
        this.wrapped.accept(id, (Recipe)new CreateStandardRecipeGen.ModdedCookingRecipeOutputShim(recipe, this.outputOverride), advancement, conditions);
    }
}
