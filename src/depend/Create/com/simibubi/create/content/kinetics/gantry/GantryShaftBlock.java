/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.lang.Lang
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementHelpers
 *  net.createmod.catnip.placement.PlacementOffset
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.kinetics.gantry;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.gantry.GantryShaftBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.placement.PoleHelper;
import java.util.ArrayList;
import java.util.function.Predicate;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GantryShaftBlock
extends DirectionalKineticBlock
implements IBE<GantryShaftBlockEntity> {
    public static final Property<Part> PART = EnumProperty.create((String)"part", Part.class);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private static final int placementHelperId = PlacementHelpers.register((IPlacementHelper)new PlacementHelper());

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition((StateDefinition.Builder<Block, BlockState>)builder.add(new Property[]{PART, POWERED}));
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        IPlacementHelper placementHelper = PlacementHelpers.get((int)placementHelperId);
        if (!placementHelper.matchesItem(stack)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return placementHelper.getOffset(player, level, state, pos, hitResult).placeInWorld(level, (BlockItem)stack.getItem(), player, hand, hitResult);
    }

    public VoxelShape getShape(BlockState state, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        return AllShapes.EIGHT_VOXEL_POLE.get(((Direction)state.getValue((Property)FACING)).getAxis());
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbour, LevelAccessor world, BlockPos pos, BlockPos neighbourPos) {
        Direction facing = (Direction)state.getValue((Property)FACING);
        Direction.Axis axis = facing.getAxis();
        if (direction.getAxis() != axis) {
            return state;
        }
        boolean connect = AllBlocks.GANTRY_SHAFT.has(neighbour) && neighbour.getValue((Property)FACING) == facing;
        Part part = (Part)((Object)state.getValue(PART));
        if (direction.getAxisDirection() == facing.getAxisDirection()) {
            if (connect) {
                if (part == Part.END) {
                    part = Part.MIDDLE;
                }
                if (part == Part.SINGLE) {
                    part = Part.START;
                }
            } else {
                if (part == Part.MIDDLE) {
                    part = Part.END;
                }
                if (part == Part.START) {
                    part = Part.SINGLE;
                }
            }
        } else if (connect) {
            if (part == Part.START) {
                part = Part.MIDDLE;
            }
            if (part == Part.SINGLE) {
                part = Part.END;
            }
        } else {
            if (part == Part.MIDDLE) {
                part = Part.START;
            }
            if (part == Part.END) {
                part = Part.SINGLE;
            }
        }
        return (BlockState)state.setValue(PART, (Comparable)((Object)part));
    }

    public GantryShaftBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue((Property)POWERED, (Comparable)Boolean.valueOf(false))).setValue(PART, (Comparable)((Object)Part.SINGLE)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState clickedState;
        BlockState state = super.getStateForPlacement(context);
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        Direction face = context.getClickedFace();
        BlockState neighbour = world.getBlockState(pos.relative(((Direction)state.getValue((Property)FACING)).getOpposite()));
        BlockState blockState = clickedState = AllBlocks.GANTRY_SHAFT.has(neighbour) ? neighbour : world.getBlockState(pos.relative(face.getOpposite()));
        if (AllBlocks.GANTRY_SHAFT.has(clickedState) && ((Direction)clickedState.getValue((Property)FACING)).getAxis() == ((Direction)state.getValue((Property)FACING)).getAxis()) {
            Direction facing = (Direction)clickedState.getValue((Property)FACING);
            state = (BlockState)state.setValue((Property)FACING, (Comparable)(context.getPlayer() == null || !context.getPlayer().isShiftKeyDown() ? facing : facing.getOpposite()));
        }
        return (BlockState)state.setValue((Property)POWERED, (Comparable)Boolean.valueOf(this.shouldBePowered(state, world, pos)));
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        InteractionResult onWrenched = super.onWrenched(state, context);
        if (onWrenched.consumesAction()) {
            BlockPos pos = context.getClickedPos();
            Level world = context.getLevel();
            this.neighborChanged(world.getBlockState(pos), world, pos, state.getBlock(), pos, false);
        }
        return onWrenched;
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, worldIn, pos, oldState, isMoving);
        if (!worldIn.isClientSide() && oldState.is((Block)AllBlocks.GANTRY_SHAFT.get())) {
            BlockEntity be;
            Part oldPart = (Part)((Object)oldState.getValue(PART));
            Part part = (Part)((Object)state.getValue(PART));
            if ((oldPart != Part.MIDDLE && part == Part.MIDDLE || oldPart == Part.SINGLE && part != Part.SINGLE) && (be = worldIn.getBlockEntity(pos)) instanceof GantryShaftBlockEntity) {
                ((GantryShaftBlockEntity)be).checkAttachedCarriageBlocks();
            }
        }
    }

    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
        if (worldIn.isClientSide) {
            return;
        }
        boolean previouslyPowered = (Boolean)state.getValue((Property)POWERED);
        boolean shouldPower = worldIn.hasNeighborSignal(pos);
        if (!previouslyPowered && !shouldPower && this.shouldBePowered(state, worldIn, pos)) {
            worldIn.setBlock(pos, (BlockState)state.setValue((Property)POWERED, (Comparable)Boolean.valueOf(true)), 3);
            return;
        }
        if (previouslyPowered == shouldPower) {
            return;
        }
        ArrayList<BlockPos> toUpdate = new ArrayList<BlockPos>();
        Direction facing = (Direction)state.getValue((Property)FACING);
        Direction.Axis axis = facing.getAxis();
        block0: for (Direction d : Iterate.directionsInAxis((Direction.Axis)axis)) {
            BlockState currentState;
            BlockPos currentPos = pos.relative(d);
            while (worldIn.isLoaded(currentPos) && (currentState = worldIn.getBlockState(currentPos)).getBlock() instanceof GantryShaftBlock && currentState.getValue((Property)FACING) == facing) {
                if (!shouldPower && ((Boolean)currentState.getValue((Property)POWERED)).booleanValue() && worldIn.hasNeighborSignal(currentPos)) {
                    return;
                }
                if ((Boolean)currentState.getValue((Property)POWERED) == shouldPower) continue block0;
                toUpdate.add(currentPos);
                currentPos = currentPos.relative(d);
            }
        }
        toUpdate.add(pos);
        for (BlockPos blockPos : toUpdate) {
            BlockState blockState = worldIn.getBlockState(blockPos);
            BlockEntity be = worldIn.getBlockEntity(blockPos);
            if (be instanceof KineticBlockEntity) {
                ((KineticBlockEntity)be).detachKinetics();
            }
            if (!(blockState.getBlock() instanceof GantryShaftBlock)) continue;
            worldIn.setBlock(blockPos, (BlockState)blockState.setValue((Property)POWERED, (Comparable)Boolean.valueOf(shouldPower)), 2);
        }
    }

    protected boolean shouldBePowered(BlockState state, Level worldIn, BlockPos pos) {
        boolean shouldPower = worldIn.hasNeighborSignal(pos);
        Direction facing = (Direction)state.getValue((Property)FACING);
        for (Direction d : Iterate.directionsInAxis((Direction.Axis)facing.getAxis())) {
            BlockState neighbourState;
            BlockPos neighbourPos = pos.relative(d);
            if (!worldIn.isLoaded(neighbourPos) || !((neighbourState = worldIn.getBlockState(neighbourPos)).getBlock() instanceof GantryShaftBlock) || neighbourState.getValue((Property)FACING) != facing) continue;
            shouldPower |= ((Boolean)neighbourState.getValue((Property)POWERED)).booleanValue();
        }
        return shouldPower;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == ((Direction)state.getValue((Property)FACING)).getAxis();
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return ((Direction)state.getValue((Property)FACING)).getAxis();
    }

    @Override
    protected boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState newState) {
        return super.areStatesKineticallyEquivalent(oldState, newState) && oldState.getValue((Property)POWERED) == newState.getValue((Property)POWERED);
    }

    @Override
    public float getParticleTargetRadius() {
        return 0.35f;
    }

    @Override
    public float getParticleInitialRadius() {
        return 0.25f;
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public Class<GantryShaftBlockEntity> getBlockEntityClass() {
        return GantryShaftBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends GantryShaftBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.GANTRY_SHAFT.get();
    }

    public static enum Part implements StringRepresentable
    {
        START,
        MIDDLE,
        END,
        SINGLE;


        public String getSerializedName() {
            return Lang.asId((String)this.name());
        }
    }

    public static class PlacementHelper
    extends PoleHelper<Direction> {
        public PlacementHelper() {
            super(arg_0 -> AllBlocks.GANTRY_SHAFT.has(arg_0), s -> ((Direction)s.getValue((Property)DirectionalKineticBlock.FACING)).getAxis(), DirectionalKineticBlock.FACING);
        }

        public Predicate<ItemStack> getItemPredicate() {
            return arg_0 -> AllBlocks.GANTRY_SHAFT.isIn(arg_0);
        }

        @Override
        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            PlacementOffset offset = super.getOffset(player, world, state, pos, ray);
            offset.withTransform(offset.getTransform().andThen(s -> (BlockState)s.setValue((Property)POWERED, (Comparable)((Boolean)state.getValue((Property)POWERED)))));
            return offset;
        }
    }
}
