/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.kinetics.mixer;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

public class MixingRecipe
extends BasinRecipe {
    public MixingRecipe(ProcessingRecipeParams params) {
        super((IRecipeTypeInfo)AllRecipeTypes.MIXING, params);
    }
}
