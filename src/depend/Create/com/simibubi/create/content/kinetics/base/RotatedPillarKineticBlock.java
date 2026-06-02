/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.EnumProperty
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
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class RotatedPillarKineticBlock
extends KineticBlock {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;

    public RotatedPillarKineticBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue(AXIS, (Comparable)Direction.Axis.Y));
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        switch (rot) {
            case COUNTERCLOCKWISE_90: 
            case CLOCKWISE_90: {
                switch ((Direction.Axis)state.getValue(AXIS)) {
                    case X: {
                        return (BlockState)state.setValue(AXIS, (Comparable)Direction.Axis.Z);
                    }
                    case Z: {
                        return (BlockState)state.setValue(AXIS, (Comparable)Direction.Axis.X);
                    }
                }
                return state;
            }
        }
        return state;
    }

    public static Direction.Axis getPreferredAxis(BlockPlaceContext context) {
        Direction.Axis prefferedAxis = null;
        for (Direction side : Iterate.directions) {
            BlockState blockState = context.getLevel().getBlockState(context.getClickedPos().relative(side));
            if (!(blockState.getBlock() instanceof IRotate) || !((IRotate)blockState.getBlock()).hasShaftTowards((LevelReader)context.getLevel(), context.getClickedPos().relative(side), blockState, side.getOpposite())) continue;
            if (prefferedAxis != null && prefferedAxis != side.getAxis()) {
                prefferedAxis = null;
                break;
            }
            prefferedAxis = side.getAxis();
        }
        return prefferedAxis;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AXIS});
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction.Axis preferredAxis = RotatedPillarKineticBlock.getPreferredAxis(context);
        if (!(preferredAxis == null || context.getPlayer() != null && context.getPlayer().isShiftKeyDown())) {
            return (BlockState)this.defaultBlockState().setValue(AXIS, (Comparable)preferredAxis);
        }
        return (BlockState)this.defaultBlockState().setValue(AXIS, (Comparable)(preferredAxis != null && context.getPlayer().isShiftKeyDown() ? context.getClickedFace().getAxis() : context.getNearestLookingDirection().getAxis()));
    }
}
