/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.ItemLike
 */
package com.simibubi.create.api.data.recipe;

import com.google.common.base.Supplier;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeBuilder;
import java.util.function.UnaryOperator;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

protected class MechanicalCraftingRecipeGen.GeneratedRecipeBuilder {
    private String suffix = "";
    private final Supplier<ItemLike> result;
    private int amount;

    public MechanicalCraftingRecipeGen.GeneratedRecipeBuilder(Supplier<ItemLike> result) {
        this.result = result;
        this.amount = 1;
    }

    public MechanicalCraftingRecipeGen.GeneratedRecipeBuilder returns(int amount) {
        this.amount = amount;
        return this;
    }

    public MechanicalCraftingRecipeGen.GeneratedRecipeBuilder withSuffix(String suffix) {
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
