/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.stats.Stats
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BellBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityTicker
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BellAttachType
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.equipment.bell;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.bell.AbstractBellBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractBellBlock<BE extends AbstractBellBlockEntity>
extends BellBlock
implements IBE<BE> {
    public AbstractBellBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext selection) {
        Direction facing = (Direction)state.getValue((Property)FACING);
        return switch ((BellAttachType)state.getValue((Property)ATTACHMENT)) {
            default -> throw new MatchException(null, null);
            case BellAttachType.CEILING -> AllShapes.BELL_CEILING.get(facing);
            case BellAttachType.DOUBLE_WALL -> AllShapes.BELL_DOUBLE_WALL.get(facing);
            case BellAttachType.FLOOR -> AllShapes.BELL_FLOOR.get(facing);
            case BellAttachType.SINGLE_WALL -> AllShapes.BELL_WALL.get(facing);
        };
    }

    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        if (pLevel.isClientSide) {
            return;
        }
        boolean shouldPower = pLevel.hasNeighborSignal(pPos);
        if (shouldPower == (Boolean)pState.getValue((Property)POWERED)) {
            return;
        }
        pLevel.setBlock(pPos, (BlockState)pState.setValue((Property)POWERED, (Comparable)Boolean.valueOf(shouldPower)), 3);
        if (!shouldPower) {
            return;
        }
        Direction facing = (Direction)pState.getValue((Property)FACING);
        BellAttachType type = (BellAttachType)pState.getValue((Property)ATTACHMENT);
        this.ring(pLevel, pPos, type == BellAttachType.CEILING || type == BellAttachType.FLOOR ? facing : facing.getClockWise(), null);
    }

    public boolean onHit(Level world, BlockState state, BlockHitResult hit, @Nullable Player player, boolean flag) {
        BlockPos pos = hit.getBlockPos();
        Direction direction = hit.getDirection();
        if (direction == null) {
            direction = (Direction)world.getBlockState(pos).getValue((Property)FACING);
        }
        if (!this.canRingFrom(state, direction, hit.getLocation().y - (double)pos.getY())) {
            return false;
        }
        return this.ring(world, pos, direction, player);
    }

    protected boolean ring(Level world, BlockPos pos, Direction direction, Player player) {
        AbstractBellBlockEntity be = (AbstractBellBlockEntity)this.getBlockEntity((BlockGetter)world, pos);
        if (world.isClientSide) {
            return true;
        }
        if (be == null || !be.ring(world, pos, direction)) {
            return false;
        }
        this.playSound(world, pos);
        if (player != null) {
            player.awardStat(Stats.BELL_RING);
        }
        return true;
    }

    public boolean canRingFrom(BlockState state, Direction hitDir, double heightChange) {
        if (hitDir.getAxis() == Direction.Axis.Y) {
            return false;
        }
        if (heightChange > 0.8124) {
            return false;
        }
        Direction direction = (Direction)state.getValue((Property)FACING);
        BellAttachType bellAttachment = (BellAttachType)state.getValue((Property)ATTACHMENT);
        switch (bellAttachment) {
            case CEILING: 
            case FLOOR: {
                return direction.getAxis() == hitDir.getAxis();
            }
            case DOUBLE_WALL: 
            case SINGLE_WALL: {
                return direction.getAxis() != hitDir.getAxis();
            }
        }
        return false;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos p_152198_, BlockState p_152199_) {
        return IBE.super.newBlockEntity(p_152198_, p_152199_);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152194_, BlockState p_152195_, BlockEntityType<T> p_152196_) {
        return IBE.super.getTicker(p_152194_, p_152195_, p_152196_);
    }

    public abstract void playSound(Level var1, BlockPos var2);
}
