/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementHelpers
 *  net.createmod.catnip.placement.PlacementOffset
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.PushReaction
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.kinetics.saw;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.drill.DrillBlock;
import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.damageTypes.CreateDamageSources;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.ParametersAreNonnullByDefault;
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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SawBlock
extends DirectionalAxisKineticBlock
implements IBE<SawBlockEntity> {
    public static final BooleanProperty FLIPPED = BooleanProperty.create((String)"flipped");
    private static final int placementHelperId = PlacementHelpers.register((IPlacementHelper)new PlacementHelper());

    public SawBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition((StateDefinition.Builder<Block, BlockState>)builder.add(new Property[]{FLIPPED}));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState stateForPlacement;
        Direction facing = (Direction)(stateForPlacement = super.getStateForPlacement(context)).getValue((Property)FACING);
        return (BlockState)stateForPlacement.setValue((Property)FLIPPED, (Comparable)Boolean.valueOf(facing.getAxis() == Direction.Axis.Y && context.getHorizontalDirection().getAxisDirection() == Direction.AxisDirection.POSITIVE));
    }

    @Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        BlockState newState = super.getRotatedBlockState(originalState, targetedFace);
        if (((Direction)newState.getValue((Property)FACING)).getAxis() != Direction.Axis.Y) {
            return newState;
        }
        if (targetedFace.getAxis() != Direction.Axis.Y) {
            return newState;
        }
        if (!((Boolean)originalState.getValue((Property)AXIS_ALONG_FIRST_COORDINATE)).booleanValue()) {
            newState = (BlockState)newState.cycle((Property)FLIPPED);
        }
        return newState;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        BlockState newState = super.rotate(state, rot);
        if (((Direction)state.getValue((Property)FACING)).getAxis() != Direction.Axis.Y) {
            return newState;
        }
        if (rot.ordinal() % 2 == 1 && rot == Rotation.CLOCKWISE_90 != (Boolean)state.getValue((Property)AXIS_ALONG_FIRST_COORDINATE)) {
            newState = (BlockState)newState.cycle((Property)FLIPPED);
        }
        if (rot == Rotation.CLOCKWISE_180) {
            newState = (BlockState)newState.cycle((Property)FLIPPED);
        }
        return newState;
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        BlockState newState = super.mirror(state, mirrorIn);
        if (((Direction)state.getValue((Property)FACING)).getAxis() != Direction.Axis.Y) {
            return newState;
        }
        boolean alongX = (Boolean)state.getValue((Property)AXIS_ALONG_FIRST_COORDINATE);
        if (alongX && mirrorIn == Mirror.FRONT_BACK) {
            newState = (BlockState)newState.cycle((Property)FLIPPED);
        }
        if (!alongX && mirrorIn == Mirror.LEFT_RIGHT) {
            newState = (BlockState)newState.cycle((Property)FLIPPED);
        }
        return newState;
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AllShapes.CASING_12PX.get((Direction)state.getValue((Property)FACING));
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        IPlacementHelper placementHelper = PlacementHelpers.get((int)placementHelperId);
        if (!player.isShiftKeyDown() && player.mayBuild() && placementHelper.matchesItem(stack) && placementHelper.getOffset(player, level, state, pos, hitResult).placeInWorld(level, (BlockItem)stack.getItem(), player, hand, hitResult).consumesAction()) {
            return ItemInteractionResult.SUCCESS;
        }
        if (player.isSpectator() || !stack.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (state.getOptionalValue((Property)FACING).orElse(Direction.WEST) != Direction.UP) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return this.onBlockEntityUseItemOn((BlockGetter)level, pos, be -> {
            for (int i = 0; i < be.inventory.getSlots(); ++i) {
                ItemStack heldItemStack = be.inventory.getStackInSlot(i);
                if (level.isClientSide || heldItemStack.isEmpty()) continue;
                player.getInventory().placeItemBackInInventory(heldItemStack);
            }
            be.inventory.clear();
            be.notifyUpdate();
            return ItemInteractionResult.SUCCESS;
        });
    }

    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        if (entityIn instanceof ItemEntity) {
            return;
        }
        if (!new AABB(pos).deflate((double)0.1f).intersects(entityIn.getBoundingBox())) {
            return;
        }
        this.withBlockEntityDo((BlockGetter)worldIn, pos, be -> {
            if (be.getSpeed() == 0.0f) {
                return;
            }
            entityIn.hurt(CreateDamageSources.saw(worldIn), (float)DrillBlock.getDamage(be.getSpeed()));
        });
    }

    public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
        super.updateEntityAfterFallOn(worldIn, entityIn);
        if (!(entityIn instanceof ItemEntity)) {
            return;
        }
        if (entityIn.level().isClientSide) {
            return;
        }
        BlockPos pos = entityIn.blockPosition();
        this.withBlockEntityDo((BlockGetter)entityIn.level(), pos, be -> {
            if (be.getSpeed() == 0.0f) {
                return;
            }
            be.insertItem((ItemEntity)entityIn);
        });
    }

    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.NORMAL;
    }

    public static boolean isHorizontal(BlockState state) {
        return ((Direction)state.getValue((Property)FACING)).getAxis().isHorizontal();
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return SawBlock.isHorizontal(state) ? ((Direction)state.getValue((Property)FACING)).getAxis() : super.getRotationAxis(state);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return SawBlock.isHorizontal(state) ? face == ((Direction)state.getValue((Property)FACING)).getOpposite() : super.hasShaftTowards(world, pos, state, face);
    }

    @Override
    public Class<SawBlockEntity> getBlockEntityClass() {
        return SawBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SawBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.SAW.get();
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @MethodsReturnNonnullByDefault
    private static class PlacementHelper
    implements IPlacementHelper {
        private PlacementHelper() {
        }

        public Predicate<ItemStack> getItemPredicate() {
            return arg_0 -> AllBlocks.MECHANICAL_SAW.isIn(arg_0);
        }

        public Predicate<BlockState> getStatePredicate() {
            return state -> AllBlocks.MECHANICAL_SAW.has(state);
        }

        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            List directions = IPlacementHelper.orderedByDistanceExceptAxis((BlockPos)pos, (Vec3)ray.getLocation(), (Direction.Axis)((Direction)state.getValue((Property)DirectionalKineticBlock.FACING)).getAxis(), dir -> world.getBlockState(pos.relative(dir)).canBeReplaced());
            if (directions.isEmpty()) {
                return PlacementOffset.fail();
            }
            return PlacementOffset.success((Vec3i)pos.relative((Direction)directions.get(0)), s -> (BlockState)((BlockState)((BlockState)s.setValue((Property)DirectionalKineticBlock.FACING, (Comparable)((Direction)state.getValue((Property)DirectionalKineticBlock.FACING)))).setValue((Property)DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE, (Comparable)((Boolean)state.getValue((Property)DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE)))).setValue((Property)FLIPPED, (Comparable)((Boolean)state.getValue((Property)FLIPPED))));
        }
    }
}
