/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.BlockParticleOption
 *  net.minecraft.core.particles.ItemParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.item.crafting.StonecutterRecipe
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.BambooStalkBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.CactusBlock
 *  net.minecraft.world.level.block.ChorusPlantBlock
 *  net.minecraft.world.level.block.KelpBlock
 *  net.minecraft.world.level.block.KelpPlantBlock
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.SugarCaneBlock
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemStackHandler
 */
package com.simibubi.create.content.kinetics.saw;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.kinetics.saw.SawBlock;
import com.simibubi.create.content.kinetics.saw.SawFilterSlot;
import com.simibubi.create.content.kinetics.saw.TreeCutter;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.processing.recipe.ProcessingInventory;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.recipe.RecipeConditions;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.AbstractBlockBreakQueue;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.ChorusPlantBlock;
import net.minecraft.world.level.block.KelpBlock;
import net.minecraft.world.level.block.KelpPlantBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SawBlockEntity
extends BlockBreakingKineticBlockEntity
implements Clearable {
    private static final Object cuttingRecipesKey = new Object();
    public static final Supplier<RecipeType<?>> woodcuttingRecipeType = Suppliers.memoize(() -> (RecipeType)BuiltInRegistries.RECIPE_TYPE.get(ResourceLocation.fromNamespaceAndPath((String)"druidcraft", (String)"woodcutting")));
    public ProcessingInventory inventory;
    private int recipeIndex;
    private FilteringBehaviour filtering;
    private ItemStack playEvent;

    public SawBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.inventory = new ProcessingInventory(this::start).withSlotLimit((Boolean)AllConfigs.server().recipes.bulkCutting.get() == false);
        this.inventory.remainingTime = -1.0f;
        this.recipeIndex = 0;
        this.playEvent = ItemStack.EMPTY;
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.SAW.get(), (be, context) -> {
            if (context != Direction.DOWN) {
                return be.inventory;
            }
            return null;
        });
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.filtering = new FilteringBehaviour(this, new SawFilterSlot()).forRecipes();
        behaviours.add(this.filtering);
        behaviours.add(new DirectBeltInputBehaviour(this).allowingBeltFunnelsWhen(this::canProcess));
        this.registerAwardables(behaviours, AllAdvancements.SAW_PROCESSING);
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.put("Inventory", (Tag)this.inventory.serializeNBT(registries));
        compound.putInt("RecipeIndex", this.recipeIndex);
        super.write(compound, registries, clientPacket);
        if (!clientPacket || this.playEvent.isEmpty()) {
            return;
        }
        compound.put("PlayEvent", this.playEvent.saveOptional(registries));
        this.playEvent = ItemStack.EMPTY;
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.inventory.deserializeNBT(registries, compound.getCompound("Inventory"));
        this.recipeIndex = compound.getInt("RecipeIndex");
        if (compound.contains("PlayEvent")) {
            this.playEvent = ItemStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)compound.getCompound("PlayEvent"));
        }
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(this.getBlockPos()).inflate(0.125);
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void tickAudio() {
        super.tickAudio();
        if (this.getSpeed() == 0.0f) {
            return;
        }
        if (!this.playEvent.isEmpty()) {
            boolean isWood = false;
            Item item = this.playEvent.getItem();
            if (item instanceof BlockItem) {
                Block block = ((BlockItem)item).getBlock();
                isWood = block.getSoundType(block.defaultBlockState(), (LevelReader)this.level, this.worldPosition, null) == SoundType.WOOD;
            }
            this.spawnEventParticles(this.playEvent);
            this.playEvent = ItemStack.EMPTY;
            if (!isWood) {
                AllSoundEvents.SAW_ACTIVATE_STONE.playAt(this.level, (Vec3i)this.worldPosition, 3.0f, 1.0f, true);
            } else {
                AllSoundEvents.SAW_ACTIVATE_WOOD.playAt(this.level, (Vec3i)this.worldPosition, 3.0f, 1.0f, true);
            }
            return;
        }
    }

    @Override
    public void tick() {
        if (this.shouldRun() && this.ticksUntilNextProgress < 0) {
            this.destroyNextTick();
        }
        super.tick();
        if (!this.canProcess()) {
            return;
        }
        if (this.getSpeed() == 0.0f) {
            return;
        }
        if (this.inventory.remainingTime == -1.0f) {
            if (!this.inventory.isEmpty() && !this.inventory.appliedRecipe) {
                this.start(this.inventory.getStackInSlot(0));
            }
            return;
        }
        float processingSpeed = Mth.clamp((float)(Math.abs(this.getSpeed()) / 24.0f), (float)1.0f, (float)128.0f);
        this.inventory.remainingTime -= processingSpeed;
        if (this.inventory.remainingTime > 0.0f) {
            this.spawnParticles(this.inventory.getStackInSlot(0));
        }
        if (this.inventory.remainingTime < 5.0f && !this.inventory.appliedRecipe) {
            if (this.level.isClientSide && !this.isVirtual()) {
                return;
            }
            this.playEvent = this.inventory.getStackInSlot(0);
            this.applyRecipe();
            this.inventory.appliedRecipe = true;
            this.inventory.recipeDuration = 20.0f;
            this.inventory.remainingTime = 20.0f;
            this.sendData();
            return;
        }
        Vec3 itemMovement = this.getItemMovementVec();
        Direction itemMovementFacing = Direction.getNearest((double)itemMovement.x, (double)itemMovement.y, (double)itemMovement.z);
        if (this.inventory.remainingTime > 0.0f) {
            return;
        }
        this.inventory.remainingTime = 0.0f;
        for (int slot = 0; slot < this.inventory.getSlots(); ++slot) {
            ItemStack tryExportingToBeltFunnel;
            ItemStack stack = this.inventory.getStackInSlot(slot);
            if (stack.isEmpty() || (tryExportingToBeltFunnel = this.getBehaviour(DirectBeltInputBehaviour.TYPE).tryExportingToBeltFunnel(stack, itemMovementFacing.getOpposite(), false)) == null) continue;
            if (tryExportingToBeltFunnel.getCount() != stack.getCount()) {
                this.inventory.setStackInSlot(slot, tryExportingToBeltFunnel);
                this.notifyUpdate();
                return;
            }
            if (tryExportingToBeltFunnel.isEmpty()) continue;
            return;
        }
        BlockPos nextPos = this.worldPosition.offset((Vec3i)BlockPos.containing((Position)itemMovement));
        DirectBeltInputBehaviour behaviour = BlockEntityBehaviour.get((BlockGetter)this.level, nextPos, DirectBeltInputBehaviour.TYPE);
        if (behaviour != null) {
            boolean changed = false;
            if (!behaviour.canInsertFromSide(itemMovementFacing)) {
                return;
            }
            if (this.level.isClientSide && !this.isVirtual()) {
                return;
            }
            for (int slot = 0; slot < this.inventory.getSlots(); ++slot) {
                ItemStack remainder;
                ItemStack stack = this.inventory.getStackInSlot(slot);
                if (stack.isEmpty() || ItemStack.matches((ItemStack)(remainder = behaviour.handleInsertion(stack, itemMovementFacing, false)), (ItemStack)stack)) continue;
                this.inventory.setStackInSlot(slot, remainder);
                changed = true;
            }
            if (changed) {
                this.setChanged();
                this.sendData();
            }
            return;
        }
        Vec3 outPos = VecHelper.getCenterOf((Vec3i)this.worldPosition).add(itemMovement.scale(0.5).add(0.0, 0.5, 0.0));
        Vec3 outMotion = itemMovement.scale(0.0625).add(0.0, 0.125, 0.0);
        for (int slot = 0; slot < this.inventory.getSlots(); ++slot) {
            ItemStack stack = this.inventory.getStackInSlot(slot);
            if (stack.isEmpty()) continue;
            ItemEntity entityIn = new ItemEntity(this.level, outPos.x, outPos.y, outPos.z, stack);
            entityIn.setDeltaMovement(outMotion);
            this.level.addFreshEntity((Entity)entityIn);
        }
        this.inventory.clear();
        this.level.updateNeighbourForOutputSignal(this.worldPosition, this.getBlockState().getBlock());
        this.inventory.remainingTime = -1.0f;
        this.sendData();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.invalidateCapabilities();
    }

    public void clearContent() {
        this.inventory.clear();
        this.filtering.setFilter(ItemStack.EMPTY);
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(this.level, this.worldPosition, (IItemHandler)this.inventory);
    }

    protected void spawnEventParticles(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        Object particleData = null;
        particleData = stack.getItem() instanceof BlockItem ? new BlockParticleOption(ParticleTypes.BLOCK, ((BlockItem)stack.getItem()).getBlock().defaultBlockState()) : new ItemParticleOption(ParticleTypes.ITEM, stack);
        RandomSource r = this.level.random;
        Vec3 v = VecHelper.getCenterOf((Vec3i)this.worldPosition).add(0.0, 0.3125, 0.0);
        for (int i = 0; i < 10; ++i) {
            Vec3 m = VecHelper.offsetRandomly((Vec3)new Vec3(0.0, 0.25, 0.0), (RandomSource)r, (float)0.125f);
            this.level.addParticle((ParticleOptions)particleData, v.x, v.y, v.z, m.x, m.y, m.y);
        }
    }

    protected void spawnParticles(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        ItemParticleOption particleData = null;
        float speed = 1.0f;
        if (stack.getItem() instanceof BlockItem) {
            particleData = new BlockParticleOption(ParticleTypes.BLOCK, ((BlockItem)stack.getItem()).getBlock().defaultBlockState());
        } else {
            particleData = new ItemParticleOption(ParticleTypes.ITEM, stack);
            speed = 0.125f;
        }
        RandomSource r = this.level.random;
        Vec3 vec = this.getItemMovementVec();
        Vec3 pos = VecHelper.getCenterOf((Vec3i)this.worldPosition);
        float offset = this.inventory.recipeDuration != 0.0f ? this.inventory.remainingTime / this.inventory.recipeDuration : 0.0f;
        offset /= 2.0f;
        if (this.inventory.appliedRecipe) {
            offset -= 0.5f;
        }
        this.level.addParticle((ParticleOptions)particleData, pos.x() + -vec.x * (double)offset, pos.y() + (double)0.45f, pos.z() + -vec.z * (double)offset, -vec.x * (double)speed, (double)(r.nextFloat() * speed), -vec.z * (double)speed);
    }

    public Vec3 getItemMovementVec() {
        boolean alongX = (Boolean)this.getBlockState().getValue((Property)SawBlock.AXIS_ALONG_FIRST_COORDINATE) == false;
        int offset = this.getSpeed() < 0.0f ? -1 : 1;
        return new Vec3((double)(offset * (alongX ? 1 : 0)), 0.0, (double)(offset * (alongX ? 0 : -1)));
    }

    private void applyRecipe() {
        ItemStack input = this.inventory.getStackInSlot(0);
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        if (PackageItem.isPackage(input)) {
            this.inventory.clear();
            ItemStackHandler results = PackageItem.getContents(input);
            for (int i = 0; i < results.getSlots(); ++i) {
                ItemStack stack = results.getStackInSlot(i);
                if (stack.isEmpty()) continue;
                ItemHelper.addToList(stack, list);
            }
            for (int slot = 0; slot < list.size() && slot + 1 < this.inventory.getSlots(); ++slot) {
                this.inventory.setStackInSlot(slot + 1, (ItemStack)list.get(slot));
            }
            return;
        }
        List<RecipeHolder<Recipe<?>>> recipes = this.getRecipes();
        if (recipes.isEmpty()) {
            return;
        }
        if (this.recipeIndex >= recipes.size()) {
            this.recipeIndex = 0;
        }
        Recipe recipe = recipes.get(this.recipeIndex).value();
        int rolls = input.getCount();
        this.inventory.clear();
        for (int roll = 0; roll < rolls; ++roll) {
            List<Object> results = new LinkedList<ItemStack>();
            if (recipe instanceof CuttingRecipe) {
                results = ((CuttingRecipe)recipe).rollResults(this.level.random);
            } else if (recipe instanceof StonecutterRecipe || recipe.getType() == woodcuttingRecipeType.get()) {
                results.add(recipe.getResultItem((HolderLookup.Provider)this.level.registryAccess()).copy());
            }
            for (ItemStack itemStack : results) {
                ItemHelper.addToList(itemStack, list);
            }
            if (!input.hasCraftingRemainingItem()) continue;
            ItemHelper.addToList(input.getCraftingRemainingItem(), list);
        }
        for (int slot = 0; slot < list.size() && slot + 1 < this.inventory.getSlots(); ++slot) {
            this.inventory.setStackInSlot(slot + 1, (ItemStack)list.get(slot));
        }
        this.award(AllAdvancements.SAW_PROCESSING);
    }

    private List<RecipeHolder<? extends Recipe<?>>> getRecipes() {
        Optional<RecipeHolder<CuttingRecipe>> assemblyRecipe = SequencedAssemblyRecipe.getRecipe(this.level, this.inventory.getStackInSlot(0), AllRecipeTypes.CUTTING.getType(), CuttingRecipe.class);
        if (assemblyRecipe.isPresent() && this.filtering.test(((CuttingRecipe)assemblyRecipe.get().value()).getResultItem((HolderLookup.Provider)this.level.registryAccess()))) {
            return ImmutableList.of(assemblyRecipe.get());
        }
        Predicate<RecipeHolder<? extends Recipe<?>>> types = RecipeConditions.isOfType(new RecipeType[]{AllRecipeTypes.CUTTING.getType(), (Boolean)AllConfigs.server().recipes.allowStonecuttingOnSaw.get() != false ? RecipeType.STONECUTTING : null});
        List<RecipeHolder<Recipe<?>>> startedSearch = RecipeFinder.get(cuttingRecipesKey, this.level, types);
        return startedSearch.stream().filter(RecipeConditions.outputMatchesFilter(this.filtering)).filter(RecipeConditions.firstIngredientMatches(this.inventory.getStackInSlot(0))).filter(r -> !AllRecipeTypes.shouldIgnoreInAutomation(r)).collect(Collectors.toList());
    }

    public void insertItem(ItemEntity entity) {
        if (!this.canProcess()) {
            return;
        }
        if (!this.inventory.isEmpty()) {
            return;
        }
        if (!entity.isAlive()) {
            return;
        }
        if (this.level.isClientSide) {
            return;
        }
        this.inventory.clear();
        ItemStack remainder = this.inventory.insertItem(0, entity.getItem().copy(), false);
        if (remainder.isEmpty()) {
            entity.discard();
        } else {
            entity.setItem(remainder);
        }
    }

    public void start(ItemStack inserted) {
        Recipe recipe;
        if (!this.canProcess()) {
            return;
        }
        if (this.inventory.isEmpty()) {
            return;
        }
        if (this.level.isClientSide && !this.isVirtual()) {
            return;
        }
        List<RecipeHolder<Recipe<?>>> recipes = this.getRecipes();
        boolean valid = !recipes.isEmpty();
        int time = 50;
        if (recipes.isEmpty()) {
            this.inventory.recipeDuration = 10.0f;
            this.inventory.remainingTime = 10.0f;
            this.inventory.appliedRecipe = false;
            this.sendData();
            return;
        }
        if (valid) {
            ++this.recipeIndex;
            if (this.recipeIndex >= recipes.size()) {
                this.recipeIndex = 0;
            }
        }
        if ((recipe = recipes.get(this.recipeIndex).value()) instanceof CuttingRecipe) {
            time = ((CuttingRecipe)recipe).getProcessingDuration();
        }
        this.inventory.recipeDuration = this.inventory.remainingTime = (float)(time * Math.max(1, inserted.getCount() / 5));
        this.inventory.appliedRecipe = false;
        this.sendData();
    }

    protected boolean canProcess() {
        return this.getBlockState().getValue((Property)SawBlock.FACING) == Direction.UP;
    }

    @Override
    protected boolean shouldRun() {
        return ((Direction)this.getBlockState().getValue((Property)SawBlock.FACING)).getAxis().isHorizontal();
    }

    @Override
    protected BlockPos getBreakingPos() {
        return this.getBlockPos().relative((Direction)this.getBlockState().getValue((Property)SawBlock.FACING));
    }

    @Override
    public void onBlockBroken(BlockState stateToBreak) {
        Optional<AbstractBlockBreakQueue> dynamicTree = TreeCutter.findDynamicTree(stateToBreak.getBlock(), this.breakingPos);
        if (dynamicTree.isPresent()) {
            dynamicTree.get().destroyBlocks(this.level, null, this::dropItemFromCutTree);
            return;
        }
        super.onBlockBroken(stateToBreak);
        TreeCutter.findTree((BlockGetter)this.level, this.breakingPos, stateToBreak).destroyBlocks(this.level, null, this::dropItemFromCutTree);
    }

    public void dropItemFromCutTree(BlockPos pos, ItemStack stack) {
        float distance = (float)Math.sqrt(pos.distSqr((Vec3i)this.breakingPos));
        Vec3 dropPos = VecHelper.getCenterOf((Vec3i)pos);
        ItemEntity entity = new ItemEntity(this.level, dropPos.x, dropPos.y, dropPos.z, stack);
        entity.setDeltaMovement(Vec3.atLowerCornerOf((Vec3i)this.breakingPos.subtract((Vec3i)this.worldPosition)).scale((double)(distance / 20.0f)));
        this.level.addFreshEntity((Entity)entity);
    }

    @Override
    public boolean canBreak(BlockState stateToBreak, float blockHardness) {
        boolean sawable = SawBlockEntity.isSawable(stateToBreak);
        return super.canBreak(stateToBreak, blockHardness) && sawable;
    }

    public static boolean isSawable(BlockState stateToBreak) {
        if (stateToBreak.is(BlockTags.SAPLINGS)) {
            return false;
        }
        if (TreeCutter.isLog(stateToBreak) || stateToBreak.is(BlockTags.LEAVES)) {
            return true;
        }
        if (TreeCutter.isRoot(stateToBreak)) {
            return true;
        }
        Block block = stateToBreak.getBlock();
        if (block instanceof BambooStalkBlock) {
            return true;
        }
        if (block.equals(Blocks.PUMPKIN) || block.equals(Blocks.MELON)) {
            return true;
        }
        if (block instanceof CactusBlock) {
            return true;
        }
        if (block instanceof SugarCaneBlock) {
            return true;
        }
        if (block instanceof KelpPlantBlock) {
            return true;
        }
        if (block instanceof KelpBlock) {
            return true;
        }
        if (block instanceof ChorusPlantBlock) {
            return true;
        }
        return TreeCutter.canDynamicTreeCutFrom(block);
    }
}
