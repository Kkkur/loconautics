/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  mezz.jei.api.gui.ingredient.IRecipeSlotsView
 *  net.minecraft.client.gui.GuiGraphics
 */
package com.simibubi.create.compat.jei.category;

import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.compat.jei.category.animations.AnimatedMixer;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import javax.annotation.ParametersAreNonnullByDefault;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.gui.GuiGraphics;

@ParametersAreNonnullByDefault
public class MixingCategory
extends BasinCategory {
    private final AnimatedMixer mixer = new AnimatedMixer();
    private final AnimatedBlazeBurner heater = new AnimatedBlazeBurner();
    MixingType type;

    public static MixingCategory standard(CreateRecipeCategory.Info<BasinRecipe> info) {
        return new MixingCategory(info, MixingType.MIXING);
    }

    public static MixingCategory autoShapeless(CreateRecipeCategory.Info<BasinRecipe> info) {
        return new MixingCategory(info, MixingType.AUTO_SHAPELESS);
    }

    public static MixingCategory autoBrewing(CreateRecipeCategory.Info<BasinRecipe> info) {
        return new MixingCategory(info, MixingType.AUTO_BREWING);
    }

    protected MixingCategory(CreateRecipeCategory.Info<BasinRecipe> info, MixingType type) {
        super(info, type != MixingType.AUTO_SHAPELESS);
        this.type = type;
    }

    @Override
    public void draw(BasinRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, iRecipeSlotsView, graphics, mouseX, mouseY);
        HeatCondition requiredHeat = recipe.getRequiredHeat();
        if (requiredHeat != HeatCondition.NONE) {
            this.heater.withHeat(requiredHeat.visualizeAsBlazeBurner()).draw(graphics, this.getBackground().getWidth() / 2 + 3, 55);
        }
        this.mixer.draw(graphics, this.getBackground().getWidth() / 2 + 3, 34);
    }

    static enum MixingType {
        MIXING,
        AUTO_SHAPELESS,
        AUTO_BREWING;

    }
}
