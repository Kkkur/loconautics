/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.fluids.hosePulley;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.fluids.hosePulley.HosePulleyBlockEntity;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class HosePulleyBlock
extends HorizontalKineticBlock
implements IBE<HosePulleyBlockEntity> {
    public HosePulleyBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return ((Direction)state.getValue(HORIZONTAL_FACING)).getClockWise().getAxis();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferredHorizontalFacing = this.getPreferredHorizontalFacing(context);
        return (BlockState)this.defaultBlockState().setValue(HORIZONTAL_FACING, (Comparable)(preferredHorizontalFacing != null ? preferredHorizontalFacing.getCounterClockWise() : context.getHorizontalDirection().getOpposite()));
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return ((Direction)state.getValue(HORIZONTAL_FACING)).getClockWise() == face;
    }

    public static boolean hasPipeTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return ((Direction)state.getValue(HORIZONTAL_FACING)).getCounterClockWise() == face;
    }

    @Override
    public Direction getPreferredHorizontalFacing(BlockPlaceContext context) {
        Direction fromParent = super.getPreferredHorizontalFacing(context);
        if (fromParent != null) {
            return fromParent;
        }
        Direction prefferedSide = null;
        for (Direction facing : Iterate.horizontalDirections) {
            BlockPos pos = context.getClickedPos().relative(facing);
            BlockState blockState = context.getLevel().getBlockState(pos);
            if (!FluidPipeBlock.canConnectTo((BlockAndTintGetter)context.getLevel(), pos, blockState, facing)) continue;
            if (prefferedSide != null && prefferedSide.getAxis() != facing.getAxis()) {
                prefferedSide = null;
                break;
            }
            prefferedSide = facing;
        }
        return prefferedSide == null ? null : prefferedSide.getOpposite();
    }

    @Override
    public Class<HosePulleyBlockEntity> getBlockEntityClass() {
        return HosePulleyBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends HosePulleyBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.HOSE_PULLEY.get();
    }
}
