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
 *  net.createmod.catnip.layout.LayoutHelper
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.world.item.crafting.Ingredient
 */
package com.simibubi.create.compat.jei.category;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedCrushingWheels;
import com.simibubi.create.content.kinetics.crusher.AbstractCrushingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.createmod.catnip.layout.LayoutHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.crafting.Ingredient;

@ParametersAreNonnullByDefault
public class CrushingCategory
extends CreateRecipeCategory<AbstractCrushingRecipe> {
    private final AnimatedCrushingWheels crushingWheels = new AnimatedCrushingWheels();

    public CrushingCategory(CreateRecipeCategory.Info<AbstractCrushingRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AbstractCrushingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 51, 3).setBackground(CrushingCategory.getRenderedSlot(), -1, -1).addIngredients((Ingredient)recipe.getIngredients().get(0));
        int xOffset = this.getBackground().getWidth() / 2;
        int yOffset = 86;
        this.layoutOutput(recipe).forEach(layoutEntry -> ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.OUTPUT, xOffset + layoutEntry.posX() + 1, yOffset + layoutEntry.posY() + 1).setBackground(CrushingCategory.getRenderedSlot(layoutEntry.output()), -1, -1).addItemStack(layoutEntry.output().getStack())).addRichTooltipCallback(CrushingCategory.addStochasticTooltip(layoutEntry.output())));
    }

    private List<LayoutEntry> layoutOutput(StandardProcessingRecipe<?> recipe) {
        int size = recipe.getRollableResults().size();
        ArrayList<LayoutEntry> positions = new ArrayList<LayoutEntry>(size);
        LayoutHelper layout = LayoutHelper.centeredHorizontal((int)size, (int)1, (int)18, (int)18, (int)1);
        for (ProcessingOutput result : recipe.getRollableResults()) {
            positions.add(new LayoutEntry(result, layout.getX(), layout.getY()));
            layout.next();
        }
        return positions;
    }

    @Override
    public void draw(AbstractCrushingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 72, 7);
        this.crushingWheels.draw(graphics, 62, 59);
    }

    private record LayoutEntry(ProcessingOutput output, int posX, int posY) {
    }
}
