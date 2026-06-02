/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  javax.annotation.ParametersAreNonnullByDefault
 *  mezz.jei.api.constants.VanillaTypes
 *  mezz.jei.api.gui.builder.IRecipeLayoutBuilder
 *  mezz.jei.api.gui.builder.IRecipeSlotBuilder
 *  mezz.jei.api.gui.ingredient.IRecipeSlotView
 *  mezz.jei.api.gui.ingredient.IRecipeSlotsView
 *  mezz.jei.api.ingredients.IIngredientType
 *  mezz.jei.api.recipe.IFocusGroup
 *  mezz.jei.api.recipe.RecipeIngredientRole
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

@ParametersAreNonnullByDefault
public class ItemApplicationCategory
extends CreateRecipeCategory<ItemApplicationRecipe> {
    public ItemApplicationCategory(CreateRecipeCategory.Info<ItemApplicationRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ItemApplicationRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 27, 38).setBackground(ItemApplicationCategory.getRenderedSlot(), -1, -1).addIngredients(recipe.getProcessedItem());
        ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.INPUT, 51, 5).setBackground(ItemApplicationCategory.getRenderedSlot(), -1, -1).addIngredients(recipe.getRequiredHeldItem())).addTooltipCallback(recipe.shouldKeepHeldItem() ? (view, tooltip) -> tooltip.add(1, CreateLang.translateDirect("recipe.deploying.not_consumed", new Object[0]).withStyle(ChatFormatting.GOLD)) : (view, tooltip) -> {});
        List<ProcessingOutput> results = recipe.getRollableResults();
        boolean single = results.size() == 1;
        for (int i = 0; i < results.size(); ++i) {
            ProcessingOutput output = results.get(i);
            int xOffset = i % 2 == 0 ? 0 : 19;
            int yOffset = i / 2 * -19;
            ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.OUTPUT, single ? 132 : 132 + xOffset, 38 + yOffset).setBackground(ItemApplicationCategory.getRenderedSlot(output), -1, -1).addItemStack(output.getStack())).addRichTooltipCallback(ItemApplicationCategory.addStochasticTooltip(output));
        }
    }

    @Override
    public void draw(ItemApplicationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_SHADOW.render(graphics, 62, 47);
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 74, 10);
        Optional displayedIngredient = ((IRecipeSlotView)recipeSlotsView.getSlotViews().get(0)).getDisplayedIngredient((IIngredientType)VanillaTypes.ITEM_STACK);
        if (displayedIngredient.isEmpty()) {
            return;
        }
        Item item = ((ItemStack)displayedIngredient.get()).getItem();
        if (!(item instanceof BlockItem)) {
            return;
        }
        BlockItem blockItem = (BlockItem)item;
        BlockState state = blockItem.getBlock().defaultBlockState();
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(74.0f, 51.0f, 100.0f);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
        int scale = 20;
        GuiGameElement.of((BlockState)state).lighting(AnimatedKinetics.DEFAULT_LIGHTING).scale((double)scale).render(graphics);
        matrixStack.popPose();
    }
}
