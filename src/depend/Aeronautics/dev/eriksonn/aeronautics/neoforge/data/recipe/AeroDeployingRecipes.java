/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.data.recipe.DeployingRecipeGen
 *  com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe$Builder
 *  com.simibubi.create.foundation.utility.DyeHelper
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  org.jetbrains.annotations.NotNull
 */
package dev.eriksonn.aeronautics.neoforge.data.recipe;

import com.simibubi.create.api.data.recipe.DeployingRecipeGen;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.foundation.utility.DyeHelper;
import dev.eriksonn.aeronautics.index.AeroBlocks;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

public class AeroDeployingRecipes
extends DeployingRecipeGen {
    public AeroDeployingRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, "aeronautics");
        for (DyeColor color : DyeColor.values()) {
            this.create("deploying_envelope_" + color.getName(), b -> (ItemApplicationRecipe.Builder)((ItemApplicationRecipe.Builder)((ItemApplicationRecipe.Builder)b.require(DyeHelper.getWoolOfDye((DyeColor)color))).require((ItemLike)Items.STICK)).output((ItemLike)AeroBlocks.DYED_ENVELOPE_BLOCKS.get(color), 3));
        }
    }

    @NotNull
    public String getName() {
        return "Aero's Devious Deploying Recipes";
    }
}
