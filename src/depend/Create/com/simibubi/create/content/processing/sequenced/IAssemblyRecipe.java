/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
 */
package com.simibubi.create.content.processing.sequenced;

import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public interface IAssemblyRecipe {
    default public boolean supportsAssembly() {
        return true;
    }

    @OnlyIn(value=Dist.CLIENT)
    public Component getDescriptionForAssembly();

    public void addRequiredMachines(Set<ItemLike> var1);

    public void addAssemblyIngredients(List<Ingredient> var1);

    default public void addAssemblyFluidIngredients(List<SizedFluidIngredient> list) {
    }

    public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory();
}
