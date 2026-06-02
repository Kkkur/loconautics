/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.kinetics.clock;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.clock.CuckooClockBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CuckooClockBlock
extends HorizontalKineticBlock
implements IBE<CuckooClockBlockEntity> {
    private boolean mysterious;

    public static CuckooClockBlock regular(BlockBehaviour.Properties properties) {
        return new CuckooClockBlock(false, properties);
    }

    public static CuckooClockBlock mysterious(BlockBehaviour.Properties properties) {
        return new CuckooClockBlock(true, properties);
    }

    protected CuckooClockBlock(boolean mysterious, BlockBehaviour.Properties properties) {
        super(properties);
        this.mysterious = mysterious;
    }

    public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        return AllShapes.CUCKOO_CLOCK;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferred = this.getPreferredHorizontalFacing(context);
        if (preferred != null) {
            return (BlockState)this.defaultBlockState().setValue(HORIZONTAL_FACING, (Comparable)preferred.getOpposite());
        }
        return (BlockState)this.defaultBlockState().setValue(HORIZONTAL_FACING, (Comparable)context.getHorizontalDirection().getOpposite());
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == ((Direction)state.getValue(HORIZONTAL_FACING)).getOpposite();
    }

    public static boolean containsSurprise(BlockState state) {
        Block block = state.getBlock();
        return block instanceof CuckooClockBlock && ((CuckooClockBlock)block).mysterious;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return ((Direction)state.getValue(HORIZONTAL_FACING)).getAxis();
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public Class<CuckooClockBlockEntity> getBlockEntityClass() {
        return CuckooClockBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CuckooClockBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.CUCKOO_CLOCK.get();
    }
}
