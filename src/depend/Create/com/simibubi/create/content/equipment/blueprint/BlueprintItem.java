/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.NonNullList
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Ingredient$ItemValue
 *  net.minecraft.world.item.crafting.Ingredient$TagValue
 *  net.minecraft.world.item.crafting.Ingredient$Value
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.ShapedRecipe
 *  net.minecraft.world.level.Level
 *  net.neoforged.neoforge.common.crafting.CompoundIngredient
 *  net.neoforged.neoforge.items.ItemStackHandler
 */
package com.simibubi.create.content.equipment.blueprint;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.blueprint.BlueprintEntity;
import com.simibubi.create.content.logistics.filter.AttributeFilterWhitelistMode;
import com.simibubi.create.content.logistics.filter.ListFilterItem;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.attributes.InTagAttribute;
import com.simibubi.create.foundation.item.ItemHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.items.ItemStackHandler;

public class BlueprintItem
extends Item {
    public BlueprintItem(Item.Properties properties) {
        super(properties);
    }

    public InteractionResult useOn(UseOnContext ctx) {
        Direction face = ctx.getClickedFace();
        Player player = ctx.getPlayer();
        ItemStack stack = ctx.getItemInHand();
        BlockPos pos = ctx.getClickedPos().relative(face);
        if (player != null && !player.mayUseItemAt(pos, face, stack)) {
            return InteractionResult.FAIL;
        }
        Level world = ctx.getLevel();
        BlueprintEntity hangingentity = new BlueprintEntity(world, pos, face, face.getAxis().isHorizontal() ? Direction.DOWN : ctx.getHorizontalDirection());
        CustomData customData = (CustomData)stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            EntityType.updateCustomEntityTag((Level)world, (Player)player, (Entity)hangingentity, (CustomData)customData);
        }
        if (!hangingentity.survives()) {
            return InteractionResult.CONSUME;
        }
        if (!world.isClientSide) {
            hangingentity.playPlacementSound();
            world.addFreshEntity((Entity)hangingentity);
        }
        stack.shrink(1);
        return InteractionResult.sidedSuccess((boolean)world.isClientSide);
    }

    public static void assignCompleteRecipe(Level level, ItemStackHandler inv, Recipe<?> recipe) {
        NonNullList ingredients = recipe.getIngredients();
        for (int i = 0; i < 9; ++i) {
            inv.setStackInSlot(i, ItemStack.EMPTY);
        }
        inv.setStackInSlot(9, recipe.getResultItem((HolderLookup.Provider)level.registryAccess()));
        if (recipe instanceof ShapedRecipe) {
            ShapedRecipe shapedRecipe = (ShapedRecipe)recipe;
            for (int row = 0; row < shapedRecipe.getHeight(); ++row) {
                for (int col = 0; col < shapedRecipe.getWidth(); ++col) {
                    inv.setStackInSlot(row * 3 + col, BlueprintItem.convertIngredientToFilter((Ingredient)ingredients.get(row * shapedRecipe.getWidth() + col)));
                }
            }
        } else {
            for (int i = 0; i < ingredients.size(); ++i) {
                inv.setStackInSlot(i, BlueprintItem.convertIngredientToFilter((Ingredient)ingredients.get(i)));
            }
        }
    }

    private static ItemStack convertIngredientToFilter(Ingredient ingredient) {
        boolean isCompoundIngredient = ingredient.getCustomIngredient() instanceof CompoundIngredient;
        Ingredient.Value[] acceptedItems = ingredient.values;
        if (acceptedItems == null || acceptedItems.length > 18) {
            return ItemStack.EMPTY;
        }
        if (acceptedItems.length == 0) {
            return ItemStack.EMPTY;
        }
        if (acceptedItems.length == 1) {
            return BlueprintItem.convertIItemListToFilter(acceptedItems[0], isCompoundIngredient);
        }
        ItemStack result = AllItems.FILTER.asStack();
        ItemStackHandler filterItems = ((ListFilterItem)AllItems.FILTER.get()).getFilterItemHandler(result);
        for (int i = 0; i < acceptedItems.length; ++i) {
            filterItems.setStackInSlot(i, BlueprintItem.convertIItemListToFilter(acceptedItems[i], isCompoundIngredient));
        }
        result.set(AllDataComponents.FILTER_ITEMS, (Object)ItemHelper.containerContentsFromHandler(filterItems));
        return result;
    }

    private static ItemStack convertIItemListToFilter(Ingredient.Value itemList, boolean isCompoundIngredient) {
        Iterator iterator;
        Collection stacks = itemList.getItems();
        if (itemList instanceof Ingredient.ItemValue && (iterator = stacks.iterator()).hasNext()) {
            ItemStack itemStack = (ItemStack)iterator.next();
            return itemStack;
        }
        if (itemList instanceof Ingredient.TagValue) {
            Ingredient.TagValue tagValue = (Ingredient.TagValue)itemList;
            ItemStack filterItem = AllItems.ATTRIBUTE_FILTER.asStack();
            filterItem.set(AllDataComponents.ATTRIBUTE_FILTER_WHITELIST_MODE, (Object)AttributeFilterWhitelistMode.WHITELIST_DISJ);
            ArrayList<ItemAttribute.ItemAttributeEntry> attributes = new ArrayList<ItemAttribute.ItemAttributeEntry>();
            InTagAttribute at = new InTagAttribute((TagKey<Item>)ItemTags.create((ResourceLocation)tagValue.tag().location()));
            attributes.add(new ItemAttribute.ItemAttributeEntry(at, false));
            filterItem.set(AllDataComponents.ATTRIBUTE_FILTER_MATCHED_ATTRIBUTES, attributes);
            return filterItem;
        }
        if (isCompoundIngredient) {
            ItemStack result = AllItems.FILTER.asStack();
            ItemStackHandler filterItems = ((ListFilterItem)AllItems.FILTER.get()).getFilterItemHandler(result);
            int i = 0;
            for (ItemStack itemStack : stacks) {
                if (i >= 18) break;
                filterItems.setStackInSlot(i++, itemStack);
            }
            result.set(AllDataComponents.FILTER_ITEMS, (Object)ItemHelper.containerContentsFromHandler(filterItems));
            result.set(AllDataComponents.FILTER_ITEMS_RESPECT_NBT, (Object)true);
            return result;
        }
        return ItemStack.EMPTY;
    }
}
