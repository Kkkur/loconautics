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
 *  net.minecraft.core.Vec3i
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.DoorBlock
 *  net.minecraft.world.level.block.LiquidBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.ticks.LevelTickAccess
 *  net.minecraft.world.ticks.LevelTicks
 */
package com.simibubi.create.content.fluids.transfer;

import com.simibubi.create.content.fluids.transfer.FluidManipulationBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
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
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.LevelTicks;

public class FluidFillingBehaviour
extends FluidManipulationBehaviour {
    public static final BehaviourType<FluidFillingBehaviour> TYPE = new BehaviourType();
    PriorityQueue<FluidManipulationBehaviour.BlockPosEntry> queue = new ObjectHeapPriorityQueue((p, p2) -> -this.comparePositions((FluidManipulationBehaviour.BlockPosEntry)p, (FluidManipulationBehaviour.BlockPosEntry)p2));
    List<FluidManipulationBehaviour.BlockPosEntry> infinityCheckFrontier;
    Set<BlockPos> infinityCheckVisited;

    public FluidFillingBehaviour(SmartBlockEntity be) {
        super(be);
        this.revalidateIn = 1;
        this.infinityCheckFrontier = new ArrayList<FluidManipulationBehaviour.BlockPosEntry>();
        this.infinityCheckVisited = new HashSet<BlockPos>();
    }

    @Override
    public void tick() {
        Fluid fluid;
        super.tick();
        if (!this.infinityCheckFrontier.isEmpty() && this.rootPos != null && (fluid = this.getWorld().getFluidState(this.rootPos).getType()) != Fluids.EMPTY) {
            this.continueValidation(fluid);
        }
        if (this.revalidateIn > 0) {
            --this.revalidateIn;
        }
    }

    protected void continueValidation(Fluid fluid) {
        try {
            this.search(fluid, this.infinityCheckFrontier, this.infinityCheckVisited, (p, d) -> this.infinityCheckFrontier.add(new FluidManipulationBehaviour.BlockPosEntry((BlockPos)p, (int)d)), true);
        }
        catch (FluidManipulationBehaviour.ChunkNotLoadedException e) {
            this.infinityCheckFrontier.clear();
            this.infinityCheckVisited.clear();
            this.setLongValidationTimer();
            return;
        }
        int maxBlocks = this.maxBlocks();
        if (this.infinityCheckVisited.size() >= maxBlocks && maxBlocks != -1 && !this.fillInfinite()) {
            if (!this.infinite) {
                this.reset();
                this.infinite = true;
                this.blockEntity.sendData();
            }
            this.infinityCheckFrontier.clear();
            this.setLongValidationTimer();
            return;
        }
        if (!this.infinityCheckFrontier.isEmpty()) {
            return;
        }
        if (this.infinite) {
            this.reset();
            return;
        }
        this.infinityCheckVisited.clear();
    }

    public boolean tryDeposit(Fluid fluid, BlockPos root, boolean simulate) {
        if (!Objects.equals(root, this.rootPos)) {
            this.reset();
            this.rootPos = root;
            this.queue.enqueue((Object)new FluidManipulationBehaviour.BlockPosEntry(root, 0));
            this.affectedArea = BoundingBox.fromCorners((Vec3i)this.rootPos, (Vec3i)this.rootPos);
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
        if (this.revalidateIn == 0) {
            this.visited.clear();
            this.infinityCheckFrontier.clear();
            this.infinityCheckVisited.clear();
            this.infinityCheckFrontier.add(new FluidManipulationBehaviour.BlockPosEntry(root, 0));
            this.setValidationTimer();
            this.softReset(root);
        }
        Level world = this.getWorld();
        int maxRange = this.maxRange();
        int maxRangeSq = maxRange * maxRange;
        int maxBlocks = this.maxBlocks();
        boolean evaporate = world.dimensionType().ultraWarm() && FluidHelper.isTag(fluid, (TagKey<Fluid>)FluidTags.WATER);
        boolean canPlaceSources = (Boolean)AllConfigs.server().fluids.fluidFillPlaceFluidSourceBlocks.get();
        if (!this.fillInfinite() && this.infinite || evaporate || !canPlaceSources) {
            FluidState fluidState = world.getFluidState(this.rootPos);
            boolean equivalentTo = fluidState.getType().isSame(fluid);
            if (!equivalentTo && !evaporate && canPlaceSources) {
                return false;
            }
            if (simulate) {
                return true;
            }
            this.playEffect(world, root, fluid, false);
            if (evaporate) {
                int i = root.getX();
                int j = root.getY();
                int k = root.getZ();
                world.playSound(null, (double)i, (double)j, (double)k, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + (world.random.nextFloat() - world.random.nextFloat()) * 0.8f);
            } else if (!canPlaceSources) {
                this.blockEntity.award(AllAdvancements.HOSE_PULLEY);
            }
            return true;
        }
        boolean success = false;
        for (int i = 0; !success && !this.queue.isEmpty() && i < 1024; ++i) {
            SpaceType spaceType;
            FluidManipulationBehaviour.BlockPosEntry entry = (FluidManipulationBehaviour.BlockPosEntry)this.queue.first();
            BlockPos currentPos = entry.pos();
            if (this.visited.contains(currentPos)) {
                this.queue.dequeue();
                continue;
            }
            if (!simulate) {
                this.visited.add(currentPos);
            }
            if (this.visited.size() >= maxBlocks && maxBlocks != -1) {
                this.infinite = true;
                if (!this.fillInfinite()) {
                    this.visited.clear();
                    this.queue.clear();
                    return false;
                }
            }
            if ((spaceType = this.getAtPos(world, currentPos, fluid)) == SpaceType.BLOCKING) continue;
            if (spaceType == SpaceType.FILLABLE) {
                success = true;
                if (!simulate) {
                    LevelTickAccess pendingFluidTicks;
                    this.playEffect(world, currentPos, fluid, false);
                    BlockState blockState = world.getBlockState(currentPos);
                    if (blockState.hasProperty((Property)BlockStateProperties.WATERLOGGED) && fluid.isSame((Fluid)Fluids.WATER)) {
                        if (!this.blockEntity.isVirtual()) {
                            world.setBlock(currentPos, this.updatePostWaterlogging((BlockState)blockState.setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(true))), 18);
                        }
                    } else {
                        this.replaceBlock(world, currentPos, blockState);
                        if (!this.blockEntity.isVirtual()) {
                            world.setBlock(currentPos, FluidHelper.convertToStill(fluid).defaultFluidState().createLegacyBlock(), 18);
                        }
                    }
                    if ((pendingFluidTicks = world.getFluidTicks()) instanceof LevelTicks) {
                        LevelTicks serverTickList = (LevelTicks)pendingFluidTicks;
                        serverTickList.clearArea(new BoundingBox(currentPos));
                    }
                    this.affectedArea = BBHelper.encapsulate((BoundingBox)this.affectedArea, (BlockPos)currentPos);
                }
            }
            if (simulate && success) {
                return true;
            }
            this.visited.add(currentPos);
            this.queue.dequeue();
            for (Direction side : Iterate.directions) {
                SpaceType nextSpaceType;
                BlockPos offsetPos;
                if (side == Direction.UP || this.visited.contains(offsetPos = currentPos.relative(side)) || offsetPos.distSqr((Vec3i)this.rootPos) > (double)maxRangeSq || (nextSpaceType = this.getAtPos(world, offsetPos, fluid)) == SpaceType.BLOCKING) continue;
                this.queue.enqueue((Object)new FluidManipulationBehaviour.BlockPosEntry(offsetPos, entry.distance() + 1));
            }
        }
        if (!simulate && success) {
            this.blockEntity.award(AllAdvancements.HOSE_PULLEY);
        }
        return success;
    }

    protected void softReset(BlockPos root) {
        this.visited.clear();
        this.queue.clear();
        this.queue.enqueue((Object)new FluidManipulationBehaviour.BlockPosEntry(root, 0));
        this.infinite = false;
        this.setValidationTimer();
        this.blockEntity.sendData();
    }

    protected SpaceType getAtPos(Level world, BlockPos pos, Fluid toFill) {
        BlockState blockState = world.getBlockState(pos);
        FluidState fluidState = blockState.getFluidState();
        if (blockState.hasProperty((Property)BlockStateProperties.WATERLOGGED)) {
            return toFill.isSame((Fluid)Fluids.WATER) ? (((Boolean)blockState.getValue((Property)BlockStateProperties.WATERLOGGED)).booleanValue() ? SpaceType.FILLED : SpaceType.FILLABLE) : SpaceType.BLOCKING;
        }
        if (blockState.getBlock() instanceof LiquidBlock) {
            return (Integer)blockState.getValue((Property)LiquidBlock.LEVEL) == 0 ? (toFill.isSame(fluidState.getType()) ? SpaceType.FILLED : SpaceType.BLOCKING) : SpaceType.FILLABLE;
        }
        if (fluidState.getType() != Fluids.EMPTY && blockState.getCollisionShape((BlockGetter)this.getWorld(), pos, CollisionContext.empty()).isEmpty()) {
            return toFill.isSame(fluidState.getType()) ? SpaceType.FILLED : SpaceType.BLOCKING;
        }
        return this.canBeReplacedByFluid((BlockGetter)world, pos, blockState) ? SpaceType.FILLABLE : SpaceType.BLOCKING;
    }

    protected void replaceBlock(Level world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropResources((BlockState)state, (LevelAccessor)world, (BlockPos)pos, (BlockEntity)blockEntity);
    }

    protected boolean canBeReplacedByFluid(BlockGetter world, BlockPos pos, BlockState pState) {
        Block block = pState.getBlock();
        if (!(block instanceof DoorBlock || pState.is(BlockTags.ALL_SIGNS) || pState.is(Blocks.LADDER) || pState.is(Blocks.SUGAR_CANE) || pState.is(Blocks.BUBBLE_COLUMN))) {
            if (!(pState.is(Blocks.NETHER_PORTAL) || pState.is(Blocks.END_PORTAL) || pState.is(Blocks.END_GATEWAY) || pState.is(Blocks.STRUCTURE_VOID))) {
                return !pState.blocksMotion();
            }
            return false;
        }
        return false;
    }

    protected BlockState updatePostWaterlogging(BlockState state) {
        if (state.hasProperty((Property)BlockStateProperties.LIT)) {
            state = (BlockState)state.setValue((Property)BlockStateProperties.LIT, (Comparable)Boolean.valueOf(false));
        }
        return state;
    }

    @Override
    public void reset() {
        super.reset();
        this.queue.clear();
        this.infinityCheckFrontier.clear();
        this.infinityCheckVisited.clear();
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    static enum SpaceType {
        FILLABLE,
        FILLED,
        BLOCKING;

    }
}
