/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllBlocks
 *  com.simibubi.create.AllItems
 *  com.simibubi.create.api.data.recipe.BaseRecipeProvider$GeneratedRecipe
 *  com.simibubi.create.api.data.recipe.SequencedAssemblyRecipeGen
 *  com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe
 *  com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe$Builder
 *  com.simibubi.create.content.kinetics.press.PressingRecipe
 *  com.simibubi.create.content.kinetics.saw.CuttingRecipe
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 */
package dev.simulated_team.simulated.data.neoforge;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.api.data.recipe.SequencedAssemblyRecipeGen;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import dev.simulated_team.simulated.index.SimItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class SimSequencedAssemblyRecipes
extends SequencedAssemblyRecipeGen {
    private final BaseRecipeProvider.GeneratedRecipe GYRO_MECHANISM = this.create("gyroscopic_mechanism", b -> b.require((ItemLike)AllItems.IRON_SHEET).transitionTo(SimItems.INCOMPLETE_GYRO_MECHANISM).addStep(DeployerApplicationRecipe::new, rb -> (ItemApplicationRecipe.Builder)rb.require((ItemLike)AllBlocks.COGWHEEL.get())).addStep(DeployerApplicationRecipe::new, rb -> (ItemApplicationRecipe.Builder)rb.require((ItemLike)AllBlocks.SHAFT.get())).addStep(DeployerApplicationRecipe::new, rb -> (ItemApplicationRecipe.Builder)rb.require((ItemLike)AllItems.BRASS_NUGGET)).loops(5).addOutput(SimItems.GYRO_MECHANISM, 200.0f).addOutput((ItemLike)AllItems.IRON_SHEET, 8.0f).addOutput((ItemLike)AllItems.ANDESITE_ALLOY, 8.0f).addOutput((ItemLike)AllItems.BRASS_NUGGET, 3.0f).addOutput((ItemLike)AllItems.CRUSHED_IRON, 2.0f).addOutput((ItemLike)Items.COMPASS.asItem(), 1.0f));
    private final BaseRecipeProvider.GeneratedRecipe ENGINE_ASSEMBLY = this.create("engine_assembly", b -> b.require((ItemLike)AllItems.IRON_SHEET).transitionTo(SimItems.INCOMPLETE_ENGINE_ASSEMBLY).addStep(CuttingRecipe::new, rb -> rb).addStep(PressingRecipe::new, rb -> rb).loops(8).addOutput(SimItems.ENGINE_ASSEMBLY, 50.0f).addOutput((ItemLike)AllItems.IRON_SHEET, 16.0f).addOutput((ItemLike)Items.IRON_NUGGET, 15.0f).addOutput((ItemLike)AllBlocks.INDUSTRIAL_IRON_BLOCK, 10.0f).addOutput((ItemLike)Blocks.IRON_BARS, 8.0f).addOutput((ItemLike)Items.IRON_HELMET, 1.0f));

    public SimSequencedAssemblyRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, "simulated");
    }

    public String getName() {
        return "Simulated's Splendid Sequenced Assembly Recipes";
    }
}
