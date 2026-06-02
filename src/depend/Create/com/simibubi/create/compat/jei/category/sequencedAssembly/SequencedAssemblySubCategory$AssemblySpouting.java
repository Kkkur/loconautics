/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  mezz.jei.api.gui.builder.IRecipeLayoutBuilder
 *  mezz.jei.api.recipe.IFocusGroup
 *  net.minecraft.client.gui.GuiGraphics
 *  net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
 */
package com.simibubi.create.compat.jei.category.sequencedAssembly;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedSpout;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import java.util.Arrays;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public static class SequencedAssemblySubCategory.AssemblySpouting
extends SequencedAssemblySubCategory {
    AnimatedSpout spout = new AnimatedSpout();

    public SequencedAssemblySubCategory.AssemblySpouting() {
        super(25);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SequencedRecipe<?> recipe, IFocusGroup focuses, int x) {
        SizedFluidIngredient fluidIngredient = (SizedFluidIngredient)((ProcessingRecipe)recipe.getRecipe()).getFluidIngredients().get(0);
        CreateRecipeCategory.addFluidSlot(builder, x + 4, 15, fluidIngredient);
    }

    @Override
    public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {
        PoseStack ms = graphics.pose();
        this.spout.offset = index;
        ms.pushPose();
        ms.translate(-7.0f, 50.0f, 0.0f);
        ms.scale(0.75f, 0.75f, 0.75f);
        this.spout.withFluids(Arrays.asList(((SizedFluidIngredient)((ProcessingRecipe)recipe.getRecipe()).getFluidIngredients().get(0)).getFluids())).draw(graphics, this.getWidth() / 2, 0);
        ms.popPose();
    }
}
