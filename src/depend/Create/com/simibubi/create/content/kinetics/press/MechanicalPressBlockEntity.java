/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.NonNullList
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.SingleRecipeInput
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.press;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.advancement.CreateAdvancement;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.List;
import java.util.Optional;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MechanicalPressBlockEntity
extends BasinOperatingBlockEntity
implements PressingBehaviour.PressingBehaviourSpecifics {
    private static final Object compressingRecipesKey = new Object();
    public PressingBehaviour pressingBehaviour;
    private int tracksCreated;

    public MechanicalPressBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(this.worldPosition).expandTowards(0.0, -1.5, 0.0).expandTowards(0.0, 1.0, 0.0);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.pressingBehaviour = new PressingBehaviour(this);
        behaviours.add(this.pressingBehaviour);
        this.registerAwardables(behaviours, AllAdvancements.PRESS, AllAdvancements.COMPACTING, AllAdvancements.TRACK_CRAFTING);
    }

    public void onItemPressed(ItemStack result) {
        this.award(AllAdvancements.PRESS);
        if (AllTags.AllBlockTags.TRACKS.matches(result)) {
            this.tracksCreated += result.getCount();
        }
        if (this.tracksCreated >= 1000) {
            this.award(AllAdvancements.TRACK_CRAFTING);
            this.tracksCreated = 0;
        }
    }

    public PressingBehaviour getPressingBehaviour() {
        return this.pressingBehaviour;
    }

    @Override
    public boolean tryProcessInBasin(boolean simulate) {
        this.applyBasinRecipe();
        Optional<BasinBlockEntity> basin = this.getBasin();
        if (basin.isPresent()) {
            SmartInventory inputs = basin.get().getInputInventory();
            for (int slot = 0; slot < inputs.getSlots(); ++slot) {
                ItemStack stackInSlot = inputs.getItem(slot);
                if (stackInSlot.isEmpty()) continue;
                this.pressingBehaviour.particleItems.add(stackInSlot);
            }
        }
        return true;
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        if (this.getBehaviour(AdvancementBehaviour.TYPE).isOwnerPresent()) {
            compound.putInt("TracksCreated", this.tracksCreated);
        }
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.tracksCreated = compound.getInt("TracksCreated");
    }

    @Override
    public boolean tryProcessInWorld(ItemEntity itemEntity, boolean simulate) {
        ItemStack item = itemEntity.getItem();
        Optional<RecipeHolder<PressingRecipe>> recipe = this.getRecipe(item);
        if (!recipe.isPresent()) {
            return false;
        }
        if (simulate) {
            return true;
        }
        ItemStack itemCreated = ItemStack.EMPTY;
        this.pressingBehaviour.particleItems.add(item);
        if (this.canProcessInBulk() || item.getCount() == 1) {
            RecipeApplier.applyRecipeOn(itemEntity, recipe.get().value(), true);
            itemCreated = itemEntity.getItem().copy();
        } else {
            for (ItemStack result : RecipeApplier.applyRecipeOn(this.level, item.copyWithCount(1), recipe.get().value(), true)) {
                if (itemCreated.isEmpty()) {
                    itemCreated = result.copy();
                }
                ItemEntity created = new ItemEntity(this.level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), result);
                created.setDefaultPickUpDelay();
                created.setDeltaMovement(VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)this.level.random, (float)0.05f));
                this.level.addFreshEntity((Entity)created);
            }
            item.shrink(1);
        }
        if (!itemCreated.isEmpty()) {
            this.onItemPressed(itemCreated);
        }
        return true;
    }

    @Override
    public boolean tryProcessOnBelt(TransportedItemStack input, List<ItemStack> outputList, boolean simulate) {
        Optional<RecipeHolder<PressingRecipe>> recipe = this.getRecipe(input.stack);
        if (!recipe.isPresent()) {
            return false;
        }
        if (simulate) {
            return true;
        }
        this.pressingBehaviour.particleItems.add(input.stack);
        List<ItemStack> outputs = RecipeApplier.applyRecipeOn(this.level, this.canProcessInBulk() ? input.stack : input.stack.copyWithCount(1), recipe.get().value(), true);
        for (ItemStack created : outputs) {
            if (created.isEmpty()) continue;
            this.onItemPressed(created);
            break;
        }
        outputList.addAll(outputs);
        return true;
    }

    @Override
    public void onPressingCompleted() {
        if (this.pressingBehaviour.onBasin() && this.matchBasinRecipe(this.currentRecipe) && this.getBasin().filter(BasinBlockEntity::canContinueProcessing).isPresent()) {
            this.startProcessingBasin();
        } else {
            this.basinChecker.scheduleUpdate();
        }
    }

    public Optional<RecipeHolder<PressingRecipe>> getRecipe(ItemStack item) {
        Optional<RecipeHolder<PressingRecipe>> assemblyRecipe = SequencedAssemblyRecipe.getRecipe(this.level, item, AllRecipeTypes.PRESSING.getType(), PressingRecipe.class);
        if (assemblyRecipe.isPresent()) {
            return assemblyRecipe;
        }
        return AllRecipeTypes.PRESSING.find(new SingleRecipeInput(item), this.level);
    }

    public static boolean canCompress(Recipe<?> recipe) {
        if (!(recipe instanceof CraftingRecipe) || !((Boolean)AllConfigs.server().recipes.allowShapedSquareInPress.get()).booleanValue()) {
            return false;
        }
        NonNullList ingredients = recipe.getIngredients();
        return (ingredients.size() == 4 || ingredients.size() == 9) && ItemHelper.matchAllIngredients((NonNullList<Ingredient>)ingredients);
    }

    @Override
    protected boolean matchStaticFilters(RecipeHolder<? extends Recipe<?>> recipe) {
        return recipe.value() instanceof CraftingRecipe && !(recipe.value() instanceof MechanicalCraftingRecipe) && MechanicalPressBlockEntity.canCompress(recipe.value()) && !AllRecipeTypes.shouldIgnoreInAutomation(recipe) || recipe.value().getType() == AllRecipeTypes.COMPACTING.getType();
    }

    @Override
    public float getKineticSpeed() {
        return this.getSpeed();
    }

    @Override
    public boolean canProcessInBulk() {
        return (Boolean)AllConfigs.server().recipes.bulkPressing.get();
    }

    @Override
    protected Object getRecipeCacheKey() {
        return compressingRecipesKey;
    }

    @Override
    public int getParticleAmount() {
        return 15;
    }

    @Override
    public void startProcessingBasin() {
        if (this.pressingBehaviour.running && this.pressingBehaviour.runningTicks <= 120) {
            return;
        }
        super.startProcessingBasin();
        this.pressingBehaviour.start(PressingBehaviour.Mode.BASIN);
    }

    @Override
    protected void onBasinRemoved() {
        this.pressingBehaviour.particleItems.clear();
        this.pressingBehaviour.running = false;
        this.pressingBehaviour.runningTicks = 0;
        this.sendData();
    }

    @Override
    protected boolean isRunning() {
        return this.pressingBehaviour.running;
    }

    @Override
    protected Optional<CreateAdvancement> getProcessedRecipeTrigger() {
        return Optional.of(AllAdvancements.COMPACTING);
    }
}
