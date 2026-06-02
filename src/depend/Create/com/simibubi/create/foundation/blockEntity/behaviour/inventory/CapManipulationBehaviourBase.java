/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  net.createmod.catnip.math.BlockFace
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.capabilities.BlockCapability
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.blockEntity.behaviour.inventory;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

public abstract class CapManipulationBehaviourBase<T, S extends CapManipulationBehaviourBase<?, ?>>
extends BlockEntityBehaviour {
    protected InterfaceProvider target;
    protected T targetCapability;
    protected Predicate<BlockEntity> filter;
    protected boolean simulateNext;
    protected boolean bypassSided;
    private boolean findNewNextTick;

    public CapManipulationBehaviourBase(SmartBlockEntity be, InterfaceProvider target) {
        super(be);
        this.setLazyTickRate(5);
        this.target = target;
        this.targetCapability = null;
        this.simulateNext = false;
        this.bypassSided = false;
        this.filter = Predicates.alwaysTrue();
    }

    protected abstract BlockCapability<T, Direction> capability();

    @Override
    public void initialize() {
        super.initialize();
        this.findNewNextTick = true;
    }

    @Override
    public void onNeighborChanged(BlockPos neighborPos) {
        if (this.getTarget().getConnectedPos().equals((Object)neighborPos)) {
            this.onHandlerInvalidated();
        }
    }

    public S bypassSidedness() {
        this.bypassSided = true;
        return (S)this;
    }

    public S simulate() {
        this.simulateNext = true;
        return (S)this;
    }

    public S withFilter(Predicate<BlockEntity> filter) {
        this.filter = filter;
        return (S)this;
    }

    public boolean hasInventory() {
        return this.targetCapability != null;
    }

    @Nullable
    public T getInventory() {
        return this.targetCapability;
    }

    public BlockFace getTarget() {
        return this.target.getTarget(this.getWorld(), this.blockEntity.getBlockPos(), this.blockEntity.getBlockState());
    }

    protected boolean onHandlerInvalidated() {
        if (this.targetCapability == null) {
            return false;
        }
        this.findNewNextTick = true;
        this.targetCapability = null;
        return true;
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (this.targetCapability == null) {
            this.findNewCapability();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.findNewNextTick || this.getWorld().getGameTime() % 64L == 0L) {
            this.findNewNextTick = false;
            this.findNewCapability();
        }
    }

    public int getAmountFromFilter() {
        int amount = -1;
        FilteringBehaviour filter = this.blockEntity.getBehaviour(FilteringBehaviour.TYPE);
        if (filter != null && !filter.anyAmount()) {
            amount = filter.getAmount();
        }
        return amount;
    }

    public ItemHelper.ExtractionCountMode getModeFromFilter() {
        ItemHelper.ExtractionCountMode mode = ItemHelper.ExtractionCountMode.UPTO;
        FilteringBehaviour filter = this.blockEntity.getBehaviour(FilteringBehaviour.TYPE);
        if (filter != null && !filter.upTo) {
            mode = ItemHelper.ExtractionCountMode.EXACTLY;
        }
        return mode;
    }

    public void findNewCapability() {
        Level world = this.getWorld();
        BlockFace targetBlockFace = this.getTarget().getOpposite();
        BlockPos pos = targetBlockFace.getPos();
        this.targetCapability = null;
        if (!world.isLoaded(pos)) {
            return;
        }
        BlockEntity invBE = world.getBlockEntity(pos);
        if (!this.filter.test((Object)invBE)) {
            return;
        }
        BlockCapability<T, Direction> capability = this.capability();
        this.targetCapability = world.getCapability(capability, pos, (Object)(this.bypassSided ? null : targetBlockFace.getFace()));
    }

    @FunctionalInterface
    public static interface InterfaceProvider {
        public static InterfaceProvider towardBlockFacing() {
            return (w, p, s) -> new BlockFace(p, s.hasProperty((Property)BlockStateProperties.FACING) ? (Direction)s.getValue((Property)BlockStateProperties.FACING) : (Direction)s.getValue((Property)BlockStateProperties.HORIZONTAL_FACING));
        }

        public static InterfaceProvider oppositeOfBlockFacing() {
            return (w, p, s) -> new BlockFace(p, (s.hasProperty((Property)BlockStateProperties.FACING) ? (Direction)s.getValue((Property)BlockStateProperties.FACING) : (Direction)s.getValue((Property)BlockStateProperties.HORIZONTAL_FACING)).getOpposite());
        }

        public BlockFace getTarget(Level var1, BlockPos var2, BlockState var3);
    }
}
