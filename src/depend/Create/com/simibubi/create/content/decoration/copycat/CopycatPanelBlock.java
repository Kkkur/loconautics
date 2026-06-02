/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
 *  net.minecraft.world.level.block.TrapDoorBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.Half
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.decoration.copycat;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.decoration.copycat.CopycatSpecialCases;
import com.simibubi.create.content.decoration.copycat.WaterloggedCopycatBlock;
import java.util.List;
import java.util.function.Predicate;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.MethodsReturnNonnullByDefault;
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
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CopycatPanelBlock
extends WaterloggedCopycatBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    private static final int placementHelperId = PlacementHelpers.register((IPlacementHelper)new PlacementHelper());

    public CopycatPanelBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)Direction.UP));
    }

    @Override
    public boolean isAcceptedRegardless(BlockState material) {
        return CopycatSpecialCases.isBarsMaterial(material) || CopycatSpecialCases.isTrapdoorMaterial(material);
    }

    @Override
    public BlockState prepareMaterial(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer, InteractionHand pHand, BlockHitResult pHit, BlockState material) {
        if (!CopycatSpecialCases.isTrapdoorMaterial(material)) {
            return super.prepareMaterial(pLevel, pPos, pState, pPlayer, pHand, pHit, material);
        }
        Direction panelFacing = (Direction)pState.getValue((Property)FACING);
        if (panelFacing == Direction.DOWN) {
            material = (BlockState)material.setValue((Property)TrapDoorBlock.HALF, (Comparable)Half.TOP);
        }
        if (panelFacing.getAxis() == Direction.Axis.Y) {
            return (BlockState)((BlockState)material.setValue((Property)TrapDoorBlock.FACING, (Comparable)pPlayer.getDirection())).setValue((Property)TrapDoorBlock.OPEN, (Comparable)Boolean.valueOf(false));
        }
        boolean clickedNearTop = pHit.getLocation().y - 0.5 > (double)pPos.getY();
        return (BlockState)((BlockState)((BlockState)material.setValue((Property)TrapDoorBlock.OPEN, (Comparable)Boolean.valueOf(true))).setValue((Property)TrapDoorBlock.HALF, (Comparable)(clickedNearTop ? Half.TOP : Half.BOTTOM))).setValue((Property)TrapDoorBlock.FACING, (Comparable)panelFacing);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        IPlacementHelper placementHelper;
        if (!player.isShiftKeyDown() && player.mayBuild() && (placementHelper = PlacementHelpers.get((int)placementHelperId)).matchesItem(stack)) {
            placementHelper.getOffset(player, level, state, pos, hitResult).placeInWorld(level, (BlockItem)stack.getItem(), player, hand, hitResult);
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public boolean isIgnoredConnectivitySide(BlockAndTintGetter reader, BlockState state, Direction face, @Nullable BlockPos fromPos, @Nullable BlockPos toPos) {
        if (fromPos == null || toPos == null) {
            return true;
        }
        Direction facing = (Direction)state.getValue((Property)FACING);
        BlockState toState = reader.getBlockState(toPos);
        if (!toState.is((Block)this)) {
            return facing != face.getOpposite();
        }
        BlockPos diff = fromPos.subtract((Vec3i)toPos);
        int coord = facing.getAxis().choose(diff.getX(), diff.getY(), diff.getZ());
        return facing == ((Direction)toState.getValue((Property)FACING)).getOpposite() && (coord == 0 || coord != facing.getAxisDirection().getStep());
    }

    @Override
    public boolean canConnectTexturesToward(BlockAndTintGetter reader, BlockPos fromPos, BlockPos toPos, BlockState state) {
        Direction facing = (Direction)state.getValue((Property)FACING);
        BlockState toState = reader.getBlockState(toPos);
        if (toPos.equals((Object)fromPos.relative(facing))) {
            return false;
        }
        BlockPos diff = fromPos.subtract((Vec3i)toPos);
        int coord = facing.getAxis().choose(diff.getX(), diff.getY(), diff.getZ());
        if (!toState.is((Block)this)) {
            return coord != -facing.getAxisDirection().getStep();
        }
        if (CopycatPanelBlock.isOccluded(state, toState, facing)) {
            return true;
        }
        return toState.setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)) == state.setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)) && coord == 0;
    }

    @Override
    public boolean canFaceBeOccluded(BlockState state, Direction face) {
        return ((Direction)state.getValue((Property)FACING)).getOpposite() == face;
    }

    @Override
    public boolean shouldFaceAlwaysRender(BlockState state, Direction face) {
        return this.canFaceBeOccluded(state, face.getOpposite());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState stateForPlacement = super.getStateForPlacement(pContext);
        return (BlockState)stateForPlacement.setValue((Property)FACING, (Comparable)pContext.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition((StateDefinition.Builder<Block, BlockState>)pBuilder.add(new Property[]{FACING}));
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AllShapes.CASING_3PX.get((Direction)pState.getValue((Property)FACING));
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    public boolean supportsExternalFaceHiding(BlockState state) {
        return true;
    }

    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
        if (state.is((Block)this) == neighborState.is((Block)this)) {
            if (CopycatSpecialCases.isBarsMaterial(CopycatPanelBlock.getMaterial(level, pos)) && CopycatSpecialCases.isBarsMaterial(CopycatPanelBlock.getMaterial(level, pos.relative(dir)))) {
                return state.getValue((Property)FACING) == neighborState.getValue((Property)FACING);
            }
            if (CopycatPanelBlock.getMaterial(level, pos).skipRendering(CopycatPanelBlock.getMaterial(level, pos.relative(dir)), dir.getOpposite())) {
                return CopycatPanelBlock.isOccluded(state, neighborState, dir.getOpposite());
            }
        }
        return state.getValue((Property)FACING) == dir.getOpposite() && CopycatPanelBlock.getMaterial(level, pos).skipRendering(neighborState, dir.getOpposite());
    }

    public static boolean isOccluded(BlockState state, BlockState other, Direction pDirection) {
        state = (BlockState)state.setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false));
        other = (BlockState)other.setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false));
        Direction facing = (Direction)state.getValue((Property)FACING);
        if (facing.getOpposite() == other.getValue((Property)FACING) && pDirection == facing) {
            return true;
        }
        if (other.getValue((Property)FACING) != facing) {
            return false;
        }
        return pDirection.getAxis() != facing.getAxis();
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return (BlockState)state.setValue((Property)FACING, (Comparable)rot.rotate((Direction)state.getValue((Property)FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation((Direction)state.getValue((Property)FACING)));
    }

    @MethodsReturnNonnullByDefault
    private static class PlacementHelper
    implements IPlacementHelper {
        private PlacementHelper() {
        }

        public Predicate<ItemStack> getItemPredicate() {
            return arg_0 -> AllBlocks.COPYCAT_PANEL.isIn(arg_0);
        }

        public Predicate<BlockState> getStatePredicate() {
            return arg_0 -> AllBlocks.COPYCAT_PANEL.has(arg_0);
        }

        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            List directions = IPlacementHelper.orderedByDistanceExceptAxis((BlockPos)pos, (Vec3)ray.getLocation(), (Direction.Axis)((Direction)state.getValue((Property)FACING)).getAxis(), dir -> world.getBlockState(pos.relative(dir)).canBeReplaced());
            if (directions.isEmpty()) {
                return PlacementOffset.fail();
            }
            return PlacementOffset.success((Vec3i)pos.relative((Direction)directions.get(0)), s -> (BlockState)s.setValue((Property)FACING, (Comparable)((Direction)state.getValue((Property)FACING))));
        }
    }
}
