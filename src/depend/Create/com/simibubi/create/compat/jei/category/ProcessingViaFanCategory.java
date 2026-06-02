/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  javax.annotation.ParametersAreNonnullByDefault
 *  mezz.jei.api.gui.builder.IRecipeLayoutBuilder
 *  mezz.jei.api.gui.builder.IRecipeSlotBuilder
 *  mezz.jei.api.gui.ingredient.IRecipeSlotsView
 *  mezz.jei.api.recipe.IFocusGroup
 *  mezz.jei.api.recipe.RecipeIngredientRole
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 */
package com.simibubi.create.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

@ParametersAreNonnullByDefault
public abstract class ProcessingViaFanCategory<T extends Recipe<?>>
extends CreateRecipeCategory<T> {
    protected static final int SCALE = 24;

    public ProcessingViaFanCategory(CreateRecipeCategory.Info<T> info) {
        super(info);
    }

    public static Supplier<ItemStack> getFan(String name) {
        ItemStack stack = AllBlocks.ENCASED_FAN.asStack();
        stack.set(DataComponents.CUSTOM_NAME, (Object)CreateLang.translateDirect("recipe." + name + ".fan", new Object[0]).withStyle(style -> style.withItalic(Boolean.valueOf(false))));
        return () -> stack;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 21, 48).setBackground(ProcessingViaFanCategory.getRenderedSlot(), -1, -1).addIngredients((Ingredient)recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 141, 48).setBackground(ProcessingViaFanCategory.getRenderedSlot(), -1, -1).addItemStack(ProcessingViaFanCategory.getResultItem(recipe));
    }

    @Override
    public void draw(T recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        this.renderWidgets(graphics, recipe, mouseX, mouseY);
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        this.translateFan(matrixStack);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-12.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
        AnimatedKinetics.defaultBlockElement(AllPartialModels.ENCASED_FAN_INNER).rotateBlock(180.0, 0.0, (double)(AnimatedKinetics.getCurrentAngle() * 16.0f)).scale(24.0).render(graphics);
        AnimatedKinetics.defaultBlockElement(AllBlocks.ENCASED_FAN.getDefaultState()).rotateBlock(0.0, 180.0, 0.0).atLocal(0.0, 0.0, 0.0).scale(24.0).render(graphics);
        this.renderAttachedBlock(graphics);
        matrixStack.popPose();
    }

    protected void renderWidgets(GuiGraphics graphics, T recipe, double mouseX, double mouseY) {
        AllGuiTextures.JEI_SHADOW.render(graphics, 46, 29);
        this.getBlockShadow().render(graphics, 65, 39);
        AllGuiTextures.JEI_LONG_ARROW.render(graphics, 54, 51);
    }

    protected AllGuiTextures getBlockShadow() {
        return AllGuiTextures.JEI_SHADOW;
    }

    protected void translateFan(PoseStack matrixStack) {
        matrixStack.translate(56.0f, 33.0f, 0.0f);
    }

    protected abstract void renderAttachedBlock(GuiGraphics var1);

    public static abstract class MultiOutput<T extends StandardProcessingRecipe<?>>
    extends ProcessingViaFanCategory<T> {
        public MultiOutput(CreateRecipeCategory.Info<T> info) {
            super(info);
        }

        @Override
        public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
            List<ProcessingOutput> results = ((ProcessingRecipe)recipe).getRollableResults();
            int xOffsetAmount = 1 - Math.min(3, results.size());
            builder.addSlot(RecipeIngredientRole.INPUT, 5 * xOffsetAmount + 21, 48).setBackground(MultiOutput.getRenderedSlot(), -1, -1).addIngredients((Ingredient)((ProcessingRecipe)recipe).getIngredients().get(0));
            int i = 0;
            boolean excessive = results.size() > 9;
            for (ProcessingOutput output : results) {
                int xOffset = i % 3 * 19 + 9 * xOffsetAmount;
                int yOffset = i / 3 * -19 + (excessive ? 8 : 0);
                ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.OUTPUT, 141 + xOffset, 48 + yOffset).setBackground(MultiOutput.getRenderedSlot(output), -1, -1).addItemStack(output.getStack())).addRichTooltipCallback(MultiOutput.addStochasticTooltip(output));
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
}
