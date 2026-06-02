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
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 */
package dev.simulated_team.simulated.data.neoforge;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeGen;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.index.SimItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class SimMechanicalCraftingRecipes
extends MechanicalCraftingRecipeGen {
    private final BaseRecipeProvider.GeneratedRecipe LINKED_TYPEWRITER = this.create(() -> SimBlocks.LINKED_TYPEWRITER.get()).returns(1).recipe(b -> b.patternLine("BBBBT").patternLine("BBBBB").patternLine(" GPG ").key(Character.valueOf('B'), Ingredient.of((TagKey)ItemTags.BUTTONS)).key(Character.valueOf('T'), (ItemLike)AllItems.TRANSMITTER).key(Character.valueOf('G'), (ItemLike)AllItems.GOLDEN_SHEET).key(Character.valueOf('P'), (ItemLike)AllItems.PRECISION_MECHANISM));
    private final BaseRecipeProvider.GeneratedRecipe PLUNGER_LAUNCHER = this.create(() -> SimItems.PLUNGER_LAUNCHER.get()).returns(1).recipe(b -> b.patternLine("   P").patternLine("AMFR").patternLine("CC P").key(Character.valueOf('C'), (ItemLike)Items.COPPER_INGOT).key(Character.valueOf('R'), SimItems.ROPE_COUPLING).key(Character.valueOf('A'), (ItemLike)AllItems.ANDESITE_ALLOY).key(Character.valueOf('M'), (ItemLike)AllItems.PRECISION_MECHANISM).key(Character.valueOf('P'), (ItemLike)Items.SLIME_BALL).key(Character.valueOf('F'), (ItemLike)AllBlocks.FLUID_PIPE));
    private final BaseRecipeProvider.GeneratedRecipe DOCKING_CONNECTOR = this.create(() -> SimBlocks.DOCKING_CONNECTOR.get()).returns(2).recipe(b -> b.patternLine("ICI").patternLine(" C ").patternLine("PAP").patternLine("BEB").key(Character.valueOf('B'), (ItemLike)AllItems.BRASS_SHEET).key(Character.valueOf('E'), (ItemLike)AllItems.ELECTRON_TUBE).key(Character.valueOf('P'), (ItemLike)Blocks.PISTON).key(Character.valueOf('A'), (ItemLike)AllBlocks.BRASS_CASING).key(Character.valueOf('C'), (ItemLike)AllBlocks.CHUTE).key(Character.valueOf('I'), (ItemLike)AllItems.IRON_SHEET));

    public SimMechanicalCraftingRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, "simulated");
    }

    public String getName() {
        return "Simulated's Marvelous Mechanical Crafting Recipes";
    }
}
