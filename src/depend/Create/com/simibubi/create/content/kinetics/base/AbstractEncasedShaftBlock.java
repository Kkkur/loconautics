/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.SignalGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.PushReaction
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.base;

import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;

@MethodsReturnNonnullByDefault
public abstract class AbstractEncasedShaftBlock
extends RotatedPillarKineticBlock {
    public AbstractEncasedShaftBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    public boolean shouldCheckWeakPower(BlockState state, SignalGetter level, BlockPos pos, Direction side) {
        return false;
    }

    public PushReaction getPistonPushReaction(@Nullable BlockState state) {
        return PushReaction.NORMAL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            return super.getStateForPlacement(context);
        }
        Direction.Axis preferredAxis = AbstractEncasedShaftBlock.getPreferredAxis(context);
        return (BlockState)this.defaultBlockState().setValue((Property)AXIS, (Comparable)(preferredAxis == null ? context.getNearestLookingDirection().getAxis() : preferredAxis));
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue((Property)AXIS);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return (Direction.Axis)state.getValue((Property)AXIS);
    }
}
