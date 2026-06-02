/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.world.item.AxeItem
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.component.ItemAttributeModifiers
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.foundation.data.recipe;

import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ManualApplicationRecipe;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;

public class LogStrippingFakeRecipes {
    public static List<RecipeHolder<ManualApplicationRecipe>> createRecipes() {
        ArrayList<RecipeHolder<ManualApplicationRecipe>> recipes = new ArrayList<RecipeHolder<ManualApplicationRecipe>>();
        if (!((Boolean)AllConfigs.server().recipes.displayLogStrippingRecipes.get()).booleanValue()) {
            return recipes;
        }
        ItemStack axe = new ItemStack((ItemLike)Items.IRON_AXE);
        axe.set(DataComponents.ATTRIBUTE_MODIFIERS, (Object)ItemAttributeModifiers.EMPTY);
        axe.set(DataComponents.CUSTOM_NAME, (Object)CreateLang.translateDirect("recipe.item_application.any_axe", new Object[0]).withStyle(style -> style.withItalic(Boolean.valueOf(false))));
        BuiltInRegistries.ITEM.getTagOrEmpty(ItemTags.LOGS).forEach(stack -> LogStrippingFakeRecipes.process((Item)stack.value(), recipes, axe));
        return recipes;
    }

    private static void process(Item item, List<RecipeHolder<ManualApplicationRecipe>> list, ItemStack axe) {
        if (!(item instanceof BlockItem)) {
            return;
        }
        BlockItem blockItem = (BlockItem)item;
        BlockState state = blockItem.getBlock().defaultBlockState();
        BlockState strippedState = AxeItem.getAxeStrippingState((BlockState)state);
        if (strippedState == null) {
            return;
        }
        Item resultItem = strippedState.getBlock().asItem();
        if (resultItem == null) {
            return;
        }
        list.add(LogStrippingFakeRecipes.create(item, resultItem, axe));
    }

    private static RecipeHolder<ManualApplicationRecipe> create(Item fromItem, Item toItem, ItemStack axe) {
        ResourceLocation rn = RegisteredObjectsHelper.getKeyOrThrow((Item)toItem);
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath((String)rn.getNamespace(), (String)(rn.getPath() + "_via_vanilla_stripping"));
        ManualApplicationRecipe recipe = (ManualApplicationRecipe)((ItemApplicationRecipe.Builder)((ItemApplicationRecipe.Builder)((ItemApplicationRecipe.Builder)new ItemApplicationRecipe.Builder<ManualApplicationRecipe>(ManualApplicationRecipe::new, id).require((ItemLike)fromItem)).require(Ingredient.of((ItemStack[])new ItemStack[]{axe}))).output((ItemLike)toItem)).build();
        return new RecipeHolder(id, (Recipe)recipe);
    }
}
