/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.IntAttached
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.lang.LangBuilder
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.StringTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  net.neoforged.neoforge.fluids.capability.templates.FluidTank
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.IItemHandlerModifiable
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 *  net.neoforged.neoforge.items.ItemStackHandler
 *  net.neoforged.neoforge.items.wrapper.CombinedInvWrapper
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.processing.basin;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.FluidFX;
import com.simibubi.create.content.fluids.particle.FluidParticleData;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlock;
import com.simibubi.create.content.processing.basin.BasinInventory;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.IntAttached;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.lang.LangBuilder;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BasinBlockEntity
extends SmartBlockEntity
implements IHaveGoggleInformation,
Clearable {
    private boolean areFluidsMoving;
    LerpedFloat ingredientRotationSpeed;
    LerpedFloat ingredientRotation;
    public BasinInventory inputInventory = new BasinInventory(9, this);
    public SmartFluidTankBehaviour inputTank;
    protected SmartInventory outputInventory;
    protected SmartFluidTankBehaviour outputTank;
    private FilteringBehaviour filtering;
    private boolean contentsChanged;
    private Couple<SmartInventory> invs;
    private Couple<SmartFluidTankBehaviour> tanks;
    protected IItemHandlerModifiable itemCapability;
    protected IFluidHandler fluidCapability;
    List<Direction> disabledSpoutputs;
    Direction preferredSpoutput;
    protected List<ItemStack> spoutputBuffer;
    protected List<FluidStack> spoutputFluidBuffer;
    int recipeBackupCheck;
    public static final int OUTPUT_ANIMATION_TIME = 10;
    List<IntAttached<ItemStack>> visualizedOutputItems;
    List<IntAttached<FluidStack>> visualizedOutputFluids;
    @Nullable
    private BlazeBurnerBlock.HeatLevel cachedHeatLevel;

    public BasinBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.inputInventory.whenContentsChanged($ -> {
            this.contentsChanged = true;
        });
        this.outputInventory = new BasinInventory(9, this).forbidInsertion().withMaxStackSize(64);
        this.areFluidsMoving = false;
        this.itemCapability = new CombinedInvWrapper(new IItemHandlerModifiable[]{this.inputInventory, this.outputInventory});
        this.contentsChanged = true;
        this.ingredientRotation = LerpedFloat.angular().startWithValue(0.0);
        this.ingredientRotationSpeed = LerpedFloat.linear().startWithValue(0.0);
        this.invs = Couple.create((Object)this.inputInventory, (Object)this.outputInventory);
        this.tanks = Couple.create((Object)this.inputTank, (Object)this.outputTank);
        this.visualizedOutputItems = Collections.synchronizedList(new ArrayList());
        this.visualizedOutputFluids = Collections.synchronizedList(new ArrayList());
        this.disabledSpoutputs = new ArrayList<Direction>();
        this.preferredSpoutput = null;
        this.spoutputBuffer = new ArrayList<ItemStack>();
        this.spoutputFluidBuffer = new ArrayList<FluidStack>();
        this.recipeBackupCheck = 20;
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.BASIN.get(), (be, context) -> be.itemCapability);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.BASIN.get(), (be, context) -> be.fluidCapability);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this));
        this.filtering = new FilteringBehaviour(this, new BasinValueBox()).withCallback(newFilter -> {
            this.contentsChanged = true;
        }).forRecipes();
        behaviours.add(this.filtering);
        this.inputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 2, 1000, true).whenFluidUpdates(() -> {
            this.contentsChanged = true;
        });
        this.outputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, 2, 1000, true).whenFluidUpdates(() -> {
            this.contentsChanged = true;
        }).forbidInsertion();
        behaviours.add(this.inputTank);
        behaviours.add(this.outputTank);
        this.fluidCapability = new CombinedTankWrapper(this.outputTank.getCapability(), this.inputTank.getCapability());
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.inputInventory.deserializeNBT(registries, compound.getCompound("InputItems"));
        this.outputInventory.deserializeNBT(registries, compound.getCompound("OutputItems"));
        this.preferredSpoutput = null;
        if (compound.contains("PreferredSpoutput")) {
            this.preferredSpoutput = (Direction)NBTHelper.readEnum((CompoundTag)compound, (String)"PreferredSpoutput", Direction.class);
        }
        this.disabledSpoutputs.clear();
        ListTag disabledList = compound.getList("DisabledSpoutput", 8);
        disabledList.forEach(d -> this.disabledSpoutputs.add(Direction.valueOf((String)((StringTag)d).getAsString())));
        this.spoutputBuffer = NBTHelper.readItemList((ListTag)compound.getList("Overflow", 10), (HolderLookup.Provider)registries);
        this.spoutputFluidBuffer = NBTHelper.readCompoundList((ListTag)compound.getList("FluidOverflow", 10), tag -> FluidStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)tag));
        if (!clientPacket) {
            return;
        }
        NBTHelper.iterateCompoundList((ListTag)compound.getList("VisualizedItems", 10), c -> this.visualizedOutputItems.add((IntAttached<ItemStack>)IntAttached.with((int)10, (Object)ItemStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)c))));
        NBTHelper.iterateCompoundList((ListTag)compound.getList("VisualizedFluids", 10), c -> this.visualizedOutputFluids.add((IntAttached<FluidStack>)IntAttached.with((int)10, (Object)FluidStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)c))));
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.put("InputItems", (Tag)this.inputInventory.serializeNBT(registries));
        compound.put("OutputItems", (Tag)this.outputInventory.serializeNBT(registries));
        if (this.preferredSpoutput != null) {
            NBTHelper.writeEnum((CompoundTag)compound, (String)"PreferredSpoutput", (Enum)this.preferredSpoutput);
        }
        ListTag disabledList = new ListTag();
        this.disabledSpoutputs.forEach(d -> disabledList.add((Object)StringTag.valueOf((String)d.name())));
        compound.put("DisabledSpoutput", (Tag)disabledList);
        compound.put("Overflow", (Tag)NBTHelper.writeItemList(this.spoutputBuffer, (HolderLookup.Provider)registries));
        compound.put("FluidOverflow", (Tag)NBTHelper.writeCompoundList(this.spoutputFluidBuffer, fs -> (CompoundTag)fs.saveOptional(registries)));
        if (!clientPacket) {
            return;
        }
        compound.put("VisualizedItems", (Tag)NBTHelper.writeCompoundList(this.visualizedOutputItems, ia -> (CompoundTag)((ItemStack)ia.getValue()).saveOptional(registries)));
        compound.put("VisualizedFluids", (Tag)NBTHelper.writeCompoundList(this.visualizedOutputFluids, ia -> (CompoundTag)((FluidStack)ia.getValue()).saveOptional(registries)));
        this.visualizedOutputItems.clear();
        this.visualizedOutputFluids.clear();
    }

    public void clearContent() {
        this.spoutputBuffer.clear();
        this.inputInventory.clearContent();
        this.outputInventory.clearContent();
        this.filtering.setFilter(ItemStack.EMPTY);
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(this.level, this.worldPosition, (IItemHandler)this.inputInventory);
        ItemHelper.dropContents(this.level, this.worldPosition, (IItemHandler)this.outputInventory);
        this.spoutputBuffer.forEach(is -> Block.popResource((Level)this.level, (BlockPos)this.worldPosition, (ItemStack)is));
    }

    @Override
    public void remove() {
        super.remove();
        this.onEmptied();
    }

    public void onEmptied() {
        this.getOperator().ifPresent(be -> {
            be.basinRemoved = true;
        });
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.invalidateCapabilities();
    }

    @Override
    public void notifyUpdate() {
        super.notifyUpdate();
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (!this.level.isClientSide) {
            this.updateSpoutput();
            if (this.recipeBackupCheck-- > 0) {
                return;
            }
            this.recipeBackupCheck = 20;
            if (this.isEmpty()) {
                return;
            }
            this.notifyChangeOfContents();
            return;
        }
        BlockEntity blockEntity = this.level.getBlockEntity(this.worldPosition.above(2));
        if (!(blockEntity instanceof MechanicalMixerBlockEntity)) {
            this.setAreFluidsMoving(false);
            return;
        }
        MechanicalMixerBlockEntity mixer = (MechanicalMixerBlockEntity)blockEntity;
        this.setAreFluidsMoving(mixer.running && mixer.runningTicks <= 20);
    }

    public boolean isEmpty() {
        return this.inputInventory.isEmpty() && this.outputInventory.isEmpty() && this.inputTank.isEmpty() && this.outputTank.isEmpty();
    }

    public void onWrenched(Direction face) {
        BlockState blockState = this.getBlockState();
        Direction currentFacing = (Direction)blockState.getValue((Property)BasinBlock.FACING);
        this.disabledSpoutputs.remove(face);
        if (currentFacing == face) {
            if (this.preferredSpoutput == face) {
                this.preferredSpoutput = null;
            }
            this.disabledSpoutputs.add(face);
        } else {
            this.preferredSpoutput = face;
        }
        this.updateSpoutput();
    }

    private void updateSpoutput() {
        BlockState blockState = this.getBlockState();
        Direction currentFacing = (Direction)blockState.getValue((Property)BasinBlock.FACING);
        Direction newFacing = Direction.DOWN;
        for (Direction test : Iterate.horizontalDirections) {
            boolean canOutputTo = BasinBlock.canOutputTo((BlockGetter)this.level, this.worldPosition, test);
            if (!canOutputTo || this.disabledSpoutputs.contains(test)) continue;
            newFacing = test;
        }
        if (this.preferredSpoutput != null && BasinBlock.canOutputTo((BlockGetter)this.level, this.worldPosition, this.preferredSpoutput) && this.preferredSpoutput != Direction.UP) {
            newFacing = this.preferredSpoutput;
        }
        if (newFacing == currentFacing) {
            return;
        }
        this.level.setBlockAndUpdate(this.worldPosition, (BlockState)blockState.setValue((Property)BasinBlock.FACING, (Comparable)newFacing));
        if (newFacing.getAxis().isVertical()) {
            return;
        }
        for (int slot = 0; slot < this.outputInventory.getSlots(); ++slot) {
            ItemStack extractItem = this.outputInventory.extractItem(slot, 64, true);
            if (extractItem.isEmpty() || !this.acceptOutputs((List<ItemStack>)ImmutableList.of((Object)extractItem), Collections.emptyList(), true)) continue;
            this.acceptOutputs((List<ItemStack>)ImmutableList.of((Object)this.outputInventory.extractItem(slot, 64, false)), Collections.emptyList(), false);
        }
        IFluidHandler handler = this.outputTank.getCapability();
        for (int slot = 0; slot < handler.getTanks(); ++slot) {
            FluidStack fs = handler.getFluidInTank(slot).copy();
            if (fs.isEmpty() || !this.acceptOutputs(Collections.emptyList(), (List<FluidStack>)ImmutableList.of((Object)fs), true)) continue;
            handler.drain(fs, IFluidHandler.FluidAction.EXECUTE);
            this.acceptOutputs(Collections.emptyList(), (List<FluidStack>)ImmutableList.of((Object)fs), false);
        }
        this.notifyChangeOfContents();
        this.notifyUpdate();
    }

    @Override
    public void tick() {
        this.cachedHeatLevel = null;
        super.tick();
        if (this.level.isClientSide) {
            this.createFluidParticles();
            this.tickVisualizedOutputs();
            this.ingredientRotationSpeed.tickChaser();
            this.ingredientRotation.setValue((double)(this.ingredientRotation.getValue() + this.ingredientRotationSpeed.getValue()));
        }
        if (!(this.spoutputBuffer.isEmpty() && this.spoutputFluidBuffer.isEmpty() || this.level.isClientSide)) {
            this.tryClearingSpoutputOverflow();
        }
        if (!this.contentsChanged) {
            return;
        }
        this.contentsChanged = false;
        this.getOperator().ifPresent(be -> be.basinChecker.scheduleUpdate());
        for (Direction offset : Iterate.horizontalDirections) {
            BlockEntity be2;
            BlockPos toUpdate = this.worldPosition.above().relative(offset);
            BlockState stateToUpdate = this.level.getBlockState(toUpdate);
            if (!(stateToUpdate.getBlock() instanceof BasinBlock) || stateToUpdate.getValue((Property)BasinBlock.FACING) != offset.getOpposite() || !((be2 = this.level.getBlockEntity(toUpdate)) instanceof BasinBlockEntity)) continue;
            ((BasinBlockEntity)be2).contentsChanged = true;
        }
    }

    private void tryClearingSpoutputOverflow() {
        BlockState blockState = this.getBlockState();
        if (!(blockState.getBlock() instanceof BasinBlock)) {
            return;
        }
        Direction direction = (Direction)blockState.getValue((Property)BasinBlock.FACING);
        BlockEntity be = this.level.getBlockEntity(this.worldPosition.below().relative(direction));
        FilteringBehaviour filter = null;
        InvManipulationBehaviour inserter = null;
        if (be != null) {
            filter = BlockEntityBehaviour.get((BlockGetter)this.level, be.getBlockPos(), FilteringBehaviour.TYPE);
            inserter = BlockEntityBehaviour.get((BlockGetter)this.level, be.getBlockPos(), InvManipulationBehaviour.TYPE);
        }
        if (filter != null && filter.isRecipeFilter()) {
            filter = null;
        }
        IItemHandler targetInv = be == null ? null : (IItemHandler)Optional.ofNullable((IItemHandler)this.level.getCapability(Capabilities.ItemHandler.BLOCK, be.getBlockPos(), (Object)direction.getOpposite())).orElse(inserter == null ? null : (IItemHandler)inserter.getInventory());
        IFluidHandler targetTank = be == null ? null : (IFluidHandler)this.level.getCapability(Capabilities.FluidHandler.BLOCK, be.getBlockPos(), (Object)direction.getOpposite());
        boolean update = false;
        Iterator<ItemStack> iterator = this.spoutputBuffer.iterator();
        while (iterator.hasNext()) {
            ItemStack itemStack = iterator.next();
            if (direction == Direction.DOWN) {
                Block.popResource((Level)this.level, (BlockPos)this.worldPosition, (ItemStack)itemStack);
                iterator.remove();
                update = true;
                continue;
            }
            if (targetInv == null) break;
            ItemStack remainder = ItemHandlerHelper.insertItemStacked((IItemHandler)targetInv, (ItemStack)itemStack, (boolean)true);
            if (remainder.getCount() == itemStack.getCount() || filter != null && !filter.test(itemStack)) continue;
            if (this.visualizedOutputItems.size() < 3) {
                this.visualizedOutputItems.add((IntAttached<ItemStack>)IntAttached.withZero((Object)itemStack));
            }
            update = true;
            remainder = ItemHandlerHelper.insertItemStacked((IItemHandler)targetInv, (ItemStack)itemStack.copy(), (boolean)false);
            if (remainder.isEmpty()) {
                iterator.remove();
                continue;
            }
            itemStack.setCount(remainder.getCount());
        }
        iterator = this.spoutputFluidBuffer.iterator();
        block1: while (iterator.hasNext()) {
            FluidStack fluidStack = (FluidStack)iterator.next();
            if (direction == Direction.DOWN) {
                iterator.remove();
                update = true;
                continue;
            }
            if (targetTank == null) break;
            for (boolean simulate : Iterate.trueAndFalse) {
                IFluidHandler.FluidAction action = simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE;
                int fill = targetTank instanceof SmartFluidTankBehaviour.InternalFluidHandler ? ((SmartFluidTankBehaviour.InternalFluidHandler)targetTank).forceFill(fluidStack.copy(), action) : targetTank.fill(fluidStack.copy(), action);
                if (fill != fluidStack.getAmount()) continue block1;
                if (simulate) continue;
                update = true;
                iterator.remove();
                if (this.visualizedOutputFluids.size() >= 3) continue;
                this.visualizedOutputFluids.add((IntAttached<FluidStack>)IntAttached.withZero((Object)fluidStack));
            }
        }
        if (update) {
            this.notifyChangeOfContents();
            this.sendData();
        }
    }

    public float getTotalFluidUnits(float partialTicks) {
        int renderedFluids = 0;
        float totalUnits = 0.0f;
        for (SmartFluidTankBehaviour behaviour : this.getTanks()) {
            if (behaviour == null) continue;
            for (SmartFluidTankBehaviour.TankSegment tankSegment : behaviour.getTanks()) {
                float units;
                if (tankSegment.getRenderedFluid().isEmpty() || (units = tankSegment.getTotalUnits(partialTicks)) < 1.0f) continue;
                totalUnits += units;
                ++renderedFluids;
            }
        }
        if (renderedFluids == 0) {
            return 0.0f;
        }
        if (totalUnits < 1.0f) {
            return 0.0f;
        }
        return totalUnits;
    }

    private Optional<BasinOperatingBlockEntity> getOperator() {
        if (this.level == null) {
            return Optional.empty();
        }
        BlockEntity be = this.level.getBlockEntity(this.worldPosition.above(2));
        if (be instanceof BasinOperatingBlockEntity) {
            return Optional.of((BasinOperatingBlockEntity)be);
        }
        return Optional.empty();
    }

    public FilteringBehaviour getFilter() {
        return this.filtering;
    }

    public void notifyChangeOfContents() {
        this.contentsChanged = true;
    }

    public SmartInventory getInputInventory() {
        return this.inputInventory;
    }

    public SmartInventory getOutputInventory() {
        return this.outputInventory;
    }

    public boolean canContinueProcessing() {
        return this.spoutputBuffer.isEmpty() && this.spoutputFluidBuffer.isEmpty();
    }

    public boolean acceptOutputs(List<ItemStack> outputItems, List<FluidStack> outputFluids, boolean simulate) {
        this.outputInventory.allowInsertion();
        this.outputTank.allowInsertion();
        boolean acceptOutputsInner = this.acceptOutputsInner(outputItems, outputFluids, simulate);
        this.outputInventory.forbidInsertion();
        this.outputTank.forbidInsertion();
        return acceptOutputsInner;
    }

    private boolean acceptOutputsInner(List<ItemStack> outputItems, List<FluidStack> outputFluids, boolean simulate) {
        BlockState blockState = this.getBlockState();
        if (!(blockState.getBlock() instanceof BasinBlock)) {
            return false;
        }
        Direction direction = (Direction)blockState.getValue((Property)BasinBlock.FACING);
        if (direction != Direction.DOWN) {
            boolean externalTankNotPresent;
            InvManipulationBehaviour inserter;
            BlockEntity be = this.level.getBlockEntity(this.worldPosition.below().relative(direction));
            InvManipulationBehaviour invManipulationBehaviour = inserter = be == null ? null : BlockEntityBehaviour.get((BlockGetter)this.level, be.getBlockPos(), InvManipulationBehaviour.TYPE);
            IItemHandler targetInv = be == null ? null : (IItemHandler)Optional.ofNullable((IItemHandler)this.level.getCapability(Capabilities.ItemHandler.BLOCK, be.getBlockPos(), (Object)direction.getOpposite())).orElse(inserter == null ? null : (IItemHandler)inserter.getInventory());
            IFluidHandler targetTank = be == null ? null : (IFluidHandler)this.level.getCapability(Capabilities.FluidHandler.BLOCK, be.getBlockPos(), (Object)direction.getOpposite());
            boolean bl = externalTankNotPresent = targetTank == null;
            if (!outputItems.isEmpty() && targetInv == null) {
                return false;
            }
            if (!outputFluids.isEmpty() && externalTankNotPresent) {
                targetTank = this.outputTank.getCapability();
                if (targetTank == null) {
                    return false;
                }
                if (!this.acceptFluidOutputsIntoBasin(outputFluids, simulate, targetTank)) {
                    return false;
                }
            }
            if (simulate) {
                return true;
            }
            for (ItemStack itemStack : outputItems) {
                if (itemStack.isEmpty()) continue;
                this.spoutputBuffer.add(itemStack.copy());
            }
            if (!externalTankNotPresent) {
                for (FluidStack fluidStack : outputFluids) {
                    this.spoutputFluidBuffer.add(fluidStack.copy());
                }
            }
            return true;
        }
        SmartInventory targetInv = this.outputInventory;
        IFluidHandler targetTank = this.outputTank.getCapability();
        if (targetInv == null && !outputItems.isEmpty()) {
            return false;
        }
        if (!this.acceptItemOutputsIntoBasin(outputItems, simulate, (IItemHandler)targetInv)) {
            return false;
        }
        if (outputFluids.isEmpty()) {
            return true;
        }
        if (targetTank == null) {
            return false;
        }
        return this.acceptFluidOutputsIntoBasin(outputFluids, simulate, targetTank);
    }

    private boolean acceptFluidOutputsIntoBasin(List<FluidStack> outputFluids, boolean simulate, IFluidHandler targetTank) {
        for (FluidStack fluidStack : outputFluids) {
            IFluidHandler.FluidAction action = simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE;
            int fill = targetTank instanceof SmartFluidTankBehaviour.InternalFluidHandler ? ((SmartFluidTankBehaviour.InternalFluidHandler)targetTank).forceFill(fluidStack.copy(), action) : targetTank.fill(fluidStack.copy(), action);
            if (fill == fluidStack.getAmount()) continue;
            return false;
        }
        return true;
    }

    private boolean acceptItemOutputsIntoBasin(List<ItemStack> outputItems, boolean simulate, IItemHandler targetInv) {
        for (ItemStack itemStack : outputItems) {
            if (ItemHandlerHelper.insertItemStacked((IItemHandler)targetInv, (ItemStack)itemStack.copy(), (boolean)simulate).isEmpty()) continue;
            return false;
        }
        return true;
    }

    public void readOnlyItems(CompoundTag compound, HolderLookup.Provider registries) {
        this.inputInventory.deserializeNBT(registries, compound.getCompound("InputItems"));
        this.outputInventory.deserializeNBT(registries, compound.getCompound("OutputItems"));
    }

    public static BlazeBurnerBlock.HeatLevel getHeatLevelOf(BlockState state) {
        if (state.hasProperty(BlazeBurnerBlock.HEAT_LEVEL)) {
            return (BlazeBurnerBlock.HeatLevel)((Object)state.getValue(BlazeBurnerBlock.HEAT_LEVEL));
        }
        return AllTags.AllBlockTags.PASSIVE_BOILER_HEATERS.matches(state) && BlockHelper.isNotUnheated(state) ? BlazeBurnerBlock.HeatLevel.SMOULDERING : BlazeBurnerBlock.HeatLevel.NONE;
    }

    public Couple<SmartFluidTankBehaviour> getTanks() {
        return this.tanks;
    }

    public Couple<SmartInventory> getInvs() {
        return this.invs;
    }

    private void tickVisualizedOutputs() {
        this.visualizedOutputFluids.forEach(IntAttached::decrement);
        this.visualizedOutputItems.forEach(IntAttached::decrement);
        this.visualizedOutputFluids.removeIf(IntAttached::isOrBelowZero);
        this.visualizedOutputItems.removeIf(IntAttached::isOrBelowZero);
    }

    private void createFluidParticles() {
        RandomSource r = this.level.random;
        if (!this.visualizedOutputFluids.isEmpty()) {
            this.createOutputFluidParticles(r);
        }
        if (!this.areFluidsMoving && r.nextFloat() > 0.125f) {
            return;
        }
        int segments = 0;
        for (SmartFluidTankBehaviour behaviour : this.getTanks()) {
            if (behaviour == null) continue;
            for (SmartFluidTankBehaviour.TankSegment tankSegment : behaviour.getTanks()) {
                if (tankSegment.isEmpty(0.0f)) continue;
                ++segments;
            }
        }
        if (segments < 2) {
            return;
        }
        float totalUnits = this.getTotalFluidUnits(0.0f);
        if (totalUnits == 0.0f) {
            return;
        }
        float fluidLevel = Mth.clamp((float)(totalUnits / 2000.0f), (float)0.0f, (float)1.0f);
        float rim = 0.125f;
        float space = 0.75f;
        float surface = (float)this.worldPosition.getY() + rim + space * fluidLevel + 0.03125f;
        if (this.areFluidsMoving) {
            this.createMovingFluidParticles(surface, segments);
            return;
        }
        for (SmartFluidTankBehaviour behaviour : this.getTanks()) {
            if (behaviour == null) continue;
            for (SmartFluidTankBehaviour.TankSegment tankSegment : behaviour.getTanks()) {
                if (tankSegment.isEmpty(0.0f)) continue;
                float x = (float)this.worldPosition.getX() + rim + space * r.nextFloat();
                float z = (float)this.worldPosition.getZ() + rim + space * r.nextFloat();
                this.level.addAlwaysVisibleParticle((ParticleOptions)new FluidParticleData(AllParticleTypes.BASIN_FLUID.get(), tankSegment.getRenderedFluid()), (double)x, (double)surface, (double)z, 0.0, 0.0, 0.0);
            }
        }
    }

    private void createOutputFluidParticles(RandomSource r) {
        BlockState blockState = this.getBlockState();
        if (!(blockState.getBlock() instanceof BasinBlock)) {
            return;
        }
        Direction direction = (Direction)blockState.getValue((Property)BasinBlock.FACING);
        if (direction == Direction.DOWN) {
            return;
        }
        Vec3 directionVec = Vec3.atLowerCornerOf((Vec3i)direction.getNormal());
        Vec3 outVec = VecHelper.getCenterOf((Vec3i)this.worldPosition).add(directionVec.scale(0.65).subtract(0.0, 0.25, 0.0));
        Vec3 outMotion = directionVec.scale(0.0625).add(0.0, -0.0625, 0.0);
        for (int i = 0; i < 2; ++i) {
            this.visualizedOutputFluids.forEach(ia -> {
                FluidStack fluidStack = (FluidStack)ia.getValue();
                ParticleOptions fluidParticle = FluidFX.getFluidParticle(fluidStack);
                Vec3 m = VecHelper.offsetRandomly((Vec3)outMotion, (RandomSource)r, (float)0.0625f);
                this.level.addAlwaysVisibleParticle(fluidParticle, outVec.x, outVec.y, outVec.z, m.x, m.y, m.z);
            });
        }
    }

    private void createMovingFluidParticles(float surface, int segments) {
        Vec3 pointer = new Vec3(1.0, 0.0, 0.0).scale(0.0625);
        float interval = 360.0f / (float)segments;
        Vec3 centerOf = VecHelper.getCenterOf((Vec3i)this.worldPosition);
        float intervalOffset = AnimationTickHolder.getTicks() * 18 % 360;
        int currentSegment = 0;
        for (SmartFluidTankBehaviour behaviour : this.getTanks()) {
            if (behaviour == null) continue;
            for (SmartFluidTankBehaviour.TankSegment tankSegment : behaviour.getTanks()) {
                if (tankSegment.isEmpty(0.0f)) continue;
                float angle = interval * (float)(1 + currentSegment) + intervalOffset;
                Vec3 vec = centerOf.add(VecHelper.rotate((Vec3)pointer, (double)angle, (Direction.Axis)Direction.Axis.Y));
                this.level.addAlwaysVisibleParticle((ParticleOptions)new FluidParticleData(AllParticleTypes.BASIN_FLUID.get(), tankSegment.getRenderedFluid()), vec.x(), (double)surface, vec.z(), 1.0, 0.0, 0.0);
                ++currentSegment;
            }
        }
    }

    public boolean areFluidsMoving() {
        return this.areFluidsMoving;
    }

    public boolean setAreFluidsMoving(boolean areFluidsMoving) {
        this.areFluidsMoving = areFluidsMoving;
        this.ingredientRotationSpeed.chase(areFluidsMoving ? 20.0 : 0.0, (double)0.1f, LerpedFloat.Chaser.EXP);
        return areFluidsMoving;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CreateLang.translate("gui.goggles.basin_contents", new Object[0]).forGoggles(tooltip);
        if (this.itemCapability == null) {
            this.itemCapability = new ItemStackHandler();
        }
        if (this.fluidCapability == null) {
            this.fluidCapability = new FluidTank(0);
        }
        boolean isEmpty = true;
        for (int i = 0; i < this.itemCapability.getSlots(); ++i) {
            ItemStack stackInSlot = this.itemCapability.getStackInSlot(i);
            if (stackInSlot.isEmpty()) continue;
            CreateLang.text("").add(Component.translatable((String)stackInSlot.getDescriptionId()).withStyle(ChatFormatting.GRAY)).add(CreateLang.text(" x" + stackInSlot.getCount()).style(ChatFormatting.GREEN)).forGoggles(tooltip, 1);
            isEmpty = false;
        }
        LangBuilder mb = CreateLang.translate("generic.unit.millibuckets", new Object[0]);
        for (int i = 0; i < this.fluidCapability.getTanks(); ++i) {
            FluidStack fluidStack = this.fluidCapability.getFluidInTank(i);
            if (fluidStack.isEmpty()) continue;
            CreateLang.text("").add(CreateLang.fluidName(fluidStack).add(CreateLang.text(" ")).style(ChatFormatting.GRAY).add(CreateLang.number(fluidStack.getAmount()).add(mb).style(ChatFormatting.BLUE))).forGoggles(tooltip, 1);
            isEmpty = false;
        }
        if (isEmpty) {
            tooltip.remove(0);
        }
        return true;
    }

    @NotNull
    BlazeBurnerBlock.HeatLevel getHeatLevel() {
        if (this.cachedHeatLevel == null) {
            if (this.level == null) {
                return BlazeBurnerBlock.HeatLevel.NONE;
            }
            this.cachedHeatLevel = BasinBlockEntity.getHeatLevelOf(this.level.getBlockState(this.getBlockPos().below(1)));
        }
        return this.cachedHeatLevel;
    }

    static class BasinValueBox
    extends ValueBoxTransform.Sided {
        BasinValueBox() {
        }

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace((double)8.0, (double)12.0, (double)16.05);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction.getAxis().isHorizontal();
        }
    }
}
