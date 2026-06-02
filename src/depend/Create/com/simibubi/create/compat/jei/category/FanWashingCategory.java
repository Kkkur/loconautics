/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 */
package com.simibubi.create.compat.jei.category;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.kinetics.fan.processing.SplashingRecipe;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class FanWashingCategory
extends ProcessingViaFanCategory.MultiOutput<SplashingRecipe> {
    public FanWashingCategory(CreateRecipeCategory.Info<SplashingRecipe> info) {
        super(info);
    }

    @Override
    protected void renderAttachedBlock(GuiGraphics graphics) {
        GuiGameElement.of((Fluid)Fluids.WATER).scale(24.0).atLocal(0.0, 0.0, 2.0).lighting(AnimatedKinetics.DEFAULT_LIGHTING).render(graphics);
    }
}
