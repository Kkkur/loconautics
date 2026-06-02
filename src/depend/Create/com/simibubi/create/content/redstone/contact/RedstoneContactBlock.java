/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.SignalGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.redstone.contact;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.elevator.ElevatorColumn;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import com.simibubi.create.foundation.utility.BlockHelper;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RedstoneContactBlock
extends WrenchableDirectionalBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public RedstoneContactBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue((Property)POWERED, (Comparable)Boolean.valueOf(false))).setValue((Property)FACING, (Comparable)Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{POWERED});
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = (BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)context.getNearestLookingDirection().getOpposite());
        Direction placeDirection = context.getClickedFace().getOpposite();
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown() || RedstoneContactBlock.hasValidContact((LevelAccessor)context.getLevel(), context.getClickedPos(), placeDirection)) {
            state = (BlockState)state.setValue((Property)FACING, (Comparable)placeDirection);
        }
        if (RedstoneContactBlock.hasValidContact((LevelAccessor)context.getLevel(), context.getClickedPos(), (Direction)state.getValue((Property)FACING))) {
            state = (BlockState)state.setValue((Property)POWERED, (Comparable)Boolean.valueOf(true));
        }
        return state;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        InteractionResult onWrenched = super.onWrenched(state, context);
        if (onWrenched != InteractionResult.SUCCESS) {
            return onWrenched;
        }
        Level level = context.getLevel();
        if (level.isClientSide()) {
            return onWrenched;
        }
        BlockPos pos = context.getClickedPos();
        state = level.getBlockState(pos);
        Direction facing = (Direction)state.getValue((Property)FACING);
        if (facing.getAxis() == Direction.Axis.Y) {
            return onWrenched;
        }
        if (ElevatorColumn.get((LevelAccessor)level, new ElevatorColumn.ColumnCoords(pos.getX(), pos.getZ(), facing)) == null) {
            return onWrenched;
        }
        level.setBlockAndUpdate(pos, BlockHelper.copyProperties(state, AllBlocks.ELEVATOR_CONTACT.getDefaultState()));
        return onWrenched;
    }

    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (facing != stateIn.getValue((Property)FACING)) {
            return stateIn;
        }
        boolean hasValidContact = RedstoneContactBlock.hasValidContact(worldIn, currentPos, facing);
        if ((Boolean)stateIn.getValue((Property)POWERED) != hasValidContact) {
            return (BlockState)stateIn.setValue((Property)POWERED, (Comparable)Boolean.valueOf(hasValidContact));
        }
        return stateIn;
    }

    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() == this && newState.getBlock() == this && state == newState.cycle((Property)POWERED)) {
            worldIn.updateNeighborsAt(pos, (Block)this);
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
        boolean hasValidContact = RedstoneContactBlock.hasValidContact((LevelAccessor)worldIn, pos, (Direction)state.getValue((Property)FACING));
        if ((Boolean)state.getValue((Property)POWERED) != hasValidContact) {
            worldIn.setBlockAndUpdate(pos, (BlockState)state.setValue((Property)POWERED, (Comparable)Boolean.valueOf(hasValidContact)));
        }
    }

    public static boolean hasValidContact(LevelAccessor world, BlockPos pos, Direction direction) {
        BlockState blockState = world.getBlockState(pos.relative(direction));
        return (AllBlocks.REDSTONE_CONTACT.has(blockState) || AllBlocks.ELEVATOR_CONTACT.has(blockState)) && blockState.getValue((Property)FACING) == direction.getOpposite();
    }

    public boolean shouldCheckWeakPower(BlockState state, SignalGetter level, BlockPos pos, Direction side) {
        return false;
    }

    public boolean isSignalSource(BlockState state) {
        return (Boolean)state.getValue((Property)POWERED);
    }

    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction side) {
        return side != null && state.getValue((Property)FACING) != side.getOpposite();
    }

    public int getSignal(BlockState state, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return (Boolean)state.getValue((Property)POWERED) != false && side != ((Direction)state.getValue((Property)FACING)).getOpposite() ? 15 : 0;
    }
}
