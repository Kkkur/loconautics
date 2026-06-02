/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllBlocks
 *  com.simibubi.create.AllItems
 *  com.simibubi.create.api.data.recipe.BaseRecipeProvider$GeneratedRecipe
 *  com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeGen
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 */
package dev.eriksonn.aeronautics.neoforge.data.recipe;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeGen;
import dev.eriksonn.aeronautics.index.AeroBlocks;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class AeroMechanicalCraftingRecipes
extends MechanicalCraftingRecipeGen {
    private final BaseRecipeProvider.GeneratedRecipe MOUNTED_POTATO_CANNON = this.create(() -> AeroBlocks.MOUNTED_POTATO_CANNON.get()).returns(1).recipe(b -> b.patternLine("SR  ").patternLine("KCPP").patternLine("SR  ").key(Character.valueOf('S'), (ItemLike)AllItems.COPPER_SHEET).key(Character.valueOf('R'), (ItemLike)Items.REDSTONE).key(Character.valueOf('K'), (ItemLike)Blocks.DRIED_KELP_BLOCK).key(Character.valueOf('C'), (ItemLike)AllBlocks.COGWHEEL).key(Character.valueOf('P'), (ItemLike)AllBlocks.FLUID_PIPE));

    public AeroMechanicalCraftingRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, "aeronautics");
    }

    public String getName() {
        return "Aero's Mischievous Mechanical Crafting Recipes";
    }
}
