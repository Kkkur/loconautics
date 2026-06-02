/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  mezz.jei.api.gui.builder.IRecipeLayoutBuilder
 *  mezz.jei.api.gui.builder.IRecipeSlotBuilder
 *  mezz.jei.api.gui.ingredient.IRecipeSlotsView
 *  mezz.jei.api.recipe.IFocusGroup
 *  mezz.jei.api.recipe.RecipeIngredientRole
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.createmod.catnip.gui.element.GuiGameElement$GuiRenderBuilder
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.core.NonNullList
 *  net.minecraft.util.Unit
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 */
package com.simibubi.create.compat.jei.category;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.equipment.sandPaper.SandPaperItemComponent;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import javax.annotation.ParametersAreNonnullByDefault;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

@ParametersAreNonnullByDefault
public class PolishingCategory
extends CreateRecipeCategory<SandPaperPolishingRecipe> {
    private final ItemStack renderedSandpaper = AllItems.SAND_PAPER.asStack();

    public PolishingCategory(CreateRecipeCategory.Info<SandPaperPolishingRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SandPaperPolishingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 27, 29).setBackground(PolishingCategory.getRenderedSlot(), -1, -1).addIngredients((Ingredient)recipe.getIngredients().get(0));
        ProcessingOutput output = recipe.getRollableResults().get(0);
        ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.OUTPUT, 132, 29).setBackground(PolishingCategory.getRenderedSlot(output), -1, -1).addItemStack(output.getStack())).addRichTooltipCallback(PolishingCategory.addStochasticTooltip(output));
    }

    @Override
    public void draw(SandPaperPolishingRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_SHADOW.render(graphics, 61, 21);
        AllGuiTextures.JEI_LONG_ARROW.render(graphics, 52, 32);
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        ItemStack[] matchingStacks = ((Ingredient)ingredients.get(0)).getItems();
        if (matchingStacks.length == 0) {
            return;
        }
        this.renderedSandpaper.set(AllDataComponents.SAND_PAPER_POLISHING, (Object)new SandPaperItemComponent(matchingStacks[0]));
        this.renderedSandpaper.set(AllDataComponents.SAND_PAPER_JEI, (Object)Unit.INSTANCE);
        ((GuiGameElement.GuiRenderBuilder)GuiGameElement.of((ItemStack)this.renderedSandpaper).at((float)(this.getBackground().getWidth() / 2 - 16), 0.0f, 0.0f)).scale(2.0).render(graphics);
    }
}
