/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.PriorityQueue
 *  it.unimi.dsi.fastutil.objects.ObjectHeapPriorityQueue
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.BBHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.LiquidBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.neoforged.neoforge.fluids.FluidStack
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.fluids.transfer;

import com.simibubi.create.content.fluids.transfer.FluidManipulationBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.fluid.FluidHelper;
import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.objects.ObjectHeapPriorityQueue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.BBHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class FluidDrainingBehaviour
extends FluidManipulationBehaviour {
    public static final BehaviourType<FluidDrainingBehaviour> TYPE = new BehaviourType();
    Fluid fluid;
    Set<BlockPos> validationSet;
    PriorityQueue<FluidManipulationBehaviour.BlockPosEntry> queue;
    boolean isValid;
    List<FluidManipulationBehaviour.BlockPosEntry> validationFrontier;
    Set<BlockPos> validationVisited = new HashSet<BlockPos>();
    Set<BlockPos> newValidationSet;

    public FluidDrainingBehaviour(SmartBlockEntity be) {
        super(be);
        this.validationFrontier = new ArrayList<FluidManipulationBehaviour.BlockPosEntry>();
        this.validationSet = new HashSet<BlockPos>();
        this.newValidationSet = new HashSet<BlockPos>();
        this.queue = new ObjectHeapPriorityQueue(this::comparePositions);
    }

    /*
     * Enabled aggressive block sorting
     */
    @Nullable
    public boolean pullNext(BlockPos root, boolean simulate) {
        if (!this.frontier.isEmpty()) {
            return false;
        }
        if (!Objects.equals(root, this.rootPos)) {
            this.rebuildContext(root);
            return false;
        }
        if (this.counterpartActed) {
            this.counterpartActed = false;
            this.softReset(root);
            return false;
        }
        if (this.affectedArea == null) {
            this.affectedArea = BoundingBox.fromCorners((Vec3i)root, (Vec3i)root);
        }
        Level world = this.getWorld();
        if (!this.queue.isEmpty() && !this.isValid) {
            this.rebuildContext(root);
            return false;
        }
        if (this.validationFrontier.isEmpty() && !this.queue.isEmpty() && !simulate && this.revalidateIn == 0) {
            this.revalidate(root);
        }
        if (this.infinite) {
            this.blockEntity.award(AllAdvancements.HOSE_PULLEY);
            if (FluidHelper.isLava(this.fluid)) {
                this.blockEntity.award(AllAdvancements.HOSE_PULLEY_LAVA);
            }
            this.playEffect(world, root, this.fluid, true);
            return true;
        }
        while (!this.queue.isEmpty()) {
            Fluid fluid;
            BlockState emptied;
            BlockPos currentPos;
            block23: {
                BlockState blockState;
                currentPos = ((FluidManipulationBehaviour.BlockPosEntry)this.queue.first()).pos();
                emptied = blockState = world.getBlockState(currentPos);
                fluid = Fluids.EMPTY;
                if (blockState.hasProperty((Property)BlockStateProperties.WATERLOGGED) && ((Boolean)blockState.getValue((Property)BlockStateProperties.WATERLOGGED)).booleanValue()) {
                    emptied = (BlockState)blockState.setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(false));
                    fluid = Fluids.WATER;
                } else {
                    Block block = blockState.getBlock();
                    if (block instanceof LiquidBlock) {
                        LiquidBlock flowingFluid = (LiquidBlock)block;
                        emptied = Blocks.AIR.defaultBlockState();
                        if ((Integer)blockState.getValue((Property)LiquidBlock.LEVEL) == 0) {
                            fluid = flowingFluid.fluid;
                            break block23;
                        } else {
                            this.affectedArea = BBHelper.encapsulate((BoundingBox)this.affectedArea, (BoundingBox)BoundingBox.fromCorners((Vec3i)currentPos, (Vec3i)currentPos));
                            if (!this.blockEntity.isVirtual()) {
                                world.setBlock(currentPos, emptied, 18);
                            }
                            this.queue.dequeue();
                            if (!this.queue.isEmpty()) continue;
                            this.isValid = this.checkValid(world, this.rootPos);
                            this.reset();
                            continue;
                        }
                    }
                    if (blockState.getFluidState().getType() != Fluids.EMPTY && blockState.getCollisionShape((BlockGetter)world, currentPos, CollisionContext.empty()).isEmpty()) {
                        fluid = blockState.getFluidState().getType();
                        emptied = Blocks.AIR.defaultBlockState();
                    }
                }
            }
            if (this.fluid == null) {
                this.fluid = fluid;
            }
            if (!this.fluid.isSame(fluid)) {
                this.queue.dequeue();
                if (!this.queue.isEmpty()) continue;
                this.isValid = this.checkValid(world, this.rootPos);
                this.reset();
                continue;
            }
            if (simulate) {
                return true;
            }
            this.playEffect(world, currentPos, fluid, true);
            this.blockEntity.award(AllAdvancements.HOSE_PULLEY);
            if (!this.blockEntity.isVirtual()) {
                world.setBlock(currentPos, emptied, 18);
                BlockState stateAbove = world.getBlockState(currentPos.above());
                if (stateAbove.getFluidState().getType() == Fluids.EMPTY && !stateAbove.canSurvive((LevelReader)world, currentPos.above())) {
                    world.setBlock(currentPos.above(), Blocks.AIR.defaultBlockState(), 18);
                }
            }
            this.affectedArea = BBHelper.encapsulate((BoundingBox)this.affectedArea, (BlockPos)currentPos);
            this.queue.dequeue();
            if (this.queue.isEmpty()) {
                this.isValid = this.checkValid(world, this.rootPos);
                this.reset();
                return true;
            }
            if (this.validationSet.contains(currentPos)) return true;
            this.reset();
            return true;
        }
        if (this.rootPos == null) {
            return false;
        }
        if (!this.isValid) return false;
        this.rebuildContext(root);
        return false;
    }

    protected void softReset(BlockPos root) {
        this.queue.clear();
        this.validationSet.clear();
        this.newValidationSet.clear();
        this.validationFrontier.clear();
        this.validationVisited.clear();
        this.visited.clear();
        this.infinite = false;
        this.setValidationTimer();
        this.frontier.add(new FluidManipulationBehaviour.BlockPosEntry(root, 0));
        this.blockEntity.sendData();
    }

    protected boolean checkValid(Level world, BlockPos root) {
        BlockPos currentPos = root;
        for (int timeout = 1000; timeout > 0 && !root.equals((Object)this.blockEntity.getBlockPos()); --timeout) {
            FluidBlockType canPullFluidsFrom = this.canPullFluidsFrom(world.getBlockState(currentPos), currentPos);
            if (canPullFluidsFrom == FluidBlockType.FLOWING) {
                for (Direction d : Iterate.directions) {
                    BlockPos side = currentPos.relative(d);
                    if (this.canPullFluidsFrom(world.getBlockState(side), side) != FluidBlockType.SOURCE) continue;
                    return true;
                }
            } else {
                if (canPullFluidsFrom != FluidBlockType.SOURCE) break;
                return true;
            }
            currentPos = currentPos.above();
        }
        return false;
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);
        if (!clientPacket && this.affectedArea != null) {
            this.frontier.add(new FluidManipulationBehaviour.BlockPosEntry(this.rootPos, 0));
        }
    }

    protected FluidBlockType canPullFluidsFrom(BlockState blockState, BlockPos pos) {
        if (blockState.hasProperty((Property)BlockStateProperties.WATERLOGGED) && ((Boolean)blockState.getValue((Property)BlockStateProperties.WATERLOGGED)).booleanValue()) {
            return FluidBlockType.SOURCE;
        }
        if (blockState.getBlock() instanceof LiquidBlock) {
            return (Integer)blockState.getValue((Property)LiquidBlock.LEVEL) == 0 ? FluidBlockType.SOURCE : FluidBlockType.FLOWING;
        }
        if (blockState.getFluidState().getType() != Fluids.EMPTY && blockState.getCollisionShape((BlockGetter)this.getWorld(), pos, CollisionContext.empty()).isEmpty()) {
            return FluidBlockType.SOURCE;
        }
        return FluidBlockType.NONE;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.rootPos != null) {
            this.isValid = this.checkValid(this.getWorld(), this.rootPos);
        }
        if (!this.frontier.isEmpty()) {
            this.continueSearch();
            return;
        }
        if (!this.validationFrontier.isEmpty()) {
            this.continueValidation();
            return;
        }
        if (this.revalidateIn > 0) {
            --this.revalidateIn;
        }
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
    }

    public void rebuildContext(BlockPos root) {
        this.reset();
        this.rootPos = root;
        this.affectedArea = BoundingBox.fromCorners((Vec3i)this.rootPos, (Vec3i)this.rootPos);
        if (this.isValid) {
            this.frontier.add(new FluidManipulationBehaviour.BlockPosEntry(root, 0));
        }
    }

    public void revalidate(BlockPos root) {
        this.validationFrontier.clear();
        this.validationVisited.clear();
        this.newValidationSet.clear();
        this.validationFrontier.add(new FluidManipulationBehaviour.BlockPosEntry(root, 0));
        this.setValidationTimer();
    }

    private void continueSearch() {
        try {
            this.fluid = this.search(this.fluid, this.frontier, this.visited, (e, d) -> {
                this.queue.enqueue((Object)new FluidManipulationBehaviour.BlockPosEntry((BlockPos)e, (int)d));
                this.validationSet.add((BlockPos)e);
            }, false);
        }
        catch (FluidManipulationBehaviour.ChunkNotLoadedException e2) {
            this.blockEntity.sendData();
            this.frontier.clear();
            this.visited.clear();
        }
        int maxBlocks = this.maxBlocks();
        if (this.visited.size() >= maxBlocks && this.canDrainInfinitely(this.fluid) && !this.queue.isEmpty()) {
            this.infinite = true;
            BlockPos firstValid = ((FluidManipulationBehaviour.BlockPosEntry)this.queue.first()).pos();
            this.frontier.clear();
            this.visited.clear();
            this.queue.clear();
            this.queue.enqueue((Object)new FluidManipulationBehaviour.BlockPosEntry(firstValid, 0));
            this.blockEntity.sendData();
            return;
        }
        if (!this.frontier.isEmpty()) {
            return;
        }
        this.blockEntity.sendData();
        this.visited.clear();
    }

    private void continueValidation() {
        try {
            this.search(this.fluid, this.validationFrontier, this.validationVisited, (e, d) -> this.newValidationSet.add((BlockPos)e), false);
        }
        catch (FluidManipulationBehaviour.ChunkNotLoadedException e2) {
            this.validationFrontier.clear();
            this.validationVisited.clear();
            this.setLongValidationTimer();
            return;
        }
        int maxBlocks = this.maxBlocks();
        if (this.validationVisited.size() >= maxBlocks && this.canDrainInfinitely(this.fluid)) {
            if (!this.infinite) {
                this.reset();
            }
            this.validationFrontier.clear();
            this.setLongValidationTimer();
            return;
        }
        if (!this.validationFrontier.isEmpty()) {
            return;
        }
        if (this.infinite) {
            this.reset();
            return;
        }
        this.validationSet = this.newValidationSet;
        this.newValidationSet = new HashSet<BlockPos>();
        this.validationVisited.clear();
    }

    @Override
    public void reset() {
        super.reset();
        this.fluid = null;
        this.rootPos = null;
        this.queue.clear();
        this.validationSet.clear();
        this.newValidationSet.clear();
        this.validationFrontier.clear();
        this.validationVisited.clear();
        this.blockEntity.sendData();
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    protected boolean isSearching() {
        return !this.frontier.isEmpty();
    }

    public FluidStack getDrainableFluid(BlockPos rootPos) {
        return this.fluid == null || this.isSearching() || !this.pullNext(rootPos, true) ? FluidStack.EMPTY : new FluidStack(this.fluid, 1000);
    }

    static enum FluidBlockType {
        NONE,
        SOURCE,
        FLOWING;

    }
}
