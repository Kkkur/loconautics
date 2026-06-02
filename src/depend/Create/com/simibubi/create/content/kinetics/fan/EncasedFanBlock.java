/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.levelWrappers.WrappedLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.kinetics.fan;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import com.simibubi.create.content.logistics.chute.AbstractChuteBlock;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.levelWrappers.WrappedLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class EncasedFanBlock
extends DirectionalKineticBlock
implements IBE<EncasedFanBlockEntity> {
    public EncasedFanBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, worldIn, pos, oldState, isMoving);
        this.blockUpdate(state, (LevelAccessor)worldIn, pos);
    }

    @Override
    public void updateIndirectNeighbourShapes(BlockState stateIn, LevelAccessor worldIn, BlockPos pos, int flags, int count) {
        super.updateIndirectNeighbourShapes(stateIn, worldIn, pos, flags, count);
        this.blockUpdate(stateIn, worldIn, pos);
    }

    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        this.blockUpdate(state, (LevelAccessor)worldIn, pos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction face = context.getClickedFace();
        BlockState placedOn = world.getBlockState(pos.relative(face.getOpposite()));
        BlockState placedOnOpposite = world.getBlockState(pos.relative(face));
        if (AbstractChuteBlock.isChute(placedOn)) {
            return (BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)face.getOpposite());
        }
        if (AbstractChuteBlock.isChute(placedOnOpposite)) {
            return (BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)face);
        }
        Direction preferredFacing = this.getPreferredFacing(context);
        if (preferredFacing == null) {
            preferredFacing = context.getNearestLookingDirection();
        }
        return (BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)(context.getPlayer() != null && context.getPlayer().isShiftKeyDown() ? preferredFacing : preferredFacing.getOpposite()));
    }

    protected void blockUpdate(BlockState state, LevelAccessor worldIn, BlockPos pos) {
        if (worldIn instanceof WrappedLevel) {
            return;
        }
        this.notifyFanBlockEntity(worldIn, pos);
    }

    protected void notifyFanBlockEntity(LevelAccessor world, BlockPos pos) {
        this.withBlockEntityDo((BlockGetter)world, pos, EncasedFanBlockEntity::blockInFrontChanged);
    }

    @Override
    public BlockState updateAfterWrenched(BlockState newState, UseOnContext context) {
        this.blockUpdate(newState, (LevelAccessor)context.getLevel(), context.getClickedPos());
        return newState;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return ((Direction)state.getValue((Property)FACING)).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == ((Direction)state.getValue((Property)FACING)).getOpposite();
    }

    @Override
    public Class<EncasedFanBlockEntity> getBlockEntityClass() {
        return EncasedFanBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends EncasedFanBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.ENCASED_FAN.get();
    }
}
