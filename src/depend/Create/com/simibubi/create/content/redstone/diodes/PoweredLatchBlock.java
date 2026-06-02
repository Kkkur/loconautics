/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.RedStoneWireBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.ticks.TickPriority
 */
package com.simibubi.create.content.redstone.diodes;

import com.simibubi.create.content.redstone.diodes.ToggleLatchBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.ticks.TickPriority;

public class PoweredLatchBlock
extends ToggleLatchBlock {
    public static BooleanProperty POWERED_SIDE = BooleanProperty.create((String)"powered_side");

    public PoweredLatchBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)POWERED_SIDE, (Comparable)Boolean.valueOf(false)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition((StateDefinition.Builder<Block, BlockState>)builder.add(new Property[]{POWERED_SIDE}));
    }

    protected void checkTickOnNeighbor(Level worldIn, BlockPos pos, BlockState state) {
        boolean back = (Boolean)state.getValue((Property)POWERED);
        boolean shouldBack = this.shouldTurnOn(worldIn, pos, state);
        boolean side = (Boolean)state.getValue((Property)POWERED_SIDE);
        boolean shouldSide = this.isPoweredOnSides(worldIn, pos, state);
        TickPriority tickpriority = TickPriority.HIGH;
        if (this.shouldPrioritize((BlockGetter)worldIn, pos, state)) {
            tickpriority = TickPriority.EXTREMELY_HIGH;
        } else if (side || back) {
            tickpriority = TickPriority.VERY_HIGH;
        }
        if (worldIn.getBlockTicks().willTickThisTick(pos, (Object)this)) {
            return;
        }
        if (back != shouldBack || side != shouldSide) {
            worldIn.scheduleTick(pos, (Block)this, this.getDelay(state), tickpriority);
        }
    }

    protected boolean isPoweredOnSides(Level worldIn, BlockPos pos, BlockState state) {
        Direction direction = (Direction)state.getValue((Property)FACING);
        Direction left = direction.getClockWise();
        Direction right = direction.getCounterClockWise();
        for (Direction d : new Direction[]{left, right}) {
            BlockPos blockpos = pos.relative(d);
            int i = worldIn.getSignal(blockpos, d);
            if (i > 0) {
                return true;
            }
            BlockState blockstate = worldIn.getBlockState(blockpos);
            if (blockstate.getBlock() != Blocks.REDSTONE_WIRE || (Integer)blockstate.getValue((Property)RedStoneWireBlock.POWER) <= 0) continue;
            return true;
        }
        return false;
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
        boolean back = (Boolean)state.getValue((Property)POWERED);
        boolean shouldBack = this.shouldTurnOn((Level)worldIn, pos, state);
        boolean side = (Boolean)state.getValue((Property)POWERED_SIDE);
        boolean shouldSide = this.isPoweredOnSides((Level)worldIn, pos, state);
        BlockState stateIn = state;
        if (back != shouldBack) {
            state = (BlockState)state.setValue((Property)POWERED, (Comparable)Boolean.valueOf(shouldBack));
            if (shouldBack) {
                state = (BlockState)state.setValue((Property)POWERING, (Comparable)Boolean.valueOf(true));
            } else if (side) {
                state = (BlockState)state.setValue((Property)POWERING, (Comparable)Boolean.valueOf(false));
            }
        }
        if (side != shouldSide) {
            state = (BlockState)state.setValue((Property)POWERED_SIDE, (Comparable)Boolean.valueOf(shouldSide));
            if (shouldSide) {
                state = (BlockState)state.setValue((Property)POWERING, (Comparable)Boolean.valueOf(false));
            } else if (back) {
                state = (BlockState)state.setValue((Property)POWERING, (Comparable)Boolean.valueOf(true));
            }
        }
        if (state != stateIn) {
            worldIn.setBlock(pos, state, 2);
        }
    }

    @Override
    protected ItemInteractionResult activated(Level worldIn, BlockPos pos, BlockState state) {
        if (state.getValue((Property)POWERED) != state.getValue((Property)POWERED_SIDE)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!worldIn.isClientSide) {
            float f = (Boolean)state.getValue((Property)POWERING) == false ? 0.6f : 0.5f;
            worldIn.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3f, f);
            worldIn.setBlock(pos, (BlockState)state.cycle((Property)POWERING), 2);
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        if (side == null) {
            return false;
        }
        return side.getAxis().isHorizontal();
    }
}
