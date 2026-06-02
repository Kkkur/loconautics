/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicates
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.Pointing
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.CraftingInput
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.FireworkRocketRecipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.simibubi.create.content.kinetics.crafter;

import com.google.common.base.Predicates;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.crafter.CrafterHelper;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlock;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlockEntity;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingInput;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.Pointing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.FireworkRocketRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;

public class RecipeGridHandler {
    public static List<MechanicalCrafterBlockEntity> getAllCraftersOfChain(MechanicalCrafterBlockEntity root) {
        return RecipeGridHandler.getAllCraftersOfChainIf(root, (Predicate<MechanicalCrafterBlockEntity>)Predicates.alwaysTrue());
    }

    public static List<MechanicalCrafterBlockEntity> getAllCraftersOfChainIf(MechanicalCrafterBlockEntity root, Predicate<MechanicalCrafterBlockEntity> test) {
        return RecipeGridHandler.getAllCraftersOfChainIf(root, test, false);
    }

    public static List<MechanicalCrafterBlockEntity> getAllCraftersOfChainIf(MechanicalCrafterBlockEntity root, Predicate<MechanicalCrafterBlockEntity> test, boolean poweredStart) {
        ArrayList<MechanicalCrafterBlockEntity> crafters = new ArrayList<MechanicalCrafterBlockEntity>();
        ArrayList<Pair> frontier = new ArrayList<Pair>();
        HashSet<MechanicalCrafterBlockEntity> visited = new HashSet<MechanicalCrafterBlockEntity>();
        frontier.add(Pair.of((Object)root, null));
        boolean empty = false;
        boolean allEmpty = true;
        while (!frontier.isEmpty()) {
            Pair pair = (Pair)frontier.remove(0);
            MechanicalCrafterBlockEntity current = (MechanicalCrafterBlockEntity)pair.getKey();
            MechanicalCrafterBlockEntity last = (MechanicalCrafterBlockEntity)pair.getValue();
            if (visited.contains(current)) {
                return null;
            }
            if (!test.test(current)) {
                empty = true;
            } else {
                allEmpty = false;
            }
            crafters.add(current);
            visited.add(current);
            MechanicalCrafterBlockEntity target = RecipeGridHandler.getTargetingCrafter(current);
            if (target != last && target != null) {
                frontier.add(Pair.of((Object)target, (Object)current));
            }
            for (MechanicalCrafterBlockEntity preceding : RecipeGridHandler.getPrecedingCrafters(current)) {
                if (preceding == last) continue;
                frontier.add(Pair.of((Object)preceding, (Object)current));
            }
        }
        return empty && !poweredStart || allEmpty ? null : crafters;
    }

    public static MechanicalCrafterBlockEntity getTargetingCrafter(MechanicalCrafterBlockEntity crafter) {
        BlockState state = crafter.getBlockState();
        if (!RecipeGridHandler.isCrafter(state)) {
            return null;
        }
        BlockPos targetPos = crafter.getBlockPos().relative(MechanicalCrafterBlock.getTargetDirection(state));
        MechanicalCrafterBlockEntity targetBE = CrafterHelper.getCrafter((BlockAndTintGetter)crafter.getLevel(), targetPos);
        if (targetBE == null) {
            return null;
        }
        BlockState targetState = targetBE.getBlockState();
        if (!RecipeGridHandler.isCrafter(targetState)) {
            return null;
        }
        if (state.getValue(HorizontalKineticBlock.HORIZONTAL_FACING) != targetState.getValue(HorizontalKineticBlock.HORIZONTAL_FACING)) {
            return null;
        }
        return targetBE;
    }

    public static List<MechanicalCrafterBlockEntity> getPrecedingCrafters(MechanicalCrafterBlockEntity crafter) {
        BlockPos pos = crafter.getBlockPos();
        Level world = crafter.getLevel();
        ArrayList<MechanicalCrafterBlockEntity> crafters = new ArrayList<MechanicalCrafterBlockEntity>();
        BlockState blockState = crafter.getBlockState();
        if (!RecipeGridHandler.isCrafter(blockState)) {
            return crafters;
        }
        Direction blockFacing = (Direction)blockState.getValue(HorizontalKineticBlock.HORIZONTAL_FACING);
        Direction blockPointing = MechanicalCrafterBlock.getTargetDirection(blockState);
        for (Direction facing : Iterate.directions) {
            MechanicalCrafterBlockEntity be;
            BlockPos neighbourPos;
            BlockState neighbourState;
            if (blockFacing.getAxis() == facing.getAxis() || blockPointing == facing || !RecipeGridHandler.isCrafter(neighbourState = world.getBlockState(neighbourPos = pos.relative(facing))) || MechanicalCrafterBlock.getTargetDirection(neighbourState) != facing.getOpposite() || blockFacing != neighbourState.getValue(HorizontalKineticBlock.HORIZONTAL_FACING) || (be = CrafterHelper.getCrafter((BlockAndTintGetter)world, neighbourPos)) == null) continue;
            crafters.add(be);
        }
        return crafters;
    }

