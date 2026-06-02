/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.recipes.RecipeOutput
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.ItemLike
 */
package com.simibubi.create.api.data.recipe;

import com.google.common.base.Supplier;
import com.simibubi.create.Create;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public abstract class MechanicalCraftingRecipeGen
extends BaseRecipeProvider {
    public MechanicalCraftingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    protected GeneratedRecipeBuilder create(Supplier<ItemLike> result) {
        return new GeneratedRecipeBuilder(result);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        this.all.forEach(c -> c.register(output));
        Create.LOGGER.info("{} registered {} recipe{}", new Object[]{this.getName(), this.all.size(), this.all.size() == 1 ? "" : "s"});
    }

    public String getName() {
        return this.modid + "'s mechanical crafting recipes";
    }

    protected class GeneratedRecipeBuilder {
        private String suffix = "";
        private final Supplier<ItemLike> result;
        private int amount;

        public GeneratedRecipeBuilder(Supplier<ItemLike> result) {
            this.result = result;
            this.amount = 1;
        }

        public GeneratedRecipeBuilder returns(int amount) {
            this.amount = amount;
            return this;
        }

        public GeneratedRecipeBuilder withSuffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        public BaseRecipeProvider.GeneratedRecipe recipe(UnaryOperator<MechanicalCraftingRecipeBuilder> builder) {
            return MechanicalCraftingRecipeGen.this.register(consumer -> {
                MechanicalCraftingRecipeBuilder b = (MechanicalCraftingRecipeBuilder)builder.apply(MechanicalCraftingRecipeBuilder.shapedRecipe((ItemLike)this.result.get(), this.amount));
                ResourceLocation location = MechanicalCraftingRecipeGen.this.asResource("mechanical_crafting/" + RegisteredObjectsHelper.getKeyOrThrow((Item)((ItemLike)this.result.get()).asItem()).getPath() + this.suffix);
                b.build(consumer, location);
            });
        }
    }
}
