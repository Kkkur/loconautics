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
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.StonecutterRecipe
 */
package com.simibubi.create.compat.jei.category;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedSaw;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.item.ItemHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.StonecutterRecipe;

@ParametersAreNonnullByDefault
public class BlockCuttingCategory
extends CreateRecipeCategory<CondensedBlockCuttingRecipe> {
    private final AnimatedSaw saw = new AnimatedSaw();

    public BlockCuttingCategory(CreateRecipeCategory.Info<CondensedBlockCuttingRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CondensedBlockCuttingRecipe recipe, IFocusGroup focuses) {
        List<List<ItemStack>> results = recipe.getCondensedOutputs();
        builder.addSlot(RecipeIngredientRole.INPUT, 5, 5).setBackground(BlockCuttingCategory.getRenderedSlot(), -1, -1).addItemStacks(Arrays.asList(((Ingredient)recipe.getIngredients().get(0)).getItems()));
        int i = 0;
        for (List<ItemStack> itemStacks : results) {
            int xPos = 78 + i % 5 * 19;
            int yPos = 48 + i / 5 * -19;
            builder.addSlot(RecipeIngredientRole.OUTPUT, xPos, yPos).setBackground(BlockCuttingCategory.getRenderedSlot(), -1, -1).addItemStacks(itemStacks);
            ++i;
        }
    }

    @Override
    public void draw(CondensedBlockCuttingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 31, 6);
        AllGuiTextures.JEI_SHADOW.render(graphics, 16, 50);
        this.saw.draw(graphics, 33, 37);
    }

    public static List<RecipeHolder<CondensedBlockCuttingRecipe>> condenseRecipes(List<RecipeHolder<?>> stoneCuttingRecipes) {
        ArrayList<RecipeHolder<CondensedBlockCuttingRecipe>> condensed = new ArrayList<RecipeHolder<CondensedBlockCuttingRecipe>>();
        block0: for (RecipeHolder<?> recipe : stoneCuttingRecipes) {
            Ingredient i1 = (Ingredient)recipe.value().getIngredients().get(0);
            for (RecipeHolder recipeHolder : condensed) {
                if (!ItemHelper.matchIngredients(i1, (Ingredient)((CondensedBlockCuttingRecipe)recipeHolder.value()).getIngredients().get(0))) continue;
                ((CondensedBlockCuttingRecipe)recipeHolder.value()).addOutput(BlockCuttingCategory.getResultItem(recipe.value()));
                continue block0;
            }
            CondensedBlockCuttingRecipe cr = new CondensedBlockCuttingRecipe(i1);
            cr.addOutput(BlockCuttingCategory.getResultItem(recipe.value()));
            condensed.add((RecipeHolder<CondensedBlockCuttingRecipe>)new RecipeHolder(recipe.id(), (Recipe)cr));
        }
        return condensed;
    }

    public static class CondensedBlockCuttingRecipe
    extends StonecutterRecipe {
        List<ItemStack> outputs = new ArrayList<ItemStack>();

        public CondensedBlockCuttingRecipe(Ingredient ingredient) {
            super("", ingredient, ItemStack.EMPTY);
        }

        public void addOutput(ItemStack stack) {
            this.outputs.add(stack);
        }

        public List<ItemStack> getOutputs() {
            return this.outputs;
        }

        public List<List<ItemStack>> getCondensedOutputs() {
            ArrayList<List<ItemStack>> result = new ArrayList<List<ItemStack>>();
            int index = 0;
            boolean firstPass = true;
            for (ItemStack itemStack : this.outputs) {
                if (firstPass) {
                    result.add(new ArrayList());
                }
                ((List)result.get(index)).add(itemStack);
                if (++index < 15) continue;
                index = 0;
                firstPass = false;
            }
            return result;
        }

        public boolean isSpecial() {
            return true;
        }
    }
}
