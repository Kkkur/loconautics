/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  mezz.jei.api.gui.builder.IRecipeLayoutBuilder
 *  mezz.jei.api.gui.builder.IRecipeSlotBuilder
 *  mezz.jei.api.recipe.IFocusGroup
 *  mezz.jei.api.recipe.RecipeIngredientRole
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.world.item.crafting.Ingredient
 */
package com.simibubi.create.compat.jei.category.sequencedAssembly;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedDeployer;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import com.simibubi.create.foundation.utility.CreateLang;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.crafting.Ingredient;

public static class SequencedAssemblySubCategory.AssemblyDeploying
extends SequencedAssemblySubCategory {
    AnimatedDeployer deployer = new AnimatedDeployer();

    public SequencedAssemblySubCategory.AssemblyDeploying() {
        super(25);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SequencedRecipe<?> recipe, IFocusGroup focuses, int x) {
        DeployerApplicationRecipe deployerRecipe;
        IRecipeSlotBuilder slot = (IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.INPUT, x + 4, 15).setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1).addIngredients((Ingredient)((ProcessingRecipe)recipe.getRecipe()).getIngredients().get(1));
        IAssemblyRecipe iAssemblyRecipe = recipe.getAsAssemblyRecipe();
        if (iAssemblyRecipe instanceof DeployerApplicationRecipe && (deployerRecipe = (DeployerApplicationRecipe)iAssemblyRecipe).shouldKeepHeldItem()) {
            slot.addTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(1, CreateLang.translateDirect("recipe.deploying.not_consumed", new Object[0]).withStyle(ChatFormatting.GOLD)));
        }
    }

    @Override
    public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {
        PoseStack ms = graphics.pose();
        this.deployer.offset = index;
        ms.pushPose();
        ms.translate(-7.0f, 50.0f, 0.0f);
        ms.scale(0.75f, 0.75f, 0.75f);
        this.deployer.draw(graphics, this.getWidth() / 2, 0);
        ms.popPose();
    }
}
