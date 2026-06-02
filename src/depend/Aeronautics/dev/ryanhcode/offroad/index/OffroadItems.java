/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllBlocks
 *  com.simibubi.create.foundation.data.AssetLookup
 *  com.tterrag.registrate.providers.RegistrateRecipeProvider
 *  dev.simulated_team.simulated.registrate.SimulatedRegistrate
 *  net.minecraft.data.recipes.RecipeCategory
 *  net.minecraft.data.recipes.RecipeOutput
 *  net.minecraft.data.recipes.ShapedRecipeBuilder
 *  net.minecraft.data.recipes.ShapelessRecipeBuilder
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 */
package dev.ryanhcode.offroad.index;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.data.AssetLookup;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.ryanhcode.offroad.Offroad;
import dev.ryanhcode.offroad.content.components.TireLike;
import dev.ryanhcode.offroad.content.items.tire.TireItem;
import dev.ryanhcode.offroad.index.OffroadDataComponents;
import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class OffroadItems {
    private static final SimulatedRegistrate REGISTRATE = Offroad.getRegistrate();

    public static void init() {
    }

    static {
        REGISTRATE.item("small_tire", TireItem::new).properties(x -> x.component(OffroadDataComponents.TIRE, (Object)TireLike.SMALL_TIRE)).recipe((c, p) -> ShapelessRecipeBuilder.shapeless((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)c.get()), (int)1).requires((ItemLike)AllBlocks.SHAFT).requires((ItemLike)Items.DRIED_KELP).unlockedBy("has_ingredient", RegistrateRecipeProvider.has((ItemLike)((ItemLike)AllBlocks.SHAFT.get()))).save((RecipeOutput)p)).model(AssetLookup.itemModelWithPartials()).register();
        REGISTRATE.item("tire", TireItem::new).properties(x -> x.component(OffroadDataComponents.TIRE, (Object)TireLike.TIRE)).recipe((c, p) -> ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)c.get()), (int)1).pattern(" K ").pattern("KSK").pattern(" K ").define(Character.valueOf('K'), (ItemLike)Items.DRIED_KELP.asItem()).define(Character.valueOf('S'), (ItemLike)AllBlocks.SHAFT.asItem()).unlockedBy("has_ingredient", RegistrateRecipeProvider.has((ItemLike)((ItemLike)AllBlocks.SHAFT.get()))).save((RecipeOutput)p)).model(AssetLookup.itemModelWithPartials()).register();
        REGISTRATE.item("large_tire", TireItem::new).properties(x -> x.component(OffroadDataComponents.TIRE, (Object)TireLike.LARGE_TIRE)).recipe((c, p) -> ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)c.get()), (int)1).pattern(" B ").pattern("BSB").pattern(" B ").define(Character.valueOf('B'), (ItemLike)AllBlocks.BELT.asItem()).define(Character.valueOf('S'), (ItemLike)AllBlocks.SHAFT.asItem()).unlockedBy("has_ingredient", RegistrateRecipeProvider.has((ItemLike)((ItemLike)AllBlocks.SHAFT.get()))).save((RecipeOutput)p)).model(AssetLookup.itemModelWithPartials()).register();
        REGISTRATE.item("monstrous_tire", TireItem::new).properties(x -> x.component(OffroadDataComponents.TIRE, (Object)TireLike.MONSTROUS_TIRE)).recipe((c, p) -> ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)c.get()), (int)1).pattern(" K ").pattern("KSK").pattern(" K ").define(Character.valueOf('K'), (ItemLike)Blocks.DRIED_KELP_BLOCK.asItem()).define(Character.valueOf('S'), (ItemLike)AllBlocks.SHAFT.asItem()).unlockedBy("has_ingredient", RegistrateRecipeProvider.has((ItemLike)((ItemLike)AllBlocks.SHAFT.get()))).save((RecipeOutput)p)).model(AssetLookup.itemModelWithPartials()).register();
    }
}
