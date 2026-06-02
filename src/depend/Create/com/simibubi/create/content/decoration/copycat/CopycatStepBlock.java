/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VoxelShaper
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementHelpers
 *  net.createmod.catnip.placement.PlacementOffset
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Half
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.decoration.copycat;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.decoration.copycat.WaterloggedCopycatBlock;
import com.simibubi.create.foundation.placement.PoleHelper;
import java.util.function.Predicate;
import net.createmod.catnip.math.VoxelShaper;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CopycatStepBlock
extends WaterloggedCopycatBlock {
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final int placementHelperId = PlacementHelpers.register((IPlacementHelper)new PlacementHelper());

    public CopycatStepBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(HALF, (Comparable)Half.BOTTOM)).setValue((Property)FACING, (Comparable)Direction.SOUTH));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        IPlacementHelper helper;
        if (!player.isShiftKeyDown() && player.mayBuild() && (helper = PlacementHelpers.get((int)placementHelperId)).matchesItem(stack)) {
            return helper.getOffset(player, level, state, pos, hitResult).placeInWorld(level, (BlockItem)stack.getItem(), player, hand, hitResult);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public boolean isIgnoredConnectivitySide(BlockAndTintGetter reader, BlockState state, Direction face, @Nullable BlockPos fromPos, @Nullable BlockPos toPos) {
        if (fromPos == null || toPos == null) {
            return true;
        }
        BlockState toState = reader.getBlockState(toPos);
        if (!toState.is((Block)this)) {
            return true;
        }
        Direction facing = (Direction)state.getValue((Property)FACING);
        BlockPos diff = fromPos.subtract((Vec3i)toPos);
        int coord = facing.getAxis().choose(diff.getX(), diff.getY(), diff.getZ());
        Half half = (Half)state.getValue(HALF);
        if (half != toState.getValue(HALF)) {
            return diff.getY() == 0;
        }
        return facing == ((Direction)toState.getValue((Property)FACING)).getOpposite() && (coord == 0 || coord == facing.getAxisDirection().getStep());
    }

    @Override
    public boolean canConnectTexturesToward(BlockAndTintGetter reader, BlockPos fromPos, BlockPos toPos, BlockState state) {
        Direction facing = (Direction)state.getValue((Property)FACING);
        BlockState toState = reader.getBlockState(toPos);
        BlockPos diff = fromPos.subtract((Vec3i)toPos);
        if (fromPos.equals((Object)toPos.relative(facing))) {
            return false;
        }
        if (!toState.is((Block)this)) {
            return false;
        }
        if (diff.getY() != 0) {
            return CopycatStepBlock.isOccluded(toState, state, diff.getY() > 0 ? Direction.UP : Direction.DOWN);
        }
        if (CopycatStepBlock.isOccluded(state, toState, facing)) {
            return true;
        }
        int coord = facing.getAxis().choose(diff.getX(), diff.getY(), diff.getZ());
        return state.setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)) == toState.setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)) && coord == 0;
    }

    @Override
    public boolean canFaceBeOccluded(BlockState state, Direction face) {
        if (face.getAxis() == Direction.Axis.Y) {
            return state.getValue(HALF) == Half.TOP == (face == Direction.UP);
        }
        return state.getValue((Property)FACING) == face;
    }

    @Override
    public boolean shouldFaceAlwaysRender(BlockState state, Direction face) {
        return this.canFaceBeOccluded(state, face.getOpposite());
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState stateForPlacement = (BlockState)super.getStateForPlacement(pContext).setValue((Property)FACING, (Comparable)pContext.getHorizontalDirection());
        Direction direction = pContext.getClickedFace();
        if (direction == Direction.UP) {
            return stateForPlacement;
        }
        if (direction == Direction.DOWN || pContext.getClickLocation().y - (double)pContext.getClickedPos().getY() > 0.5) {
            return (BlockState)stateForPlacement.setValue(HALF, (Comparable)Half.TOP);
        }
        return stateForPlacement;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition((StateDefinition.Builder<Block, BlockState>)pBuilder.add(new Property[]{HALF, FACING}));
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        VoxelShaper voxelShaper = pState.getValue(HALF) == Half.BOTTOM ? AllShapes.STEP_BOTTOM : AllShapes.STEP_TOP;
        return voxelShaper.get((Direction)pState.getValue((Property)FACING));
    }

    public boolean supportsExternalFaceHiding(BlockState state) {
        return true;
    }

    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
        if (state.is((Block)this) == neighborState.is((Block)this) && CopycatStepBlock.getMaterial(level, pos).skipRendering(CopycatStepBlock.getMaterial(level, pos.relative(dir)), dir.getOpposite())) {
            return CopycatStepBlock.isOccluded(state, neighborState, dir);
        }
        return false;
    }

    public static boolean isOccluded(BlockState state, BlockState other, Direction pDirection) {
        boolean vertical;
        state = (BlockState)state.setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false));
        other = (BlockState)other.setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false));
        Half half = (Half)state.getValue(HALF);
        boolean bl = vertical = pDirection.getAxis() == Direction.Axis.Y;
        if (half != other.getValue(HALF)) {
            return vertical && pDirection == Direction.UP == (half == Half.TOP);
        }
        if (vertical) {
            return false;
        }
        Direction facing = (Direction)state.getValue((Property)FACING);
        if (facing.getOpposite() == other.getValue((Property)FACING) && pDirection == facing) {
            return true;
        }
        if (other.getValue((Property)FACING) != facing) {
            return false;
        }
        return pDirection.getAxis() != facing.getAxis();
    }

    public BlockState rotate(BlockState pState, Rotation pRot) {
        return (BlockState)pState.setValue((Property)FACING, (Comparable)pRot.rotate((Direction)pState.getValue((Property)FACING)));
    }

    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation((Direction)pState.getValue((Property)FACING)));
    }

    private static class PlacementHelper
    extends PoleHelper<Direction> {
        public PlacementHelper() {
            super(arg_0 -> AllBlocks.COPYCAT_STEP.has(arg_0), state -> ((Direction)state.getValue((Property)FACING)).getClockWise().getAxis(), FACING);
        }

        @NotNull
        public Predicate<ItemStack> getItemPredicate() {
            return arg_0 -> AllBlocks.COPYCAT_STEP.isIn(arg_0);
        }

        @Override
        @NotNull
        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            PlacementOffset offset = super.getOffset(player, world, state, pos, ray);
            if (offset.isSuccessful()) {
                offset.withTransform(offset.getTransform().andThen(s -> (BlockState)s.setValue(HALF, (Comparable)((Half)state.getValue(HALF)))));
            }
            return offset;
        }
    }
}
