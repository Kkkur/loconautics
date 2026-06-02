/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
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
package com.simibubi.create.content.contraptions.gantry;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.gantry.GantryCarriageBlockEntity;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.gantry.GantryShaftBlock;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public class GantryCarriageBlock
extends DirectionalAxisKineticBlock
implements IBE<GantryCarriageBlockEntity> {
    public GantryCarriageBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        Direction direction = (Direction)state.getValue((Property)FACING);
        BlockState shaft = world.getBlockState(pos.relative(direction.getOpposite()));
        return AllBlocks.GANTRY_SHAFT.has(shaft) && ((Direction)shaft.getValue((Property)GantryShaftBlock.FACING)).getAxis() != direction.getAxis();
    }

    @Override
    public void updateIndirectNeighbourShapes(BlockState stateIn, LevelAccessor worldIn, BlockPos pos, int flags, int count) {
        super.updateIndirectNeighbourShapes(stateIn, worldIn, pos, flags, count);
        this.withBlockEntityDo((BlockGetter)worldIn, pos, GantryCarriageBlockEntity::checkValidGantryShaft);
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, worldIn, pos, oldState, isMoving);
    }

    @Override
    protected Direction getFacingForPlacement(BlockPlaceContext context) {
        return context.getClickedFace();
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!player.mayBuild() || player.isShiftKeyDown()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (stack.isEmpty()) {
            this.withBlockEntityDo((BlockGetter)level, pos, be -> be.checkValidGantryShaft());
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState stateForPlacement = super.getStateForPlacement(context);
        Direction opposite = ((Direction)stateForPlacement.getValue((Property)FACING)).getOpposite();
        return this.cycleAxisIfNecessary(stateForPlacement, opposite, context.getLevel().getBlockState(context.getClickedPos().relative(opposite)));
    }

    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block p_220069_4_, BlockPos updatePos, boolean p_220069_6_) {
        if (updatePos.equals((Object)pos.relative(((Direction)state.getValue((Property)FACING)).getOpposite())) && !this.canSurvive(state, (LevelReader)world, pos)) {
            world.destroyBlock(pos, true);
        }
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, LevelAccessor world, BlockPos pos, BlockPos p_196271_6_) {
        if (state.getValue((Property)FACING) != direction.getOpposite()) {
            return state;
        }
        return this.cycleAxisIfNecessary(state, direction, otherState);
    }

    protected BlockState cycleAxisIfNecessary(BlockState state, Direction direction, BlockState otherState) {
        if (!AllBlocks.GANTRY_SHAFT.has(otherState)) {
            return state;
        }
        if (((Direction)otherState.getValue((Property)GantryShaftBlock.FACING)).getAxis() == direction.getAxis()) {
            return state;
        }
        if (GantryCarriageBlock.isValidGantryShaftAxis(state, otherState)) {
            return state;
        }
        return (BlockState)state.cycle((Property)AXIS_ALONG_FIRST_COORDINATE);
    }

    public static boolean isValidGantryShaftAxis(BlockState pinionState, BlockState gantryState) {
        return GantryCarriageBlock.getValidGantryShaftAxis(pinionState) == ((Direction)gantryState.getValue((Property)GantryShaftBlock.FACING)).getAxis();
    }

    public static Direction.Axis getValidGantryShaftAxis(BlockState state) {
        Block block = state.getBlock();
        if (!(block instanceof GantryCarriageBlock)) {
            return Direction.Axis.Y;
        }
        GantryCarriageBlock block2 = (GantryCarriageBlock)block;
        Direction.Axis rotationAxis = block2.getRotationAxis(state);
        Direction.Axis facingAxis = ((Direction)state.getValue((Property)FACING)).getAxis();
        for (Direction.Axis axis : Iterate.axes) {
            if (axis == rotationAxis || axis == facingAxis) continue;
            return axis;
        }
        return Direction.Axis.Y;
    }

    public static Direction.Axis getValidGantryPinionAxis(BlockState state, Direction.Axis shaftAxis) {
        Direction.Axis facingAxis = ((Direction)state.getValue((Property)FACING)).getAxis();
        for (Direction.Axis axis : Iterate.axes) {
            if (axis == shaftAxis || axis == facingAxis) continue;
            return axis;
        }
        return Direction.Axis.Y;
    }

    @Override
    public Class<GantryCarriageBlockEntity> getBlockEntityClass() {
        return GantryCarriageBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends GantryCarriageBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.GANTRY_PINION.get();
    }
}
