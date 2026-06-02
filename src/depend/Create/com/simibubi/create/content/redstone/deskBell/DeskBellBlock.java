/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.redstone.deskBell;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.redstone.deskBell.DeskBellBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class DeskBellBlock
extends WrenchableDirectionalBlock
implements ProperWaterloggedBlock,
IBE<DeskBellBlockEntity> {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public DeskBellBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)Direction.UP)).setValue((Property)POWERED, (Comparable)Boolean.valueOf(false))).setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.withWater((BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)context.getClickedFace()), context);
    }

    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        this.updateWater(pLevel, pState, pPos);
        return pState;
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AllShapes.DESK_BELL.get((Direction)pState.getValue((Property)FACING));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition((StateDefinition.Builder<Block, BlockState>)builder.add(new Property[]{POWERED, WATERLOGGED}));
    }

    public FluidState getFluidState(BlockState pState) {
        return this.fluidState(pState);
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        this.playSound(player, (LevelAccessor)level, pos);
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        level.setBlock(pos, (BlockState)state.setValue((Property)POWERED, (Comparable)Boolean.valueOf(true)), 3);
        this.updateNeighbours(state, level, pos);
        this.withBlockEntityDo((BlockGetter)level, pos, DeskBellBlockEntity::ding);
        return InteractionResult.SUCCESS;
    }

    public void playSound(@Nullable Player pPlayer, LevelAccessor pLevel, BlockPos pPos) {
        if (pLevel instanceof Level) {
            Level level = (Level)pLevel;
            AllSoundEvents.DESK_BELL_USE.play(level, pPlayer, (Vec3i)pPos);
        }
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pIsMoving && !pState.is(pNewState.getBlock()) && ((Boolean)pState.getValue((Property)POWERED)).booleanValue()) {
            this.updateNeighbours(pState, pLevel, pPos);
        }
        IBE.onRemove(pState, pLevel, pPos, pNewState);
    }

    public int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        return (Boolean)pBlockState.getValue((Property)POWERED) != false ? 15 : 0;
    }

    public int getDirectSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        return (Boolean)pBlockState.getValue((Property)POWERED) != false && this.getConnectedDirection(pBlockState) == pSide ? 15 : 0;
    }

    public boolean isSignalSource(BlockState pState) {
        return true;
    }

    public void unPress(BlockState pState, Level pLevel, BlockPos pPos) {
        pLevel.setBlock(pPos, (BlockState)pState.setValue((Property)POWERED, (Comparable)Boolean.valueOf(false)), 3);
        this.updateNeighbours(pState, pLevel, pPos);
    }

    protected void updateNeighbours(BlockState pState, Level pLevel, BlockPos pPos) {
        pLevel.updateNeighborsAt(pPos, (Block)this);
        pLevel.updateNeighborsAt(pPos.relative(this.getConnectedDirection(pState).getOpposite()), (Block)this);
    }

    private Direction getConnectedDirection(BlockState pState) {
        return pState.getOptionalValue((Property)FACING).orElse(Direction.UP);
    }

    @Override
    public Class<DeskBellBlockEntity> getBlockEntityClass() {
        return DeskBellBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends DeskBellBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.DESK_BELL.get();
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}