    private static boolean isCrafter(BlockState state) {
        return AllBlocks.MECHANICAL_CRAFTER.has(state);
    }

    public static ItemStack tryToApplyRecipe(Level world, GroupedItems items) {
        items.calcStats();
        MechanicalCraftingInput craftingInput = MechanicalCraftingInput.of(items);
        ItemStack result = null;
        RegistryAccess registryAccess = world.registryAccess();
        if (((Boolean)AllConfigs.server().recipes.allowRegularCraftingInCrafter.get()).booleanValue()) {
            result = world.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, (RecipeInput)craftingInput, world).filter(r -> RecipeGridHandler.isRecipeAllowed((RecipeHolder<CraftingRecipe>)r, craftingInput)).map(r -> ((CraftingRecipe)r.value()).assemble((RecipeInput)craftingInput, (HolderLookup.Provider)registryAccess)).orElse(null);
        }
        if (result == null) {
            result = AllRecipeTypes.MECHANICAL_CRAFTING.find(craftingInput, world).map(r -> r.value().assemble((RecipeInput)craftingInput, (HolderLookup.Provider)registryAccess)).orElse(null);
        }
        return result;
    }

    public static boolean isRecipeAllowed(RecipeHolder<CraftingRecipe> recipe, CraftingInput craftingInput) {
        if (recipe.value() instanceof FireworkRocketRecipe) {
            int numItems = 0;
            for (int i = 0; i < craftingInput.size(); ++i) {
                if (craftingInput.getItem(i).isEmpty()) continue;
                ++numItems;
            }
            if (numItems > (Integer)AllConfigs.server().recipes.maxFireworkIngredientsInCrafter.get()) {
                return false;
            }
        }
        return !AllRecipeTypes.shouldIgnoreInAutomation(recipe);
    }

    public static class GroupedItems {
        Map<Pair<Integer, Integer>, ItemStack> grid = new HashMap<Pair<Integer, Integer>, ItemStack>();
        int minX;
        int minY;
        int maxX;
        int maxY;
        int width;
        int height;
        boolean statsReady;

        public GroupedItems() {
        }

        public GroupedItems(ItemStack stack) {
            this.grid.put((Pair<Integer, Integer>)Pair.of((Object)0, (Object)0), stack);
        }

        public void mergeOnto(GroupedItems other, Pointing pointing) {
            int xOffset;
            int n = pointing == Pointing.LEFT ? 1 : (xOffset = pointing == Pointing.RIGHT ? -1 : 0);
            int yOffset = pointing == Pointing.DOWN ? 1 : (pointing == Pointing.UP ? -1 : 0);
            this.grid.forEach((pair, stack) -> other.grid.put((Pair<Integer, Integer>)Pair.of((Object)((Integer)pair.getKey() + xOffset), (Object)((Integer)pair.getValue() + yOffset)), (ItemStack)stack));
            other.statsReady = false;
        }

        public void write(CompoundTag nbt, HolderLookup.Provider registries) {
            ListTag gridNBT = new ListTag();
            this.grid.forEach((pair, stack) -> {
                CompoundTag entry = new CompoundTag();
                entry.putInt("x", ((Integer)pair.getKey()).intValue());
                entry.putInt("y", ((Integer)pair.getValue()).intValue());
                entry.put("item", stack.saveOptional(registries));
                gridNBT.add((Object)entry);
            });
            nbt.put("Grid", (Tag)gridNBT);
        }

        public static GroupedItems read(CompoundTag nbt, HolderLookup.Provider registries) {
            GroupedItems items = new GroupedItems();
            ListTag gridNBT = nbt.getList("Grid", 10);
            gridNBT.forEach(inbt -> {
                CompoundTag entry = (CompoundTag)inbt;
                int x = entry.getInt("x");
                int y = entry.getInt("y");
                ItemStack stack = ItemStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)entry.getCompound("item"));
                items.grid.put((Pair<Integer, Integer>)Pair.of((Object)x, (Object)y), stack);
            });
            return items;
        }

        public void calcStats() {
            if (this.statsReady) {
                return;
            }
            this.statsReady = true;
            this.minX = 0;
            this.minY = 0;
            this.maxX = 0;
            this.maxY = 0;
            for (Pair<Integer, Integer> pair : this.grid.keySet()) {
                int x = (Integer)pair.getKey();
                int y = (Integer)pair.getValue();
                this.minX = Math.min(this.minX, x);
                this.minY = Math.min(this.minY, y);
                this.maxX = Math.max(this.maxX, x);
                this.maxY = Math.max(this.maxY, y);
            }
            this.width = this.maxX - this.minX + 1;
            this.height = this.maxY - this.minY + 1;
        }

        public boolean onlyEmptyItems() {
            for (ItemStack stack : this.grid.values()) {
                if (stack.isEmpty()) continue;
                return false;
            }
            return true;
        }
    }
}
