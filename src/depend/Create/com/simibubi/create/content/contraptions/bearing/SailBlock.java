/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementHelpers
 *  net.createmod.catnip.placement.PlacementOffset
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.ShearsItem
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.DirectionalBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.contraptions.bearing;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import com.simibubi.create.foundation.utility.BlockHelper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SailBlock
extends WrenchableDirectionalBlock {
    private static final int placementHelperId = PlacementHelpers.register((IPlacementHelper)new PlacementHelper());
    protected final boolean frame;
    protected final DyeColor color;

    public static SailBlock frame(BlockBehaviour.Properties properties) {
        return new SailBlock(properties, true, null);
    }

    public static SailBlock withCanvas(BlockBehaviour.Properties properties, DyeColor color) {
        return new SailBlock(properties, false, color);
    }

    protected SailBlock(BlockBehaviour.Properties properties, boolean frame, DyeColor color) {
        super(properties);
        this.frame = frame;
        this.color = color;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return (BlockState)state.setValue((Property)FACING, (Comparable)((Direction)state.getValue((Property)FACING)).getOpposite());
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        IPlacementHelper placementHelper = PlacementHelpers.get((int)placementHelperId);
        if (!player.isShiftKeyDown() && player.mayBuild() && placementHelper.matchesItem(stack)) {
            placementHelper.getOffset(player, level, state, pos, hitResult).placeInWorld(level, (BlockItem)stack.getItem(), player, hand, hitResult);
            return ItemInteractionResult.SUCCESS;
        }
        if (stack.getItem() instanceof ShearsItem) {
            if (!level.isClientSide) {
                level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
            this.applyDye(state, level, pos, hitResult.getLocation(), null);
            return ItemInteractionResult.SUCCESS;
        }
        if (this.frame) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        DyeColor color = DyeColor.getColor((ItemStack)stack);
        if (color != null) {
            if (!level.isClientSide) {
                level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0f, 1.1f - level.random.nextFloat() * 0.2f);
            }
            this.applyDye(state, level, pos, hitResult.getLocation(), color);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public void applyDye(BlockState state, Level world, BlockPos pos, Vec3 hit, @Nullable DyeColor color) {
        BlockState newState = (color == null ? AllBlocks.SAIL_FRAME : AllBlocks.DYED_SAILS.get(color)).getDefaultState();
        if (state != (newState = BlockHelper.copyProperties(state, newState))) {
            world.setBlockAndUpdate(pos, newState);
            return;
        }
        List directions = IPlacementHelper.orderedByDistanceExceptAxis((BlockPos)pos, (Vec3)hit, (Direction.Axis)((Direction)state.getValue((Property)FACING)).getAxis());
        for (Direction d : directions) {
            BlockPos offset = pos.relative(d);
            BlockState adjacentState = world.getBlockState(offset);
            Block block = adjacentState.getBlock();
            if (!(block instanceof SailBlock) || ((SailBlock)block).frame || state.getValue((Property)FACING) != adjacentState.getValue((Property)FACING) || state == adjacentState) continue;
            world.setBlockAndUpdate(offset, newState);
            return;
        }
        ArrayList<BlockPos> frontier = new ArrayList<BlockPos>();
        frontier.add(pos);
        HashSet<BlockPos> visited = new HashSet<BlockPos>();
        int timeout = 100;
        while (!frontier.isEmpty() && timeout-- >= 0) {
            BlockPos currentPos = (BlockPos)frontier.remove(0);
            visited.add(currentPos);
            for (Direction d : Iterate.directions) {
                BlockState adjacentState;
                Block block;
                BlockPos offset;
                if (d.getAxis() == ((Direction)state.getValue((Property)FACING)).getAxis() || visited.contains(offset = currentPos.relative(d)) || !((block = (adjacentState = world.getBlockState(offset)).getBlock()) instanceof SailBlock) || ((SailBlock)block).frame && color != null || adjacentState.getValue((Property)FACING) != state.getValue((Property)FACING)) continue;
                if (state != adjacentState) {
                    world.setBlockAndUpdate(offset, newState);
                }
                frontier.add(offset);
                visited.add(offset);
            }
        }
    }

    public VoxelShape getShape(BlockState state, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        return (this.frame ? AllShapes.SAIL_FRAME : AllShapes.SAIL).get((Direction)state.getValue((Property)FACING));
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter p_220071_2_, BlockPos p_220071_3_, CollisionContext p_220071_4_) {
        if (this.frame) {
            return AllShapes.SAIL_FRAME_COLLISION.get((Direction)state.getValue((Property)FACING));
        }
        return this.getShape(state, p_220071_2_, p_220071_3_, p_220071_4_);
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack pickBlock = super.getCloneItemStack(state, target, level, pos, player);
        if (pickBlock.isEmpty()) {
            return ((SailBlock)AllBlocks.SAIL.get()).getCloneItemStack(state, target, level, pos, player);
        }
        return pickBlock;
    }

    public void fallOn(Level p_152426_, BlockState p_152427_, BlockPos p_152428_, Entity p_152429_, float p_152430_) {
        if (this.frame) {
            super.fallOn(p_152426_, p_152427_, p_152428_, p_152429_, p_152430_);
        }
        super.fallOn(p_152426_, p_152427_, p_152428_, p_152429_, 0.0f);
    }

    public void updateEntityAfterFallOn(BlockGetter p_176216_1_, Entity p_176216_2_) {
        if (this.frame || p_176216_2_.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(p_176216_1_, p_176216_2_);
        } else {
            this.bounce(p_176216_2_);
        }
    }

    private void bounce(Entity p_226860_1_) {
        Vec3 Vector3d = p_226860_1_.getDeltaMovement();
        if (Vector3d.y < 0.0) {
            double d0 = p_226860_1_ instanceof LivingEntity ? 1.0 : 0.8;
            p_226860_1_.setDeltaMovement(Vector3d.x, -Vector3d.y * (double)0.26f * d0, Vector3d.z);
        }
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    public boolean isFrame() {
        return this.frame;
    }

    public DyeColor getColor() {
        return this.color;
    }

    @MethodsReturnNonnullByDefault
    private static class PlacementHelper
    implements IPlacementHelper {
        private PlacementHelper() {
        }

        public Predicate<ItemStack> getItemPredicate() {
            return i -> AllBlocks.SAIL.isIn(i) || AllBlocks.SAIL_FRAME.isIn(i);
        }

        public Predicate<BlockState> getStatePredicate() {
            return s -> s.getBlock() instanceof SailBlock;
        }

        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            List directions = IPlacementHelper.orderedByDistanceExceptAxis((BlockPos)pos, (Vec3)ray.getLocation(), (Direction.Axis)((Direction)state.getValue((Property)FACING)).getAxis(), dir -> world.getBlockState(pos.relative(dir)).canBeReplaced());
            if (directions.isEmpty()) {
                return PlacementOffset.fail();
            }
            return PlacementOffset.success((Vec3i)pos.relative((Direction)directions.get(0)), s -> (BlockState)s.setValue((Property)DirectionalBlock.FACING, (Comparable)((Direction)state.getValue((Property)DirectionalBlock.FACING))));
        }
    }
}
