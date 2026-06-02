/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  javax.annotation.ParametersAreNonnullByDefault
 *  mezz.jei.api.gui.builder.IRecipeLayoutBuilder
 *  mezz.jei.api.gui.builder.IRecipeSlotBuilder
 *  mezz.jei.api.gui.ingredient.IRecipeSlotsView
 *  mezz.jei.api.ingredients.IIngredientType
 *  mezz.jei.api.neoforge.NeoForgeTypes
 *  mezz.jei.api.recipe.IFocusGroup
 *  mezz.jei.api.recipe.RecipeIngredientRole
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.core.NonNullList
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.NotNull;

@ParametersAreNonnullByDefault
public class SequencedAssemblyCategory
extends CreateRecipeCategory<SequencedAssemblyRecipe> {
    Map<ResourceLocation, SequencedAssemblySubCategory> subCategories = new HashMap<ResourceLocation, SequencedAssemblySubCategory>();
    final String[] romans = new String[]{"I", "II", "III", "IV", "V", "VI", "-"};

    public SequencedAssemblyCategory(CreateRecipeCategory.Info<SequencedAssemblyRecipe> info) {
        super(info);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SequencedAssemblyRecipe recipe, IFocusGroup focuses) {
        void var9_13;
        boolean noRandomOutput = recipe.getOutputChance() == 1.0f;
        int xOffset = noRandomOutput ? 0 : -7;
        builder.addSlot(RecipeIngredientRole.INPUT, 27 + xOffset, 91).setBackground(SequencedAssemblyCategory.getRenderedSlot(), -1, -1).addItemStacks(List.of(recipe.getIngredient().getItems()));
        ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.OUTPUT, 132 + xOffset, 91).setBackground(SequencedAssemblyCategory.getRenderedSlot(recipe.getOutputChance()), -1, -1).addItemStack(SequencedAssemblyCategory.getResultItem(recipe))).addTooltipCallback((recipeSlotView, tooltip) -> {
            if (noRandomOutput) {
                return;
            }
            float chance = recipe.getOutputChance();
            tooltip.add(1, this.chanceComponent(chance));
        });
        int width = 0;
        int margin = 3;
        for (SequencedRecipe<?> sequencedRecipe : recipe.getSequence()) {
            width += this.getSubCategory(sequencedRecipe).getWidth() + margin;
        }
        int x = (width -= margin) / -2 + this.getBackground().getWidth() / 2;
        for (SequencedRecipe<?> sequencedRecipe : recipe.getSequence()) {
            SequencedAssemblySubCategory subCategory = this.getSubCategory(sequencedRecipe);
            subCategory.setRecipe(builder, sequencedRecipe, focuses, x);
            x += subCategory.getWidth() + margin;
        }
        boolean bl = true;
        while (var9_13 < recipe.getLoops()) {
            for (SequencedRecipe<?> sequencedRecipe : recipe.getSequence()) {
                NonNullList<Ingredient> sequencedIngredients = ((ProcessingRecipe)sequencedRecipe.getRecipe()).getIngredients();
                for (Ingredient ingredient : sequencedIngredients.subList(1, sequencedIngredients.size())) {
                    builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addIngredients(ingredient);
                }
                for (SizedFluidIngredient fluidIngredient : ((ProcessingRecipe)sequencedRecipe.getRecipe()).getFluidIngredients()) {
                    builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addIngredients((IIngredientType)NeoForgeTypes.FLUID_STACK, Arrays.asList(fluidIngredient.getFluids()));
                }
            }
            ++var9_13;
        }
    }

    private SequencedAssemblySubCategory getSubCategory(SequencedRecipe<?> sequencedRecipe) {
        return this.subCategories.computeIfAbsent(RegisteredObjectsHelper.getKeyOrThrow(((ProcessingRecipe)sequencedRecipe.getRecipe()).getSerializer()), rl -> sequencedRecipe.getAsAssemblyRecipe().getJEISubCategory().get().get());
    }

    @Override
    public void draw(SequencedAssemblyRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.pushPose();
        matrixStack.translate(0.0f, 15.0f, 0.0f);
        boolean singleOutput = recipe.getOutputChance() == 1.0f;
        int xOffset = singleOutput ? 0 : -7;
        AllGuiTextures.JEI_LONG_ARROW.render(graphics, 52 + xOffset, 79);
        if (!singleOutput) {
            AllGuiTextures.JEI_CHANCE_SLOT.render(graphics, 150 + xOffset, 75);
            MutableComponent component = Component.literal((String)"?").withStyle(ChatFormatting.BOLD);
            graphics.drawString(font, (Component)component, font.width((FormattedText)component) / -2 + 8 + 150 + xOffset, 80, 0xEFEFEF);
        }
        if (recipe.getLoops() > 1) {
            matrixStack.pushPose();
            matrixStack.translate(15.0f, 9.0f, 0.0f);
            AllIcons.I_SEQ_REPEAT.render(graphics, 50 + xOffset, 75);
            MutableComponent repeat = Component.literal((String)("x" + recipe.getLoops()));
            graphics.drawString(font, (Component)repeat, 66 + xOffset, 80, 0x888888, false);
            matrixStack.popPose();
        }
        matrixStack.popPose();
        int width = 0;
        int margin = 3;
        for (SequencedRecipe<?> sequencedRecipe : recipe.getSequence()) {
            width += this.getSubCategory(sequencedRecipe).getWidth() + margin;
        }
        matrixStack.translate((float)((width -= margin) / -2 + this.getBackground().getWidth() / 2), 0.0f, 0.0f);
        matrixStack.pushPose();
        List<SequencedRecipe<?>> sequence = recipe.getSequence();
        for (int i = 0; i < sequence.size(); ++i) {
            SequencedRecipe<?> sequencedRecipe = sequence.get(i);
            SequencedAssemblySubCategory subCategory = this.getSubCategory(sequencedRecipe);
            int subWidth = subCategory.getWidth();
            MutableComponent component = Component.literal((String)this.romans[Math.min(i, 6)]);
            graphics.drawString(font, (Component)component, font.width((FormattedText)component) / -2 + subWidth / 2, 2, 0x888888, false);
            subCategory.draw(sequencedRecipe, graphics, mouseX, mouseY, i);
            matrixStack.translate((float)(subWidth + margin), 0.0f, 0.0f);
        }
        matrixStack.popPose();
        matrixStack.popPose();
    }

    @Override
    @NotNull
    public List<Component> getTooltipStrings(SequencedAssemblyRecipe recipe, IRecipeSlotsView iRecipeSlotsView, double mouseX, double mouseY) {
        ArrayList<Component> tooltip = new ArrayList<Component>();
        MutableComponent junk = CreateLang.translateDirect("recipe.assembly.junk", new Object[0]);
        boolean singleOutput = recipe.getOutputChance() == 1.0f;
        boolean willRepeat = recipe.getLoops() > 1;
        int xOffset = -7;
        int minX = 150 + xOffset;
        int maxX = minX + 18;
        int minY = 90;
        int maxY = minY + 18;
        if (!singleOutput && mouseX >= (double)minX && mouseX < (double)maxX && mouseY >= (double)minY && mouseY < (double)maxY) {
            float chance = recipe.getOutputChance();
            tooltip.add((Component)junk);
            tooltip.add((Component)this.chanceComponent(1.0f - chance));
            return tooltip;
        }
        minX = 55 + xOffset;
        maxX = minX + 65;
        minY = 92;
        maxY = minY + 24;
        if (willRepeat && mouseX >= (double)minX && mouseX < (double)maxX && mouseY >= (double)minY && mouseY < (double)maxY) {
            tooltip.add((Component)CreateLang.translateDirect("recipe.assembly.repeat", recipe.getLoops()));
            return tooltip;
        }
        if (mouseY > 5.0 && mouseY < 84.0) {
            int width = 0;
            int margin = 3;
            for (SequencedRecipe<?> sequencedRecipe : recipe.getSequence()) {
                width += this.getSubCategory(sequencedRecipe).getWidth() + margin;
            }
            xOffset = (width -= margin) / 2 + this.getBackground().getWidth() / -2;
            double relativeX = mouseX + (double)xOffset;
            List<SequencedRecipe<?>> sequence = recipe.getSequence();
            for (int i = 0; i < sequence.size(); ++i) {
                SequencedRecipe<?> sequencedRecipe = sequence.get(i);
                SequencedAssemblySubCategory subCategory = this.getSubCategory(sequencedRecipe);
                if (relativeX >= 0.0 && relativeX < (double)subCategory.getWidth()) {
                    tooltip.add((Component)CreateLang.translateDirect("recipe.assembly.step", i + 1));
                    tooltip.add((Component)sequencedRecipe.getAsAssemblyRecipe().getDescriptionForAssembly().plainCopy().withStyle(ChatFormatting.DARK_GREEN));
                    return tooltip;
                }
                relativeX -= (double)(subCategory.getWidth() + margin);
            }
        }
        return tooltip;
    }

    protected MutableComponent chanceComponent(float chance) {
        String number = (double)chance < 0.01 ? "<1" : ((double)chance > 0.99 ? ">99" : String.valueOf(Math.round(chance * 100.0f)));
        return CreateLang.translateDirect("recipe.processing.chance", number).withStyle(ChatFormatting.GOLD);
    }
}
