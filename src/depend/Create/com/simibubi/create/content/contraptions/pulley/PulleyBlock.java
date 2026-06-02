/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
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
 *  net.minecraft.world.level.block.SimpleWaterloggedBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.level.material.PushReaction
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.contraptions.pulley;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.contraptions.pulley.PulleyBlockEntity;
import com.simibubi.create.content.kinetics.base.HorizontalAxisKineticBlock;
import com.simibubi.create.foundation.block.IBE;
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
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PulleyBlock
extends HorizontalAxisKineticBlock
implements IBE<PulleyBlockEntity> {
    public PulleyBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    private static void onRopeBroken(Level world, BlockPos pulleyPos) {
        BlockEntity be = world.getBlockEntity(pulleyPos);
        if (be instanceof PulleyBlockEntity) {
            PulleyBlockEntity pulley = (PulleyBlockEntity)be;
            pulley.initialOffset = 0;
            pulley.onLengthBroken();
        }
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, worldIn, pos, newState, isMoving);
        if (state.is(newState.getBlock())) {
            return;
        }
        if (worldIn.isClientSide) {
            return;
        }
        BlockState below = worldIn.getBlockState(pos.below());
        if (below.getBlock() instanceof RopeBlockBase) {
            worldIn.destroyBlock(pos.below(), true);
        }
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!player.mayBuild()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (player.isShiftKeyDown()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (stack.isEmpty()) {
            this.withBlockEntityDo((BlockGetter)level, pos, be -> {
                be.assembleNextTick = true;
            });
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public Class<PulleyBlockEntity> getBlockEntityClass() {
        return PulleyBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PulleyBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.ROPE_PULLEY.get();
    }

    private static class RopeBlockBase
    extends Block
    implements SimpleWaterloggedBlock {
        public RopeBlockBase(BlockBehaviour.Properties properties) {
            super(properties);
            this.registerDefaultState((BlockState)super.defaultBlockState().setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(false)));
        }

        protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
            return false;
        }

        public PushReaction getPistonPushReaction(BlockState state) {
            return PushReaction.BLOCK;
        }

        public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
            return AllBlocks.ROPE_PULLEY.asStack();
        }

        public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
            if (!(isMoving || state.hasProperty((Property)BlockStateProperties.WATERLOGGED) && newState.hasProperty((Property)BlockStateProperties.WATERLOGGED) && state.getValue((Property)BlockStateProperties.WATERLOGGED) != newState.getValue((Property)BlockStateProperties.WATERLOGGED))) {
                PulleyBlock.onRopeBroken(worldIn, pos.above());
                if (!worldIn.isClientSide) {
                    BlockState above = worldIn.getBlockState(pos.above());
                    BlockState below = worldIn.getBlockState(pos.below());
                    if (above.getBlock() instanceof RopeBlockBase) {
                        worldIn.destroyBlock(pos.above(), true);
                    }
                    if (below.getBlock() instanceof RopeBlockBase) {
                        worldIn.destroyBlock(pos.below(), true);
                    }
                }
            }
            if (state.hasBlockEntity() && state.getBlock() != newState.getBlock()) {
                worldIn.removeBlockEntity(pos);
            }
        }

        public FluidState getFluidState(BlockState state) {
            return (Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED) != false ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
        }

        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(new Property[]{BlockStateProperties.WATERLOGGED});
            super.createBlockStateDefinition(builder);
        }

        public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor world, BlockPos pos, BlockPos neighbourPos) {
            if (((Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED)).booleanValue()) {
                world.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay((LevelReader)world));
            }
            return state;
        }

        public BlockState getStateForPlacement(BlockPlaceContext context) {
            FluidState FluidState2 = context.getLevel().getFluidState(context.getClickedPos());
            return (BlockState)super.getStateForPlacement(context).setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(FluidState2.getType() == Fluids.WATER));
        }
    }

    public static class RopeBlock
    extends RopeBlockBase {
        public RopeBlock(BlockBehaviour.Properties properties) {
            super(properties);
        }

        public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
            return AllShapes.FOUR_VOXEL_POLE.get(Direction.UP);
        }
    }

    public static class MagnetBlock
    extends RopeBlockBase {
        public MagnetBlock(BlockBehaviour.Properties properties) {
            super(properties);
        }

        public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
            return AllShapes.PULLEY_MAGNET;
        }
    }
}
