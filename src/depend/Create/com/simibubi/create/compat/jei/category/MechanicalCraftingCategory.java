/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  javax.annotation.ParametersAreNonnullByDefault
 *  mezz.jei.api.constants.VanillaTypes
 *  mezz.jei.api.gui.builder.IRecipeLayoutBuilder
 *  mezz.jei.api.gui.ingredient.IRecipeSlotsView
 *  mezz.jei.api.ingredients.IIngredientRenderer
 *  mezz.jei.api.ingredients.IIngredientType
 *  mezz.jei.api.recipe.IFocusGroup
 *  mezz.jei.api.recipe.RecipeIngredientRole
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.ShapedRecipe
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Matrix4fStack
 */
package com.simibubi.create.compat.jei.category;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedCrafter;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4fStack;

@ParametersAreNonnullByDefault
public class MechanicalCraftingCategory
extends CreateRecipeCategory<CraftingRecipe> {
    private final AnimatedCrafter crafter = new AnimatedCrafter();
    static int maxSize = 100;

    public MechanicalCraftingCategory(CreateRecipeCategory.Info<CraftingRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CraftingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 134, 81).addItemStack(MechanicalCraftingCategory.getResultItem(recipe));
        int x = MechanicalCraftingCategory.getXPadding(recipe);
        int y = MechanicalCraftingCategory.getYPadding(recipe);
        float scale = MechanicalCraftingCategory.getScale(recipe);
        CrafterIngredientRenderer renderer = new CrafterIngredientRenderer(recipe);
        int i = 0;
        for (Ingredient ingredient : recipe.getIngredients()) {
            float f = 19.0f * scale;
            int xPosition = (int)((float)(x + 1) + (float)(i % MechanicalCraftingCategory.getWidth(recipe)) * f);
            int yPosition = (int)((float)(y + 1) + (float)(i / MechanicalCraftingCategory.getWidth(recipe)) * f);
            builder.addSlot(RecipeIngredientRole.INPUT, xPosition, yPosition).setCustomRenderer((IIngredientType)VanillaTypes.ITEM_STACK, (IIngredientRenderer)renderer).addIngredients(ingredient);
            ++i;
        }
    }

    public static float getScale(CraftingRecipe recipe) {
        int w = MechanicalCraftingCategory.getWidth(recipe);
        int h = MechanicalCraftingCategory.getHeight(recipe);
        return Math.min(1.0f, (float)maxSize / (19.0f * (float)Math.max(w, h)));
    }

    public static int getYPadding(CraftingRecipe recipe) {
        return 53 - (int)((double)(MechanicalCraftingCategory.getScale(recipe) * (float)MechanicalCraftingCategory.getHeight(recipe) * 19.0f) * 0.5);
    }

    public static int getXPadding(CraftingRecipe recipe) {
        return 53 - (int)((double)(MechanicalCraftingCategory.getScale(recipe) * (float)MechanicalCraftingCategory.getWidth(recipe) * 19.0f) * 0.5);
    }

    private static int getWidth(CraftingRecipe recipe) {
        return recipe instanceof ShapedRecipe ? ((ShapedRecipe)recipe).getWidth() : 1;
    }

    private static int getHeight(CraftingRecipe recipe) {
        return recipe instanceof ShapedRecipe ? ((ShapedRecipe)recipe).getHeight() : 1;
    }

    @Override
    public void draw(CraftingRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        float scale = MechanicalCraftingCategory.getScale(recipe);
        matrixStack.translate((float)MechanicalCraftingCategory.getXPadding(recipe), (float)MechanicalCraftingCategory.getYPadding(recipe), 0.0f);
        for (int row = 0; row < MechanicalCraftingCategory.getHeight(recipe); ++row) {
            int pIndex;
            for (int col = 0; col < MechanicalCraftingCategory.getWidth(recipe) && (pIndex = row * MechanicalCraftingCategory.getWidth(recipe) + col) < recipe.getIngredients().size(); ++col) {
                if (((Ingredient)recipe.getIngredients().get(pIndex)).isEmpty()) continue;
                matrixStack.pushPose();
                matrixStack.translate((float)(col * 19) * scale, (float)(row * 19) * scale, 0.0f);
                matrixStack.scale(scale, scale, scale);
                AllGuiTextures.JEI_SLOT.render(graphics, 0, 0);
                matrixStack.popPose();
            }
        }
        matrixStack.popPose();
        AllGuiTextures.JEI_SLOT.render(graphics, 133, 80);
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 128, 59);
        this.crafter.draw(graphics, 129, 25);
        matrixStack.pushPose();
        matrixStack.translate(0.0f, 0.0f, 300.0f);
        int amount = 0;
        for (Ingredient ingredient : recipe.getIngredients()) {
            if (Ingredient.EMPTY == ingredient) continue;
            ++amount;
        }
        graphics.drawString(Minecraft.getInstance().font, "" + amount, 142, 39, 0xFFFFFF);
        matrixStack.popPose();
    }

    private static final class CrafterIngredientRenderer
    implements IIngredientRenderer<ItemStack> {
        private final CraftingRecipe recipe;
        private final float scale;

        public CrafterIngredientRenderer(CraftingRecipe recipe) {
            this.recipe = recipe;
            this.scale = MechanicalCraftingCategory.getScale(recipe);
        }

        public void render(GuiGraphics graphics, @NotNull ItemStack ingredient) {
            PoseStack matrixStack = graphics.pose();
            matrixStack.pushPose();
            float scale = MechanicalCraftingCategory.getScale(this.recipe);
            matrixStack.scale(scale, scale, scale);
            if (ingredient != null) {
                Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
                modelViewStack.pushMatrix();
                RenderSystem.applyModelViewMatrix();
                RenderSystem.enableDepthTest();
                Minecraft minecraft = Minecraft.getInstance();
                Font font = this.getFontRenderer(minecraft, ingredient);
                graphics.renderItem(ingredient, 0, 0);
                graphics.renderItemDecorations(font, ingredient, 0, 0, null);
                RenderSystem.disableBlend();
                modelViewStack.popMatrix();
                RenderSystem.applyModelViewMatrix();
            }
            matrixStack.popPose();
        }

        public int getWidth() {
            return (int)(16.0f * this.scale);
        }

        public int getHeight() {
            return (int)(16.0f * this.scale);
        }

        public List<Component> getTooltip(ItemStack ingredient, TooltipFlag tooltipFlag) {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            try {
                return ingredient.getTooltipLines(Item.TooltipContext.of((Level)minecraft.level), (Player)player, tooltipFlag);
            }
            catch (LinkageError | RuntimeException e) {
                ArrayList<Component> list = new ArrayList<Component>();
                MutableComponent crash = Component.translatable((String)"jei.tooltip.error.crash");
                list.add((Component)crash.withStyle(ChatFormatting.RED));
                return list;
            }
        }
    }
}
