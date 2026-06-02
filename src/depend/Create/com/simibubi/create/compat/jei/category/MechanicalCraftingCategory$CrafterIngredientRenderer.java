/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  mezz.jei.api.ingredients.IIngredientRenderer
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
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Matrix4fStack
 */
package com.simibubi.create.compat.jei.category;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.MechanicalCraftingCategory;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.ingredients.IIngredientRenderer;
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
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4fStack;

private static final class MechanicalCraftingCategory.CrafterIngredientRenderer
implements IIngredientRenderer<ItemStack> {
    private final CraftingRecipe recipe;
    private final float scale;

    public MechanicalCraftingCategory.CrafterIngredientRenderer(CraftingRecipe recipe) {
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
