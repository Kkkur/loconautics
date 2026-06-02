/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  net.createmod.catnip.math.BlockFace
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.Mth
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.ticks.TickPriority
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  net.neoforged.neoforge.items.IItemHandler
 */
package com.simibubi.create.content.redstone.thresholdSwitch;

import com.google.common.base.Predicate;
import com.simibubi.create.compat.thresholdSwitch.FunctionalStorage;
import com.simibubi.create.compat.thresholdSwitch.SophisticatedStorage;
import com.simibubi.create.compat.thresholdSwitch.StorageDrawers;
import com.simibubi.create.compat.thresholdSwitch.ThresholdSwitchCompat;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingInventory;
import com.simibubi.create.content.redstone.DirectedDirectionalBlock;
import com.simibubi.create.content.redstone.FilteredDetectorFilterSlot;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlock;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchBlock;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchObservable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.CapManipulationBehaviourBase;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.TankManipulationBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryTrackerBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.ticks.TickPriority;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

public class ThresholdSwitchBlockEntity
extends SmartBlockEntity
implements Clearable {
    public int onWhenAbove = 128;
    public int offWhenBelow = 64;
    public int currentMinLevel;
    public int currentLevel = -1;
    public int currentMaxLevel;
    public boolean inStacks;
    private boolean redstoneState = false;
    private boolean inverted = false;
    private boolean poweredAfterDelay = false;
    private FilteringBehaviour filtering;
    private InvManipulationBehaviour observedInventory;
    private TankManipulationBehaviour observedTank;
    private VersionedInventoryTrackerBehaviour invVersionTracker;
    private static final List<ThresholdSwitchCompat> COMPAT = List.of(new FunctionalStorage(), new SophisticatedStorage(), new StorageDrawers());

    public ThresholdSwitchBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.setLazyTickRate(10);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        this.onWhenAbove = compound.getInt("OnAboveAmount");
        this.offWhenBelow = compound.getInt("OffBelowAmount");
        this.currentLevel = compound.getInt("CurrentAmount");
        this.currentMinLevel = compound.getInt("CurrentMinAmount");
        this.currentMaxLevel = compound.getInt("CurrentMaxAmount");
        this.inStacks = compound.getBoolean("InStacks");
        this.redstoneState = compound.getBoolean("Powered");
        this.inverted = compound.getBoolean("Inverted");
        this.poweredAfterDelay = compound.getBoolean("PoweredAfterDelay");
        super.read(compound, registries, clientPacket);
    }

    protected void writeCommon(CompoundTag compound) {
        compound.putFloat("OnAboveAmount", (float)this.onWhenAbove);
        compound.putFloat("OffBelowAmount", (float)this.offWhenBelow);
        compound.putBoolean("Inverted", this.inverted);
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        this.writeCommon(compound);
        compound.putInt("CurrentAmount", this.currentLevel);
        compound.putInt("CurrentMinAmount", this.currentMinLevel);
        compound.putInt("CurrentMaxAmount", this.currentMaxLevel);
        compound.putBoolean("InStacks", this.inStacks);
        compound.putBoolean("Powered", this.redstoneState);
        compound.putBoolean("PoweredAfterDelay", this.poweredAfterDelay);
        super.write(compound, registries, clientPacket);
    }

    @Override
    public void writeSafe(CompoundTag compound, HolderLookup.Provider registries) {
        this.writeCommon(compound);
        super.writeSafe(compound, registries);
    }

    public int getMinLevel() {
        return this.currentMinLevel;
    }

    public int getStockLevel() {
        return this.currentLevel;
    }

    public int getMaxLevel() {
        return this.currentMaxLevel;
    }

    public void updateCurrentLevel() {
        boolean changed = false;
        int prevLevel = this.currentLevel;
        int prevMaxLevel = this.currentMaxLevel;
        BlockPos target = this.getTargetPos();
        BlockEntity targetBlockEntity = this.level.getBlockEntity(target);
        this.observedInventory.findNewCapability();
        this.observedTank.findNewCapability();
        if (targetBlockEntity instanceof ThresholdSwitchObservable) {
            ThresholdSwitchObservable observable = (ThresholdSwitchObservable)targetBlockEntity;
            this.currentMinLevel = observable.getMinValue();
            this.currentLevel = observable.getCurrentValue();
            this.currentMaxLevel = observable.getMaxValue();
        } else if (this.observedInventory.hasInventory() || this.observedTank.hasInventory()) {
            FluidStack stackInSlot;
            int slot;
            this.currentMinLevel = 0;
            this.currentLevel = 0;
            this.currentMaxLevel = 0;
            if (this.observedInventory.hasInventory()) {
                IItemHandler inv = (IItemHandler)this.observedInventory.getInventory();
                if (this.invVersionTracker.stillWaiting(inv)) {
                    this.currentLevel = prevLevel;
                    this.currentMaxLevel = prevMaxLevel;
                } else {
                    this.invVersionTracker.awaitNewVersion(inv);
                    for (slot = 0; slot < inv.getSlots(); ++slot) {
                        stackInSlot = inv.getStackInSlot(slot);
                        int finalSlot = slot;
                        long space = COMPAT.stream().filter(compat -> compat.isFromThisMod(targetBlockEntity)).map(compat -> compat.getSpaceInSlot(inv, finalSlot)).findFirst().orElseGet(() -> ThresholdSwitchBlockEntity.lambda$updateCurrentLevel$2((ItemStack)stackInSlot, inv, finalSlot));
                        int count = stackInSlot.getCount();
                        if (space == 0L) continue;
                        this.currentMaxLevel = (int)((long)this.currentMaxLevel + space);
                        if (!this.filtering.test((ItemStack)stackInSlot)) continue;
                        this.currentLevel += count;
                    }
                }
            }
            if (this.observedTank.hasInventory()) {
                IFluidHandler tank = (IFluidHandler)this.observedTank.getInventory();
                for (slot = 0; slot < tank.getTanks(); ++slot) {
                    stackInSlot = tank.getFluidInTank(slot);
                    int space = tank.getTankCapacity(slot);
                    int count = stackInSlot.getAmount();
                    if (space == 0) continue;
                    this.currentMaxLevel += space;
                    if (!this.filtering.test(stackInSlot)) continue;
                    this.currentLevel += count;
                }
            }
        } else {
            this.currentMinLevel = -1;
            this.currentMaxLevel = -1;
            if (this.currentLevel == -1) {
                return;
            }
            this.level.setBlock(this.worldPosition, (BlockState)this.getBlockState().setValue((Property)ThresholdSwitchBlock.LEVEL, (Comparable)Integer.valueOf(0)), 3);
            this.currentLevel = -1;
            this.redstoneState = false;
            this.sendData();
            this.scheduleBlockTick();
            return;
        }
        this.currentLevel = Mth.clamp((int)this.currentLevel, (int)this.currentMinLevel, (int)this.currentMaxLevel);
        changed = this.currentLevel != prevLevel;
        boolean previouslyPowered = this.redstoneState;
        if (this.redstoneState && this.currentLevel <= this.offWhenBelow) {
            this.redstoneState = false;
        } else if (!this.redstoneState && this.currentLevel >= this.onWhenAbove) {
            this.redstoneState = true;
        }
        boolean update = previouslyPowered != this.redstoneState;
        int displayLevel = 0;
        float normedLevel = (float)(this.currentLevel - this.currentMinLevel) / (float)(this.currentMaxLevel - this.currentMinLevel);
        if (this.currentLevel > 0) {
            displayLevel = (int)(1.0f + normedLevel * 4.0f);
        }
        this.level.setBlock(this.worldPosition, (BlockState)this.getBlockState().setValue((Property)ThresholdSwitchBlock.LEVEL, (Comparable)Integer.valueOf(displayLevel)), update ? 3 : 2);
        if (update) {
            this.scheduleBlockTick();
        }
        if (changed || update) {
            DisplayLinkBlock.notifyGatherers((LevelAccessor)this.level, this.worldPosition);
            this.notifyUpdate();
        }
    }

    private boolean isSuitableInventory(BlockEntity be) {
        return be != null && !(be instanceof StockTickerBlockEntity) && !(this.level.getCapability(Capabilities.ItemHandler.BLOCK, be.getBlockPos(), null, be, null) instanceof ProcessingInventory);
    }

    private BlockPos getTargetPos() {
        return this.worldPosition.relative(ThresholdSwitchBlock.getTargetDirection(this.getBlockState()));
    }

    public ItemStack getDisplayItemForScreen() {
        BlockPos target = this.getTargetPos();
        return new ItemStack((ItemLike)this.level.getBlockState(target).getBlock());
    }

    public MutableComponent format(int value, boolean stacks) {
        BlockEntity blockEntity;
        ThresholdType type = this.getTypeOfCurrentTarget();
        if (type == ThresholdType.CUSTOM && (blockEntity = this.level.getBlockEntity(this.getTargetPos())) instanceof ThresholdSwitchObservable) {
            ThresholdSwitchObservable tso = (ThresholdSwitchObservable)blockEntity;
            return tso.format(value);
        }
        String suffix = type == ThresholdType.ITEM ? (stacks ? "schedule.condition.threshold.stacks" : "schedule.condition.threshold.items") : "schedule.condition.threshold.buckets";
        return CreateLang.text(value + " ").add(CreateLang.translate(suffix, new Object[0])).component();
    }

    public ThresholdType getTypeOfCurrentTarget() {
        if (this.observedInventory.hasInventory()) {
            return ThresholdType.ITEM;
        }
        if (this.observedTank.hasInventory()) {
            return ThresholdType.FLUID;
        }
        if (this.level.getBlockEntity(this.getTargetPos()) instanceof ThresholdSwitchObservable) {
            return ThresholdType.CUSTOM;
        }
        return ThresholdType.UNSUPPORTED;
    }

    protected void scheduleBlockTick() {
        Block block = this.getBlockState().getBlock();
        if (!this.level.getBlockTicks().willTickThisTick(this.worldPosition, (Object)block)) {
            this.level.scheduleTick(this.worldPosition, block, 2, TickPriority.NORMAL);
        }
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (this.level.isClientSide) {
            return;
        }
        this.updateCurrentLevel();
    }

    public void clearContent() {
        this.filtering.setFilter(ItemStack.EMPTY);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.filtering = new FilteringBehaviour(this, new FilteredDetectorFilterSlot(true)).withCallback($ -> {
            this.updateCurrentLevel();
            this.invVersionTracker.reset();
        });
        behaviours.add(this.filtering);
        this.invVersionTracker = new VersionedInventoryTrackerBehaviour(this);
        behaviours.add(this.invVersionTracker);
        CapManipulationBehaviourBase.InterfaceProvider towardBlockFacing = (w, p, s) -> new BlockFace(p, DirectedDirectionalBlock.getTargetDirection(s));
        this.observedInventory = (InvManipulationBehaviour)((InvManipulationBehaviour)new InvManipulationBehaviour(this, towardBlockFacing).bypassSidedness()).withFilter((Predicate<BlockEntity>)((Predicate)this::isSuitableInventory));
        behaviours.add(this.observedInventory);
        this.observedTank = (TankManipulationBehaviour)new TankManipulationBehaviour(this, towardBlockFacing).bypassSidedness();
        behaviours.add(this.observedTank);
    }

    public float getLevelForDisplay() {
        return this.currentLevel == -1 ? 0.0f : (float)this.currentLevel;
    }

    public boolean getState() {
        return this.redstoneState;
    }

    public boolean shouldBePowered() {
        return this.inverted != this.redstoneState;
    }

    public void updatePowerAfterDelay() {
        this.poweredAfterDelay = this.shouldBePowered();
        this.level.blockUpdated(this.worldPosition, this.getBlockState().getBlock());
        this.sendData();
    }

    public boolean isPowered() {
        return this.poweredAfterDelay;
    }

    public boolean isInverted() {
        return this.inverted;
    }

    public void setInverted(boolean inverted) {
        if (inverted == this.inverted) {
            return;
        }
        this.inverted = inverted;
        this.updatePowerAfterDelay();
    }

    private static /* synthetic */ Long lambda$updateCurrentLevel$2(ItemStack stackInSlot, IItemHandler inv, int finalSlot) {
        return Math.min((Integer)stackInSlot.getOrDefault(DataComponents.MAX_STACK_SIZE, (Object)64), inv.getSlotLimit(finalSlot));
    }

    public static enum ThresholdType {
        UNSUPPORTED,
        ITEM,
        FLUID,
        CUSTOM;

    }
}
