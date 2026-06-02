/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementHelpers
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.ChainBlock
 *  net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock
 *  net.minecraft.world.level.block.LanternBlock
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.SimpleWaterloggedBlock
 *  net.minecraft.world.level.block.WallBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.decoration.girder;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.decoration.bracket.BracketBlock;
import com.simibubi.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.simibubi.create.content.decoration.girder.GirderEncasedShaftBlock;
import com.simibubi.create.content.decoration.girder.GirderPlacementHelper;
import com.simibubi.create.content.decoration.girder.GirderWrenchBehavior;
import com.simibubi.create.content.decoration.placard.PlacardBlock;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.logistics.chute.AbstractChuteBlock;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlock;
import com.simibubi.create.content.trains.display.FlapDisplayBlock;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GirderBlock
extends Block
implements SimpleWaterloggedBlock,
IWrenchable {
    private static final int placementHelperId = PlacementHelpers.register((IPlacementHelper)new GirderPlacementHelper());
    public static final BooleanProperty X = BooleanProperty.create((String)"x");
    public static final BooleanProperty Z = BooleanProperty.create((String)"z");
    public static final BooleanProperty TOP = BooleanProperty.create((String)"top");
    public static final BooleanProperty BOTTOM = BooleanProperty.create((String)"bottom");
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;

    public GirderBlock(BlockBehaviour.Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(false))).setValue(AXIS, (Comparable)Direction.Axis.Y)).setValue((Property)TOP, (Comparable)Boolean.valueOf(false))).setValue((Property)BOTTOM, (Comparable)Boolean.valueOf(false))).setValue((Property)X, (Comparable)Boolean.valueOf(false))).setValue((Property)Z, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(new Property[]{X, Z, TOP, BOTTOM, AXIS, BlockStateProperties.WATERLOGGED}));
    }

    public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return Shapes.or((VoxelShape)super.getBlockSupportShape(pState, pReader, pPos), (VoxelShape)AllShapes.EIGHT_VOXEL_POLE.get(Direction.Axis.Y));
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (AllBlocks.SHAFT.isIn(stack)) {
            KineticBlockEntity.switchToBlockState(level, pos, (BlockState)((BlockState)((BlockState)((BlockState)AllBlocks.METAL_GIRDER_ENCASED_SHAFT.getDefaultState().setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)((Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED)))).setValue((Property)TOP, (Comparable)((Boolean)state.getValue((Property)TOP)))).setValue((Property)BOTTOM, (Comparable)((Boolean)state.getValue((Property)BOTTOM)))).setValue(GirderEncasedShaftBlock.HORIZONTAL_AXIS, (Comparable)((Boolean)state.getValue((Property)X) != false || hitResult.getDirection().getAxis() == Direction.Axis.Z ? Direction.Axis.Z : Direction.Axis.X)));
            level.playSound(null, pos, SoundEvents.NETHERITE_BLOCK_HIT, SoundSource.BLOCKS, 0.5f, 1.25f);
            if (!level.isClientSide && !player.isCreative()) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    player.setItemInHand(hand, ItemStack.EMPTY);
                }
            }
            return ItemInteractionResult.SUCCESS;
        }
        if (AllItems.WRENCH.isIn(stack) && !player.isShiftKeyDown()) {
            if (GirderWrenchBehavior.handleClick(level, pos, state, hitResult)) {
                return ItemInteractionResult.sidedSuccess((boolean)level.isClientSide);
            }
            return ItemInteractionResult.FAIL;
        }
        IPlacementHelper helper = PlacementHelpers.get((int)placementHelperId);
        if (helper.matchesItem(stack)) {
            return helper.getOffset(player, level, state, pos, hitResult).placeInWorld(level, (BlockItem)stack.getItem(), player, hand, hitResult);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public FluidState getFluidState(BlockState state) {
        return (Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED) != false ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return InteractionResult.PASS;
    }

    public void tick(BlockState p_60462_, ServerLevel p_60463_, BlockPos p_60464_, RandomSource p_60465_) {
        Block.updateOrDestroy((BlockState)p_60462_, (BlockState)Block.updateFromNeighbourShapes((BlockState)p_60462_, (LevelAccessor)p_60463_, (BlockPos)p_60464_), (LevelAccessor)p_60463_, (BlockPos)p_60464_, (int)3);
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor world, BlockPos pos, BlockPos neighbourPos) {
        if (((Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED)).booleanValue()) {
            world.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay((LevelReader)world));
        }
        Direction.Axis axis = direction.getAxis();
        if (direction.getAxis() != Direction.Axis.Y) {
            if (state.getValue(AXIS) != direction.getAxis()) {
                BooleanProperty updateProperty;
                BooleanProperty booleanProperty = axis == Direction.Axis.X ? X : (axis == Direction.Axis.Z ? Z : (updateProperty = direction == Direction.UP ? TOP : BOTTOM));
                if (!GirderBlock.isConnected((BlockAndTintGetter)world, pos, state, direction) && !GirderBlock.isConnected((BlockAndTintGetter)world, pos, state, direction.getOpposite())) {
                    state = (BlockState)state.setValue((Property)updateProperty, (Comparable)Boolean.valueOf(false));
                }
            }
        } else if (state.getValue(AXIS) != Direction.Axis.Y) {
            if (world.getBlockState(pos.above()).getBlockSupportShape((BlockGetter)world, pos.above()).isEmpty()) {
                state = (BlockState)state.setValue((Property)TOP, (Comparable)Boolean.valueOf(false));
            }
            if (world.getBlockState(pos.below()).getBlockSupportShape((BlockGetter)world, pos.below()).isEmpty()) {
                state = (BlockState)state.setValue((Property)BOTTOM, (Comparable)Boolean.valueOf(false));
            }
        }
        for (Direction d : Iterate.directionsInAxis((Direction.Axis)axis)) {
            state = GirderBlock.updateState(world, pos, state, d);
        }
        return state;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction face = context.getClickedFace();
        FluidState ifluidstate = level.getFluidState(pos);
        BlockState state = super.getStateForPlacement(context);
        state = (BlockState)state.setValue((Property)X, (Comparable)Boolean.valueOf(face.getAxis() == Direction.Axis.X));
        state = (BlockState)state.setValue((Property)Z, (Comparable)Boolean.valueOf(face.getAxis() == Direction.Axis.Z));
        state = (BlockState)state.setValue(AXIS, (Comparable)face.getAxis());
        for (Direction d : Iterate.directions) {
            state = GirderBlock.updateState((LevelAccessor)level, pos, state, d);
        }
        return (BlockState)state.setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(ifluidstate.getType() == Fluids.WATER));
    }

    public static BlockState updateState(LevelAccessor level, BlockPos pos, BlockState state, Direction d) {
        Direction.Axis axis = d.getAxis();
        BooleanProperty updateProperty = axis == Direction.Axis.X ? X : (axis == Direction.Axis.Z ? Z : (d == Direction.UP ? TOP : BOTTOM));
        BlockState sideState = level.getBlockState(pos.relative(d));
        if (axis.isVertical()) {
            return GirderBlock.updateVerticalProperty(level, pos, state, (Property<Boolean>)updateProperty, sideState, d);
        }
        if (state.getValue(AXIS) == axis) {
            state = (BlockState)state.setValue((Property)updateProperty, (Comparable)Boolean.valueOf(true));
        } else if (sideState.getBlock() instanceof GirderEncasedShaftBlock && sideState.getValue(GirderEncasedShaftBlock.HORIZONTAL_AXIS) != axis) {
            state = (BlockState)state.setValue((Property)updateProperty, (Comparable)Boolean.valueOf(true));
        } else if (sideState.getBlock() == state.getBlock() && ((Boolean)sideState.getValue((Property)updateProperty)).booleanValue()) {
            state = (BlockState)state.setValue((Property)updateProperty, (Comparable)Boolean.valueOf(true));
        } else if (sideState.getBlock() instanceof NixieTubeBlock && NixieTubeBlock.getFacing(sideState) == d) {
            state = (BlockState)state.setValue((Property)updateProperty, (Comparable)Boolean.valueOf(true));
        } else if (sideState.getBlock() instanceof PlacardBlock && PlacardBlock.connectedDirection(sideState) == d) {
            state = (BlockState)state.setValue((Property)updateProperty, (Comparable)Boolean.valueOf(true));
        } else if (GirderBlock.isFacingBracket((BlockAndTintGetter)level, pos, d)) {
            state = (BlockState)state.setValue((Property)updateProperty, (Comparable)Boolean.valueOf(true));
        }
        for (Direction d2 : Iterate.directionsInAxis((Direction.Axis)(axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X))) {
            TrackShape shape;
            BlockState above = level.getBlockState(pos.above().relative(d2));
            if (!AllTags.AllBlockTags.GIRDABLE_TRACKS.matches(above) || (shape = (TrackShape)((Object)above.getValue(TrackBlock.SHAPE))) != (axis == Direction.Axis.X ? TrackShape.XO : TrackShape.ZO)) continue;
            state = (BlockState)state.setValue((Property)updateProperty, (Comparable)Boolean.valueOf(true));
        }
        return state;
    }

    public static boolean isFacingBracket(BlockAndTintGetter level, BlockPos pos, Direction d) {
        BlockEntity blockEntity = level.getBlockEntity(pos.relative(d));
        if (!(blockEntity instanceof SmartBlockEntity)) {
            return false;
        }
        SmartBlockEntity sbe = (SmartBlockEntity)blockEntity;
        BracketedBlockEntityBehaviour behaviour = sbe.getBehaviour(BracketedBlockEntityBehaviour.TYPE);
        if (behaviour == null) {
            return false;
        }
        BlockState bracket = behaviour.getBracket();
        if (bracket == null || !bracket.hasProperty((Property)BracketBlock.FACING)) {
            return false;
        }
        return bracket.getValue((Property)BracketBlock.FACING) == d;
    }

    public static BlockState updateVerticalProperty(LevelAccessor level, BlockPos pos, BlockState state, Property<Boolean> updateProperty, BlockState sideState, Direction d) {
        boolean canAttach = false;
        if (state.hasProperty(AXIS) && state.getValue(AXIS) == Direction.Axis.Y) {
            canAttach = true;
        } else if (GirderBlock.isGirder(sideState) && GirderBlock.isXGirder(sideState) == GirderBlock.isZGirder(sideState)) {
            canAttach = true;
        } else if (GirderBlock.isGirder(sideState)) {
            canAttach = true;
        } else if (sideState.hasProperty((Property)WallBlock.UP) && ((Boolean)sideState.getValue((Property)WallBlock.UP)).booleanValue()) {
            canAttach = true;
        } else if (sideState.getBlock() instanceof NixieTubeBlock && NixieTubeBlock.getFacing(sideState) == d) {
            canAttach = true;
        } else if (sideState.getBlock() instanceof FlapDisplayBlock) {
            canAttach = true;
        } else if (sideState.getBlock() instanceof LanternBlock && d == Direction.DOWN == (Boolean)sideState.getValue((Property)LanternBlock.HANGING)) {
            canAttach = true;
        } else if (sideState.getBlock() instanceof ChainBlock && sideState.getValue((Property)ChainBlock.AXIS) == Direction.Axis.Y) {
            canAttach = true;
        } else if (sideState.hasProperty((Property)FaceAttachedHorizontalDirectionalBlock.FACE)) {
            if (sideState.getValue((Property)FaceAttachedHorizontalDirectionalBlock.FACE) == AttachFace.CEILING && d == Direction.DOWN) {
                canAttach = true;
            } else if (sideState.getValue((Property)FaceAttachedHorizontalDirectionalBlock.FACE) == AttachFace.FLOOR && d == Direction.UP) {
                canAttach = true;
            }
        } else if (sideState.getBlock() instanceof PlacardBlock && PlacardBlock.connectedDirection(sideState) == d) {
            canAttach = true;
        } else if (GirderBlock.isFacingBracket((BlockAndTintGetter)level, pos, d)) {
            canAttach = true;
        }
        if (canAttach) {
            return (BlockState)state.setValue(updateProperty, (Comparable)Boolean.valueOf(true));
        }
        return state;
    }

    public static boolean isGirder(BlockState state) {
        return state.getBlock() instanceof GirderBlock || state.getBlock() instanceof GirderEncasedShaftBlock;
    }

    public static boolean isXGirder(BlockState state) {
        return state.getBlock() instanceof GirderBlock && (Boolean)state.getValue((Property)X) != false || state.getBlock() instanceof GirderEncasedShaftBlock && state.getValue(GirderEncasedShaftBlock.HORIZONTAL_AXIS) == Direction.Axis.Z;
    }

    public static boolean isZGirder(BlockState state) {
        return state.getBlock() instanceof GirderBlock && (Boolean)state.getValue((Property)Z) != false || state.getBlock() instanceof GirderEncasedShaftBlock && state.getValue(GirderEncasedShaftBlock.HORIZONTAL_AXIS) == Direction.Axis.X;
    }

    public VoxelShape getShape(BlockState state, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        boolean x = (Boolean)state.getValue((Property)X);
        boolean z = (Boolean)state.getValue((Property)Z);
        return x ? (z ? AllShapes.GIRDER_CROSS : AllShapes.GIRDER_BEAM.get(Direction.Axis.X)) : (z ? AllShapes.GIRDER_BEAM.get(Direction.Axis.Z) : AllShapes.EIGHT_VOXEL_POLE.get(Direction.Axis.Y));
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    public static boolean isConnected(BlockAndTintGetter world, BlockPos pos, BlockState state, Direction side) {
        Direction.Axis axis = side.getAxis();
        if (state.getBlock() instanceof GirderBlock && !((Boolean)state.getValue((Property)(axis == Direction.Axis.X ? X : Z))).booleanValue()) {
            return false;
        }
        if (state.getBlock() instanceof GirderEncasedShaftBlock && state.getValue(GirderEncasedShaftBlock.HORIZONTAL_AXIS) == axis) {
            return false;
        }
        BlockPos relative = pos.relative(side);
        BlockState blockState = world.getBlockState(relative);
        if (blockState.isAir()) {
            return false;
        }
        if (blockState.getBlock() instanceof NixieTubeBlock && NixieTubeBlock.getFacing(blockState) == side) {
            return true;
        }
        if (GirderBlock.isFacingBracket(world, pos, side)) {
            return true;
        }
        if (blockState.getBlock() instanceof PlacardBlock && PlacardBlock.connectedDirection(blockState) == side) {
            return true;
        }
        VoxelShape shape = blockState.getShape((BlockGetter)world, relative);
        if (shape.isEmpty()) {
            return false;
        }
        if (Block.isFaceFull((VoxelShape)shape, (Direction)side.getOpposite()) && blockState.isSolid()) {
            return true;
        }
        return AbstractChuteBlock.getChuteFacing(blockState) == Direction.DOWN;
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        state = (BlockState)state.setValue(AXIS, (Comparable)rot.rotate(Direction.fromAxisAndDirection((Direction.Axis)((Direction.Axis)state.getValue(AXIS)), (Direction.AxisDirection)Direction.AxisDirection.POSITIVE)).getAxis());
        if (rot.rotate(Direction.EAST).getAxis() == Direction.Axis.X) {
            return state;
        }
        return (BlockState)((BlockState)state.setValue((Property)X, (Comparable)((Boolean)state.getValue((Property)Z)))).setValue((Property)Z, (Comparable)((Boolean)state.getValue((Property)Z)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state;
    }
}
