/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.levelWrappers.WrappedLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.simibubi.create.content.kinetics.waterwheel;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.waterwheel.WaterWheelBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.levelWrappers.WrappedLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class WaterWheelBlock
extends DirectionalKineticBlock
implements IBE<WaterWheelBlockEntity> {
    public WaterWheelBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        for (Direction direction : Iterate.directions) {
            BlockPos neighbourPos = pos.relative(direction);
            BlockState neighbourState = worldIn.getBlockState(neighbourPos);
            if (!AllBlocks.WATER_WHEEL.has(neighbourState)) continue;
            Direction.Axis axis = ((Direction)state.getValue((Property)FACING)).getAxis();
            if (((Direction)neighbourState.getValue((Property)FACING)).getAxis() == axis && axis == direction.getAxis()) continue;
            return false;
        }
        return true;
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return this.onBlockEntityUseItemOn((BlockGetter)level, pos, wwt -> wwt.applyMaterialIfValid(stack));
    }

    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (worldIn instanceof WrappedLevel) {
            return stateIn;
        }
        if (worldIn.isClientSide()) {
            return stateIn;
        }
        if (!worldIn.getBlockTicks().hasScheduledTick(currentPos, (Object)this)) {
            worldIn.scheduleTick(currentPos, (Block)this, 1);
        }
        return stateIn;
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, worldIn, pos, oldState, isMoving);
        if (worldIn.isClientSide()) {
            return;
        }
        if (!worldIn.getBlockTicks().hasScheduledTick(pos, (Object)this)) {
            worldIn.scheduleTick(pos, (Block)this, 1);
        }
    }

    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        this.withBlockEntityDo((BlockGetter)pLevel, pPos, WaterWheelBlockEntity::determineAndApplyFlowScore);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        state.setValue((Property)FACING, (Comparable)Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)((Direction)state.getValue((Property)FACING)).getAxis()));
        return state;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return ((Direction)state.getValue((Property)FACING)).getAxis() == face.getAxis();
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return ((Direction)state.getValue((Property)FACING)).getAxis();
    }

    @Override
    public float getParticleTargetRadius() {
        return 1.125f;
    }

    @Override
    public float getParticleInitialRadius() {
        return 1.0f;
    }

    @Override
    public boolean hideStressImpact() {
        return true;
    }

    @Override
    public Class<WaterWheelBlockEntity> getBlockEntityClass() {
        return WaterWheelBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends WaterWheelBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.WATER_WHEEL.get();
    }

    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return false;
    }
}
