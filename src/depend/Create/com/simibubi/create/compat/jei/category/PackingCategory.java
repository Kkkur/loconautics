/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  mezz.jei.api.gui.builder.IRecipeLayoutBuilder
 *  mezz.jei.api.gui.ingredient.IRecipeSlotsView
 *  mezz.jei.api.recipe.IFocusGroup
 *  mezz.jei.api.recipe.RecipeIngredientRole
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.core.NonNullList
 *  net.minecraft.world.item.crafting.Ingredient
 */
package com.simibubi.create.compat.jei.category;

import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.compat.jei.category.animations.AnimatedPress;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import javax.annotation.ParametersAreNonnullByDefault;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;

@ParametersAreNonnullByDefault
public class PackingCategory
extends BasinCategory {
    private final AnimatedPress press = new AnimatedPress(true);
    private final AnimatedBlazeBurner heater = new AnimatedBlazeBurner();
    private final PackingType type;

    public static PackingCategory standard(CreateRecipeCategory.Info<BasinRecipe> info) {
        return new PackingCategory(info, PackingType.COMPACTING);
    }

    public static PackingCategory autoSquare(CreateRecipeCategory.Info<BasinRecipe> info) {
        return new PackingCategory(info, PackingType.AUTO_SQUARE);
    }

    protected PackingCategory(CreateRecipeCategory.Info<BasinRecipe> info, PackingType type) {
        super(info, type != PackingType.AUTO_SQUARE);
        this.type = type;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BasinRecipe recipe, IFocusGroup focuses) {
        int rows;
        if (this.type == PackingType.COMPACTING) {
            super.setRecipe(builder, recipe, focuses);
            return;
        }
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        int size = ingredients.size();
        int n = rows = size == 4 ? 2 : 3;
        for (int i = 0; i < size; ++i) {
            Ingredient ingredient = (Ingredient)ingredients.get(i);
            builder.addSlot(RecipeIngredientRole.INPUT, (rows == 2 ? 27 : 18) + i % rows * 19, 51 - i / rows * 19).setBackground(PackingCategory.getRenderedSlot(), -1, -1).addIngredients(ingredient);
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 142, 51).setBackground(PackingCategory.getRenderedSlot(), -1, -1).addItemStack(PackingCategory.getResultItem(recipe));
    }

    @Override
    public void draw(BasinRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        if (this.type == PackingType.COMPACTING) {
            super.draw(recipe, iRecipeSlotsView, graphics, mouseX, mouseY);
        } else {
            AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 136, 32);
            AllGuiTextures.JEI_SHADOW.render(graphics, 81, 68);
        }
        HeatCondition requiredHeat = recipe.getRequiredHeat();
        if (requiredHeat != HeatCondition.NONE) {
            this.heater.withHeat(requiredHeat.visualizeAsBlazeBurner()).draw(graphics, this.getBackground().getWidth() / 2 + 3, 55);
        }
        this.press.draw(graphics, this.getBackground().getWidth() / 2 + 3, 34);
    }

    static enum PackingType {
        COMPACTING,
        AUTO_SQUARE;

    }
}
