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
package dev.simulated_team.simulated.data.neoforge;

import dev.simulated_team.simulated.content.blocks.portable_engine.PortableEngineBlock;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.index.neoforge.SimNeoForgeRecipeTypes;
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

public class PortableEngineDyeingRecipe
extends CustomRecipe {
    public PortableEngineDyeingRecipe(CraftingBookCategory category) {
        super(category);
    }

    public boolean matches(CraftingInput input, Level level) {
        int engines = 0;
        int dyes = 0;
        for (int i = 0; i < input.size(); ++i) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            if (Block.byItem((Item)stack.getItem()) instanceof PortableEngineBlock) {
                ++engines;
            } else {
                if (!stack.is(Tags.Items.DYES)) {
                    return false;
                }
                ++dyes;
            }
            if (dyes <= 1 && engines <= 1) continue;
            return false;
        }
        return engines == 1 && dyes == 1;
    }

    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack engine = ItemStack.EMPTY;
        DyeColor color = DyeColor.RED;
        for (int i = 0; i < input.size(); ++i) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            if (Block.byItem((Item)stack.getItem()) instanceof PortableEngineBlock) {
                engine = stack;
                continue;
            }
            DyeColor color1 = DyeColor.getColor((ItemStack)stack);
            if (color1 == null) continue;
            color = color1;
        }
        ItemStack dyedEngine = SimBlocks.PORTABLE_ENGINES.get(color).asStack();
        if (!engine.isComponentsPatchEmpty()) {
            dyedEngine.applyComponents(engine.getComponentsPatch());
        }
        return dyedEngine;
    }

    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return SimNeoForgeRecipeTypes.PORTABLE_ENGINE_DYEING.getSerializer();
    }
}
