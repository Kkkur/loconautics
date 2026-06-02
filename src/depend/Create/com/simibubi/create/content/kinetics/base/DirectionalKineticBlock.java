/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.Direction
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.kinetics.base;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class DirectionalKineticBlock
extends KineticBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public DirectionalKineticBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING});
        super.createBlockStateDefinition(builder);
    }

    public Direction getPreferredFacing(BlockPlaceContext context) {
        Direction prefferedSide = null;
        for (Direction side : Iterate.directions) {
            BlockState blockState = context.getLevel().getBlockState(context.getClickedPos().relative(side));
            if (!(blockState.getBlock() instanceof IRotate) || !((IRotate)blockState.getBlock()).hasShaftTowards((LevelReader)context.getLevel(), context.getClickedPos().relative(side), blockState, side.getOpposite())) continue;
            if (prefferedSide != null && prefferedSide.getAxis() != side.getAxis()) {
                prefferedSide = null;
                break;
            }
            prefferedSide = side;
        }
        return prefferedSide;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferred = this.getPreferredFacing(context);
        if (preferred == null || context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            Direction nearestLookingDirection = context.getNearestLookingDirection();
            return (BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)(context.getPlayer() != null && context.getPlayer().isShiftKeyDown() ? nearestLookingDirection : nearestLookingDirection.getOpposite()));
        }
        return (BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)preferred.getOpposite());
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return (BlockState)state.setValue((Property)FACING, (Comparable)rot.rotate((Direction)state.getValue((Property)FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation((Direction)state.getValue((Property)FACING)));
    }
}
