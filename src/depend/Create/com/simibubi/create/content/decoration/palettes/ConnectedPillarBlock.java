/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.ticks.LevelTickAccess
 */
package com.simibubi.create.content.decoration.palettes;

import com.simibubi.create.content.decoration.palettes.LayeredBlock;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.ticks.LevelTickAccess;

public class ConnectedPillarBlock
extends LayeredBlock {
    public static final BooleanProperty NORTH = BooleanProperty.create((String)"north");
    public static final BooleanProperty SOUTH = BooleanProperty.create((String)"south");
    public static final BooleanProperty EAST = BooleanProperty.create((String)"east");
    public static final BooleanProperty WEST = BooleanProperty.create((String)"west");

    public ConnectedPillarBlock(BlockBehaviour.Properties p_55926_) {
        super(p_55926_);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue((Property)NORTH, (Comparable)Boolean.valueOf(false))).setValue((Property)WEST, (Comparable)Boolean.valueOf(false))).setValue((Property)EAST, (Comparable)Boolean.valueOf(false))).setValue((Property)SOUTH, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(new Property[]{NORTH, SOUTH, EAST, WEST}));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState state = super.getStateForPlacement(pContext);
        return this.updateColumn(pContext.getLevel(), pContext.getClickedPos(), state, true);
    }

    private BlockState updateColumn(Level level, BlockPos pos, BlockState state, boolean present) {
        BlockPos.MutableBlockPos currentPos = new BlockPos.MutableBlockPos();
        Direction.Axis axis = (Direction.Axis)state.getValue((Property)AXIS);
        for (Direction connection : Iterate.directions) {
            if (connection.getAxis() == axis) continue;
            boolean connect = true;
            block1: for (Direction movement : Iterate.directionsInAxis((Direction.Axis)axis)) {
                currentPos.set((Vec3i)pos);
                for (int i = 0; i < 1000 && level.isLoaded((BlockPos)currentPos); ++i) {
                    BlockState other1 = currentPos.equals((Object)pos) ? state : level.getBlockState((BlockPos)currentPos);
                    BlockState other2 = level.getBlockState(currentPos.relative(connection));
                    boolean col1 = this.canConnect(state, other1);
                    boolean col2 = this.canConnect(state, other2);
                    currentPos.move(movement);
                    if (!col1 && !col2) continue block1;
                    if (col1 && col2) continue;
                    connect = false;
                    break block1;
                }
            }
            state = ConnectedPillarBlock.setConnection(state, connection, connect);
        }
        return state;
    }

    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if (pOldState.getBlock() == this) {
            return;
        }
        LevelTickAccess blockTicks = pLevel.getBlockTicks();
        if (!blockTicks.hasScheduledTick(pPos, (Object)this)) {
            pLevel.scheduleTick(pPos, (Block)this, 1);
        }
    }

    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pState.getBlock() != this) {
            return;
        }
        BlockPos belowPos = pPos.relative(Direction.fromAxisAndDirection((Direction.Axis)((Direction.Axis)pState.getValue((Property)AXIS)), (Direction.AxisDirection)Direction.AxisDirection.NEGATIVE));
        BlockState belowState = pLevel.getBlockState(belowPos);
        if (!this.canConnect(pState, belowState)) {
            pLevel.setBlock(pPos, this.updateColumn((Level)pLevel, pPos, pState, true), 3);
        }
    }

    public BlockState updateShape(BlockState state, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        if (!this.canConnect(state, pNeighborState)) {
            return ConnectedPillarBlock.setConnection(state, pDirection, false);
        }
        if (pDirection.getAxis() == state.getValue((Property)AXIS)) {
            return this.withPropertiesOf(pNeighborState);
        }
        return ConnectedPillarBlock.setConnection(state, pDirection, ConnectedPillarBlock.getConnection(pNeighborState, pDirection.getOpposite()));
    }

    protected boolean canConnect(BlockState state, BlockState other) {
        return other.getBlock() == this && state.getValue((Property)AXIS) == other.getValue((Property)AXIS);
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pIsMoving || pNewState.getBlock() == this) {
            return;
        }
        for (Direction d : Iterate.directionsInAxis((Direction.Axis)((Direction.Axis)pState.getValue((Property)AXIS)))) {
            BlockPos relative = pPos.relative(d);
            BlockState adjacent = pLevel.getBlockState(relative);
            if (!this.canConnect(pState, adjacent)) continue;
            pLevel.setBlock(relative, this.updateColumn(pLevel, relative, adjacent, false), 3);
        }
    }

    public static boolean getConnection(BlockState state, Direction side) {
        BooleanProperty property = ConnectedPillarBlock.connection((Direction.Axis)state.getValue((Property)AXIS), side);
        return property != null && (Boolean)state.getValue((Property)property) != false;
    }

    public static BlockState setConnection(BlockState state, Direction side, boolean connect) {
        BooleanProperty property = ConnectedPillarBlock.connection((Direction.Axis)state.getValue((Property)AXIS), side);
        if (property != null) {
            state = (BlockState)state.setValue((Property)property, (Comparable)Boolean.valueOf(connect));
        }
        return state;
    }

    public static BooleanProperty connection(Direction.Axis axis, Direction side) {
        if (side.getAxis() == axis) {
            return null;
        }
        if (axis == Direction.Axis.X) {
            switch (side) {
                case UP: {
                    return EAST;
                }
                case NORTH: {
                    return NORTH;
                }
                case SOUTH: {
                    return SOUTH;
                }
                case DOWN: {
                    return WEST;
                }
            }
            return null;
        }
        if (axis == Direction.Axis.Y) {
            switch (side) {
                case EAST: {
                    return EAST;
                }
                case NORTH: {
                    return NORTH;
                }
                case SOUTH: {
                    return SOUTH;
                }
                case WEST: {
                    return WEST;
                }
            }
            return null;
        }
        if (axis == Direction.Axis.Z) {
            switch (side) {
                case UP: {
                    return WEST;
                }
                case WEST: {
                    return SOUTH;
                }
                case EAST: {
                    return NORTH;
                }
                case DOWN: {
                    return EAST;
                }
            }
            return null;
        }
        return null;
    }
}
