/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementHelpers
 *  net.createmod.catnip.placement.PlacementOffset
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock
 *  net.minecraft.world.level.block.SimpleWaterloggedBlock
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.kinetics.steamEngine;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.content.kinetics.steamEngine.PoweredShaftBlock;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlockEntity;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.BlockHelper;
import java.util.function.Predicate;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class SteamEngineBlock
extends FaceAttachedHorizontalDirectionalBlock
implements SimpleWaterloggedBlock,
IWrenchable,
IBE<SteamEngineBlockEntity> {
    private static final int placementHelperId = PlacementHelpers.register((IPlacementHelper)new PlacementHelper());
    public static final MapCodec<SteamEngineBlock> CODEC = SteamEngineBlock.simpleCodec(SteamEngineBlock::new);

    public SteamEngineBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue((Property)FACE, (Comparable)AttachFace.FLOOR)).setValue((Property)FACING, (Comparable)Direction.NORTH)).setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(new Property[]{FACE, FACING, BlockStateProperties.WATERLOGGED}));
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return SteamEngineBlock.canAttach(pLevel, pPos, SteamEngineBlock.getConnectedDirection(pState).getOpposite());
    }

    public static boolean canAttach(LevelReader pReader, BlockPos pPos, Direction pDirection) {
        BlockPos blockpos = pPos.relative(pDirection);
        return pReader.getBlockState(blockpos).getBlock() instanceof FluidTankBlock;
    }

    public FluidState getFluidState(BlockState state) {
        return (Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED) != false ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        IPlacementHelper placementHelper = PlacementHelpers.get((int)placementHelperId);
        if (placementHelper.matchesItem(stack)) {
            return placementHelper.getOffset(player, level, state, pos, hitResult).placeInWorld(level, (BlockItem)stack.getItem(), player, hand, hitResult);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor world, BlockPos pos, BlockPos neighbourPos) {
        if (((Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED)).booleanValue()) {
            world.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay((LevelReader)world));
        }
        return state;
    }

    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        FluidTankBlock.updateBoilerState(pState, pLevel, pPos.relative(SteamEngineBlock.getFacing(pState).getOpposite()));
        BlockPos shaftPos = SteamEngineBlock.getShaftPos(pState, pPos);
        BlockState shaftState = pLevel.getBlockState(shaftPos);
        if (SteamEngineBlock.isShaftValid(pState, shaftState)) {
            pLevel.setBlock(shaftPos, PoweredShaftBlock.getEquivalent(shaftState), 3);
        }
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!(!pState.hasBlockEntity() || pState.is(pNewState.getBlock()) && pNewState.hasBlockEntity())) {
            pLevel.removeBlockEntity(pPos);
        }
        FluidTankBlock.updateBoilerState(pState, pLevel, pPos.relative(SteamEngineBlock.getFacing(pState).getOpposite()));
        BlockPos shaftPos = SteamEngineBlock.getShaftPos(pState, pPos);
        BlockState shaftState = pLevel.getBlockState(shaftPos);
        if (AllBlocks.POWERED_SHAFT.has(shaftState)) {
            pLevel.scheduleTick(shaftPos, shaftState.getBlock(), 1);
        }
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        AttachFace face = (AttachFace)pState.getValue((Property)FACE);
        Direction direction = (Direction)pState.getValue((Property)FACING);
        return face == AttachFace.CEILING ? AllShapes.STEAM_ENGINE_CEILING.get(direction.getAxis()) : (face == AttachFace.FLOOR ? AllShapes.STEAM_ENGINE.get(direction.getAxis()) : AllShapes.STEAM_ENGINE_WALL.get(direction));
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        FluidState ifluidstate = level.getFluidState(pos);
        BlockState state = super.getStateForPlacement(context);
        if (state == null) {
            return null;
        }
        return (BlockState)state.setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(ifluidstate.getType() == Fluids.WATER));
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    public static Direction getFacing(BlockState sideState) {
        return SteamEngineBlock.getConnectedDirection(sideState);
    }

    public static BlockPos getShaftPos(BlockState sideState, BlockPos pos) {
        return pos.relative(SteamEngineBlock.getConnectedDirection(sideState), 2);
    }

    public static boolean isShaftValid(BlockState state, BlockState shaft) {
        return (AllBlocks.SHAFT.has(shaft) || AllBlocks.POWERED_SHAFT.has(shaft)) && shaft.getValue((Property)ShaftBlock.AXIS) != SteamEngineBlock.getFacing(state).getAxis();
    }

    @Override
    public Class<SteamEngineBlockEntity> getBlockEntityClass() {
        return SteamEngineBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SteamEngineBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.STEAM_ENGINE.get();
    }

    public static Couple<Integer> getSpeedRange() {
        return Couple.create((Object)16, (Object)64);
    }

    public static Direction getConnectedDirection(BlockState state) {
        return FaceAttachedHorizontalDirectionalBlock.getConnectedDirection((BlockState)state);
    }

    @NotNull
    protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @MethodsReturnNonnullByDefault
    private static class PlacementHelper
    implements IPlacementHelper {
        private PlacementHelper() {
        }

        public Predicate<ItemStack> getItemPredicate() {
            return arg_0 -> AllBlocks.SHAFT.isIn(arg_0);
        }

        public Predicate<BlockState> getStatePredicate() {
            return s -> s.getBlock() instanceof SteamEngineBlock;
        }

        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            Direction direction;
            BlockPos shaftPos = SteamEngineBlock.getShaftPos(state, pos);
            BlockState shaft = AllBlocks.SHAFT.getDefaultState();
            Direction[] directionArray = Direction.orderedByNearest((Entity)player);
            int n = directionArray.length;
            for (int i = 0; i < n && !SteamEngineBlock.isShaftValid(state, shaft = (BlockState)shaft.setValue((Property)ShaftBlock.AXIS, (Comparable)(direction = directionArray[i]).getAxis())); ++i) {
            }
            BlockState newState = world.getBlockState(shaftPos);
            if (!newState.canBeReplaced()) {
                return PlacementOffset.fail();
            }
            Direction.Axis axis = (Direction.Axis)shaft.getValue((Property)ShaftBlock.AXIS);
            return PlacementOffset.success((Vec3i)shaftPos, s -> (BlockState)BlockHelper.copyProperties(s, (world.isClientSide ? AllBlocks.SHAFT : AllBlocks.POWERED_SHAFT).getDefaultState()).setValue((Property)PoweredShaftBlock.AXIS, (Comparable)axis));
        }
    }
}
