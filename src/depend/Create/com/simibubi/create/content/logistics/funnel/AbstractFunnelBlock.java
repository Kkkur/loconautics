/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.logistics.funnel;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractFunnelBlock
extends Block
implements IBE<FunnelBlockEntity>,
IWrenchable,
ProperWaterloggedBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    protected AbstractFunnelBlock(BlockBehaviour.Properties p_i48377_1_) {
        super(p_i48377_1_);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue((Property)POWERED, (Comparable)Boolean.valueOf(false))).setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.withWater((BlockState)this.defaultBlockState().setValue((Property)POWERED, (Comparable)Boolean.valueOf(context.getLevel().hasNeighborSignal(context.getClickedPos()))), context);
    }

    public FluidState getFluidState(BlockState pState) {
        return this.fluidState(pState);
    }

    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        this.updateWater(pLevel, pState, pCurrentPos);
        return pState;
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(new Property[]{POWERED, WATERLOGGED}));
    }

    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide) {
            return;
        }
        InvManipulationBehaviour behaviour = BlockEntityBehaviour.get((BlockGetter)level, pos, InvManipulationBehaviour.TYPE);
        if (behaviour != null) {
            behaviour.onNeighborChanged(fromPos);
        }
        if (!level.getBlockTicks().willTickThisTick(pos, (Object)this)) {
            level.scheduleTick(pos, (Block)this, 1);
        }
    }

    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource r) {
        boolean previouslyPowered = (Boolean)state.getValue((Property)POWERED);
        if (previouslyPowered != worldIn.hasNeighborSignal(pos)) {
            worldIn.setBlock(pos, (BlockState)state.cycle((Property)POWERED), 2);
        }
    }

    public static ItemStack tryInsert(Level worldIn, BlockPos pos, ItemStack toInsert, boolean simulate) {
        BlockEntity blockEntity;
        FilteringBehaviour filter = BlockEntityBehaviour.get((BlockGetter)worldIn, pos, FilteringBehaviour.TYPE);
        InvManipulationBehaviour inserter = BlockEntityBehaviour.get((BlockGetter)worldIn, pos, InvManipulationBehaviour.TYPE);
        if (inserter == null) {
            return toInsert;
        }
        if (filter != null && !filter.test(toInsert)) {
            return toInsert;
        }
        if (simulate) {
            inserter.simulate();
        }
        ItemStack insert = inserter.insert(toInsert);
        if (!simulate && insert.getCount() != toInsert.getCount() && (blockEntity = worldIn.getBlockEntity(pos)) instanceof FunnelBlockEntity) {
            FunnelBlockEntity funnelBlockEntity = (FunnelBlockEntity)blockEntity;
            funnelBlockEntity.onTransfer(toInsert);
            if (funnelBlockEntity.hasFlap()) {
                funnelBlockEntity.flap(true);
            }
        }
        return insert;
    }

    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        Block block = world.getBlockState(pos.relative(AbstractFunnelBlock.getFunnelFacing(state).getOpposite())).getBlock();
        return !(block instanceof AbstractFunnelBlock);
    }

    @Nullable
    public static boolean isFunnel(BlockState state) {
        return state.getBlock() instanceof AbstractFunnelBlock;
    }

    @Nullable
    public static Direction getFunnelFacing(BlockState state) {
        if (!(state.getBlock() instanceof AbstractFunnelBlock)) {
            return null;
        }
        return ((AbstractFunnelBlock)state.getBlock()).getFacing(state);
    }

    protected abstract Direction getFacing(BlockState var1);

    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock() && !AbstractFunnelBlock.isFunnel(newState) || !newState.hasBlockEntity()) {
            IBE.onRemove(state, world, pos, newState);
        }
    }

    @Override
    public Class<FunnelBlockEntity> getBlockEntityClass() {
        return FunnelBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FunnelBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.FUNNEL.get();
    }
}
