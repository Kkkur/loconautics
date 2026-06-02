/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.level.ItemLike
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.kinetics.deployer;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.AllTags;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipeParams;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class DeployerApplicationRecipe
extends ItemApplicationRecipe
implements IAssemblyRecipe {
    public DeployerApplicationRecipe(ItemApplicationRecipeParams params) {
        super(AllRecipeTypes.DEPLOYING, params);
    }

    @Override
    protected int getMaxOutputCount() {
        return 4;
    }

    public static RecipeHolder<DeployerApplicationRecipe> convert(RecipeHolder<?> sandpaperRecipe) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath((String)sandpaperRecipe.id().getNamespace(), (String)(sandpaperRecipe.id().getPath() + "_using_deployer"));
        DeployerApplicationRecipe recipe = (DeployerApplicationRecipe)((ItemApplicationRecipe.Builder)((ItemApplicationRecipe.Builder)((ItemApplicationRecipe.Builder)new ItemApplicationRecipe.Builder<DeployerApplicationRecipe>(DeployerApplicationRecipe::new, id).require((Ingredient)sandpaperRecipe.value().getIngredients().get(0))).require(AllTags.AllItemTags.SANDPAPER.tag)).output(sandpaperRecipe.value().getResultItem((HolderLookup.Provider)Minecraft.getInstance().level.registryAccess()))).build();
        return new RecipeHolder(id, (Recipe)recipe);
    }

    @Override
    public void addAssemblyIngredients(List<Ingredient> list) {
        list.add((Ingredient)this.ingredients.get(1));
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public Component getDescriptionForAssembly() {
        ItemStack[] matchingStacks = ((Ingredient)this.ingredients.get(1)).getItems();
        if (matchingStacks.length == 0) {
            return Component.literal((String)"Invalid");
        }
        return CreateLang.translateDirect("recipe.assembly.deploying_item", Component.translatable((String)matchingStacks[0].getDescriptionId()).getString());
    }

    @Override
    public void addRequiredMachines(Set<ItemLike> list) {
        list.add((ItemLike)AllBlocks.DEPLOYER.get());
    }

    @Override
    public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
        return () -> SequencedAssemblySubCategory.AssemblyDeploying::new;
    }
}
