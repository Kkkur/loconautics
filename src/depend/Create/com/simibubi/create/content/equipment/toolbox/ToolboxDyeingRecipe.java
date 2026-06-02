/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.CraftingBookCategory
 *  net.minecraft.world.item.crafting.CraftingInput
 *  net.minecraft.world.item.crafting.CustomRecipe
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.neoforged.neoforge.common.Tags$Items
 */
package com.simibubi.create.content.equipment.toolbox;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlock;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

public class ToolboxDyeingRecipe
extends CustomRecipe {
    public ToolboxDyeingRecipe(CraftingBookCategory category) {
        super(category);
    }

    public boolean matches(CraftingInput input, Level level) {
        int toolboxes = 0;
        int dyes = 0;
        for (int i = 0; i < input.size(); ++i) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            if (Block.byItem((Item)stack.getItem()) instanceof ToolboxBlock) {
                ++toolboxes;
            } else {
                if (!stack.is(Tags.Items.DYES)) {
                    return false;
                }
                ++dyes;
            }
            if (dyes <= 1 && toolboxes <= 1) continue;
            return false;
        }
        return toolboxes == 1 && dyes == 1;
    }

    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack toolbox = ItemStack.EMPTY;
        DyeColor color = DyeColor.BROWN;
        for (int i = 0; i < input.size(); ++i) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            if (Block.byItem((Item)stack.getItem()) instanceof ToolboxBlock) {
                toolbox = stack;
                continue;
            }
            DyeColor color1 = DyeColor.getColor((ItemStack)stack);
            if (color1 == null) continue;
            color = color1;
        }
        ItemStack dyedToolbox = AllBlocks.TOOLBOXES.get(color).asStack();
        if (!toolbox.isComponentsPatchEmpty()) {
            dyedToolbox.applyComponents(toolbox.getComponentsPatch());
        }
        return dyedToolbox;
    }

    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return AllRecipeTypes.TOOLBOX_DYEING.getSerializer();
    }
}
