/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  mezz.jei.api.gui.builder.IRecipeLayoutBuilder
 *  mezz.jei.api.gui.builder.IRecipeSlotBuilder
 *  mezz.jei.api.recipe.IFocusGroup
 *  mezz.jei.api.recipe.RecipeIngredientRole
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.world.item.crafting.Ingredient
 */
package com.simibubi.create.compat.jei.category;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.crafting.Ingredient;

public static abstract class ProcessingViaFanCategory.MultiOutput<T extends StandardProcessingRecipe<?>>
extends ProcessingViaFanCategory<T> {
    public ProcessingViaFanCategory.MultiOutput(CreateRecipeCategory.Info<T> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
        List<ProcessingOutput> results = ((ProcessingRecipe)recipe).getRollableResults();
        int xOffsetAmount = 1 - Math.min(3, results.size());
        builder.addSlot(RecipeIngredientRole.INPUT, 5 * xOffsetAmount + 21, 48).setBackground(ProcessingViaFanCategory.MultiOutput.getRenderedSlot(), -1, -1).addIngredients((Ingredient)((ProcessingRecipe)recipe).getIngredients().get(0));
        int i = 0;
        boolean excessive = results.size() > 9;
        for (ProcessingOutput output : results) {
            int xOffset = i % 3 * 19 + 9 * xOffsetAmount;
            int yOffset = i / 3 * -19 + (excessive ? 8 : 0);
            ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.OUTPUT, 141 + xOffset, 48 + yOffset).setBackground(ProcessingViaFanCategory.MultiOutput.getRenderedSlot(output), -1, -1).addItemStack(output.getStack())).addRichTooltipCallback(ProcessingViaFanCategory.MultiOutput.addStochasticTooltip(output));
            ++i;
        }
    }

    @Override
    protected void renderWidgets(GuiGraphics graphics, T recipe, double mouseX, double mouseY) {
        int size = ((ProcessingRecipe)recipe).getRollableResultsAsItemStacks().size();
        int xOffsetAmount = 1 - Math.min(3, size);
        AllGuiTextures.JEI_SHADOW.render(graphics, 46, 29);
        this.getBlockShadow().render(graphics, 65, 39);
        AllGuiTextures.JEI_LONG_ARROW.render(graphics, 7 * xOffsetAmount + 54, 51);
    }
}
