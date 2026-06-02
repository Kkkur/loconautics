/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.compat.jei.category;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.kinetics.fan.processing.HauntingRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class FanHauntingCategory
extends ProcessingViaFanCategory.MultiOutput<HauntingRecipe> {
    public FanHauntingCategory(CreateRecipeCategory.Info<HauntingRecipe> info) {
        super(info);
    }

    @Override
    protected AllGuiTextures getBlockShadow() {
        return AllGuiTextures.JEI_LIGHT;
    }

    @Override
    protected void renderAttachedBlock(GuiGraphics graphics) {
        GuiGameElement.of((BlockState)Blocks.SOUL_FIRE.defaultBlockState()).scale(24.0).atLocal(0.0, 0.0, 2.0).lighting(AnimatedKinetics.DEFAULT_LIGHTING).render(graphics);
    }
}
