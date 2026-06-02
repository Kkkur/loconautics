/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  net.neoforged.neoforge.items.IItemHandler
 */
package com.simibubi.create.content.processing.basin;

import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.foundation.advancement.CreateAdvancement;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.simple.DeferralBehaviour;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.recipe.trie.AbstractVariant;
import com.simibubi.create.foundation.recipe.trie.RecipeTrie;
import com.simibubi.create.foundation.recipe.trie.RecipeTrieFinder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

public abstract class BasinOperatingBlockEntity
extends KineticBlockEntity {
    public DeferralBehaviour basinChecker;
    public boolean basinRemoved;
    protected Recipe<?> currentRecipe;

    public BasinOperatingBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.basinChecker = new DeferralBehaviour(this, this::updateBasin);
        behaviours.add(this.basinChecker);
    }

    @Override
    public void onSpeedChanged(float prevSpeed) {
        super.onSpeedChanged(prevSpeed);
        if (this.getSpeed() == 0.0f) {
            this.basinRemoved = true;
        }
        this.basinRemoved = false;
        this.basinChecker.scheduleUpdate();
    }

    @Override
    public void tick() {
        if (this.basinRemoved) {
            this.basinRemoved = false;
            this.onBasinRemoved();
            this.sendData();
            return;
        }
        super.tick();
    }

    protected boolean updateBasin() {
        if (!this.isSpeedRequirementFulfilled()) {
            return true;
        }
        if (this.getSpeed() == 0.0f) {
            return true;
        }
        if (this.isRunning()) {
            return true;
        }
        if (this.level == null || this.level.isClientSide) {
            return true;
        }
        Optional<BasinBlockEntity> basin = this.getBasin();
        if (!basin.filter(BasinBlockEntity::canContinueProcessing).isPresent()) {
            return true;
        }
        List<Recipe<?>> recipes = this.getMatchingRecipes();
        if (recipes.isEmpty()) {
            return true;
        }
        this.currentRecipe = recipes.get(0);
        this.startProcessingBasin();
        this.sendData();
        return true;
    }

    protected abstract boolean isRunning();

    public void startProcessingBasin() {
    }

    public boolean continueWithPreviousRecipe() {
        return true;
    }

    protected <I extends RecipeInput> boolean matchBasinRecipe(Recipe<I> recipe) {
        if (recipe == null) {
            return false;
        }
        Optional<BasinBlockEntity> basin = this.getBasin();
        if (!basin.isPresent()) {
            return false;
        }
        return BasinRecipe.match(basin.get(), recipe);
    }

    protected void applyBasinRecipe() {
        if (this.currentRecipe == null) {
            return;
        }
        Optional<BasinBlockEntity> optionalBasin = this.getBasin();
        if (!optionalBasin.isPresent()) {
            return;
        }
        BasinBlockEntity basin = optionalBasin.get();
        boolean wasEmpty = basin.canContinueProcessing();
        if (!BasinRecipe.apply(basin, this.currentRecipe)) {
            return;
        }
        this.getProcessedRecipeTrigger().ifPresent(this::award);
        basin.inputTank.sendDataImmediately();
        if (wasEmpty && this.matchBasinRecipe(this.currentRecipe)) {
            this.continueWithPreviousRecipe();
            this.sendData();
        }
        basin.notifyChangeOfContents();
    }

    protected List<Recipe<?>> getMatchingRecipes() {
        BasinBlockEntity basin;
        Optional<BasinBlockEntity> $basin = this.getBasin();
        if ($basin.isEmpty() || (basin = $basin.get()).isEmpty()) {
            return new ArrayList();
        }
        ArrayList list = new ArrayList();
        try {
            IItemHandler availableItems = (IItemHandler)this.level.getCapability(Capabilities.ItemHandler.BLOCK, basin.getBlockPos(), null);
            IFluidHandler availableFluids = (IFluidHandler)this.level.getCapability(Capabilities.FluidHandler.BLOCK, basin.getBlockPos(), null);
            if (availableItems == null && availableFluids == null) {
                return list;
            }
            RecipeTrie<?> recipeTrie = RecipeTrieFinder.get(this.getRecipeCacheKey(), this.level, this::matchStaticFilters);
            Set<AbstractVariant> availableVariants = RecipeTrie.getVariants(availableItems, availableFluids);
            for (Recipe r : recipeTrie.lookup(availableVariants)) {
                if (!this.matchBasinRecipe(r)) continue;
                list.add(r);
            }
        }
        catch (Exception e) {
            Create.LOGGER.error("Failed to get recipe trie, falling back to slow logic", (Throwable)e);
            list.clear();
            for (RecipeHolder<? extends Recipe<?>> recipeHolder : RecipeFinder.get(this.getRecipeCacheKey(), this.level, this::matchStaticFilters)) {
                if (!this.matchBasinRecipe(recipeHolder.value())) continue;
                list.add(recipeHolder.value());
            }
        }
        list.sort((r1, r2) -> r2.getIngredients().size() - r1.getIngredients().size());
        return list;
    }

    protected abstract void onBasinRemoved();

    protected Optional<BasinBlockEntity> getBasin() {
        if (this.level == null) {
            return Optional.empty();
        }
        BlockEntity basinBE = this.level.getBlockEntity(this.worldPosition.below(2));
        if (!(basinBE instanceof BasinBlockEntity)) {
            return Optional.empty();
        }
        return Optional.of((BasinBlockEntity)basinBE);
    }

    protected Optional<CreateAdvancement> getProcessedRecipeTrigger() {
        return Optional.empty();
    }

    protected abstract boolean matchStaticFilters(RecipeHolder<? extends Recipe<?>> var1);

    protected abstract Object getRecipeCacheKey();
}
