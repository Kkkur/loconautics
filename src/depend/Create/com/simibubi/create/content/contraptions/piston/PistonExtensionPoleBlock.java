/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementHelpers
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.DirectionalBlock
 *  net.minecraft.world.level.block.SimpleWaterloggedBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
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
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.contraptions.piston;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.contraptions.piston.MechanicalPistonBlock;
import com.simibubi.create.content.contraptions.piston.MechanicalPistonBlockEntity;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import com.simibubi.create.foundation.placement.PoleHelper;
import java.util.function.Predicate;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PistonExtensionPoleBlock
extends WrenchableDirectionalBlock
implements IWrenchable,
SimpleWaterloggedBlock {
    private static final int placementHelperId = PlacementHelpers.register((IPlacementHelper)PlacementHelper.get());

    public PistonExtensionPoleBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)Direction.UP)).setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.NORMAL;
    }

    public BlockState playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        Direction.Axis axis = ((Direction)state.getValue((Property)FACING)).getAxis();
        Direction direction = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis);
        BlockPos pistonHead = null;
        BlockPos pistonBase = null;
        int[] nArray = new int[]{1, -1};
        int n = nArray.length;
        block0: for (int i = 0; i < n; ++i) {
            int modifier;
            int offset = modifier = nArray[i];
            while (modifier * offset < MechanicalPistonBlock.maxAllowedPistonPoles()) {
                BlockPos currentPos = pos.relative(direction, offset);
                BlockState block = worldIn.getBlockState(currentPos);
                if (!MechanicalPistonBlock.isExtensionPole(block) || axis != ((Direction)block.getValue((Property)FACING)).getAxis()) {
                    if (MechanicalPistonBlock.isPiston(block) && ((Direction)block.getValue((Property)BlockStateProperties.FACING)).getAxis() == axis) {
                        pistonBase = currentPos;
                    }
                    if (!MechanicalPistonBlock.isPistonHead(block) || ((Direction)block.getValue((Property)BlockStateProperties.FACING)).getAxis() != axis) continue block0;
                    pistonHead = currentPos;
                    continue block0;
                }
                offset += modifier;
            }
        }
        if (pistonHead != null && pistonBase != null && worldIn.getBlockState(pistonHead).getValue((Property)BlockStateProperties.FACING) == worldIn.getBlockState(pistonBase).getValue((Property)BlockStateProperties.FACING)) {
            BlockPos basePos = pistonBase;
            BlockPos.betweenClosedStream(pistonBase, pistonHead).filter(p -> !p.equals((Object)pos) && !p.equals((Object)basePos)).forEach(p -> worldIn.destroyBlock(p, !player.isCreative()));
            worldIn.setBlockAndUpdate(basePos, (BlockState)worldIn.getBlockState(basePos).setValue(MechanicalPistonBlock.STATE, (Comparable)((Object)MechanicalPistonBlock.PistonState.RETRACTED)));
            BlockEntity be = worldIn.getBlockEntity(basePos);
            if (be instanceof MechanicalPistonBlockEntity) {
                MechanicalPistonBlockEntity baseBE = (MechanicalPistonBlockEntity)be;
                baseBE.offset = 0.0f;
                baseBE.onLengthBroken();
            }
        }
        return super.playerWillDestroy(worldIn, pos, state, player);
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AllShapes.FOUR_VOXEL_POLE.get(((Direction)state.getValue((Property)FACING)).getAxis());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState FluidState2 = context.getLevel().getFluidState(context.getClickedPos());
        return (BlockState)((BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)context.getClickedFace().getOpposite())).setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(FluidState2.getType() == Fluids.WATER));
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        IPlacementHelper placementHelper = PlacementHelpers.get((int)placementHelperId);
        if (placementHelper.matchesItem(stack) && !player.isShiftKeyDown()) {
            return placementHelper.getOffset(player, level, state, pos, hitResult).placeInWorld(level, (BlockItem)stack.getItem(), player, hand, hitResult);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public FluidState getFluidState(BlockState state) {
        return (Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED) != false ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
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

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @MethodsReturnNonnullByDefault
    public static class PlacementHelper
    extends PoleHelper<Direction> {
        private static final PlacementHelper instance = new PlacementHelper();

        public static PlacementHelper get() {
            return instance;
        }

        private PlacementHelper() {
            super(arg_0 -> AllBlocks.PISTON_EXTENSION_POLE.has(arg_0), state -> ((Direction)state.getValue((Property)DirectionalBlock.FACING)).getAxis(), DirectionalBlock.FACING);
        }

        public Predicate<ItemStack> getItemPredicate() {
            return arg_0 -> AllBlocks.PISTON_EXTENSION_POLE.isIn(arg_0);
        }
    }
}
