/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicates
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementHelpers
 *  net.createmod.catnip.placement.PlacementOffset
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.kinetics.simpleRelays;

import com.google.common.base.Predicates;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.decoration.girder.GirderEncasedShaftBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractSimpleShaftBlock;
import com.simibubi.create.content.kinetics.steamEngine.PoweredShaftBlock;
import com.simibubi.create.foundation.placement.PoleHelper;
import java.util.function.Predicate;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShaftBlock
extends AbstractSimpleShaftBlock
implements EncasableBlock {
    public static final int placementHelperId = PlacementHelpers.register((IPlacementHelper)new PlacementHelper());

    public ShaftBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static boolean isShaft(BlockState state) {
        return AllBlocks.SHAFT.has(state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState stateForPlacement = super.getStateForPlacement(context);
        return ShaftBlock.pickCorrectShaftType(stateForPlacement, context.getLevel(), context.getClickedPos());
    }

    public static BlockState pickCorrectShaftType(BlockState stateForPlacement, Level level, BlockPos pos) {
        if (PoweredShaftBlock.stillValid(stateForPlacement, (LevelReader)level, pos)) {
            return PoweredShaftBlock.getEquivalent(stateForPlacement);
        }
        return stateForPlacement;
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AllShapes.SIX_VOXEL_POLE.get((Direction.Axis)state.getValue((Property)AXIS));
    }

    @Override
    public float getParticleTargetRadius() {
        return 0.35f;
    }

    @Override
    public float getParticleInitialRadius() {
        return 0.125f;
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isShiftKeyDown() || !player.mayBuild()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        ItemInteractionResult result = this.tryEncase(state, level, pos, stack, player, hand, hitResult);
        if (result.consumesAction()) {
            return result;
        }
        if (AllBlocks.METAL_GIRDER.isIn(stack) && state.getValue((Property)AXIS) != Direction.Axis.Y) {
            KineticBlockEntity.switchToBlockState(level, pos, (BlockState)((BlockState)AllBlocks.METAL_GIRDER_ENCASED_SHAFT.getDefaultState().setValue((Property)WATERLOGGED, (Comparable)((Boolean)state.getValue((Property)WATERLOGGED)))).setValue(GirderEncasedShaftBlock.HORIZONTAL_AXIS, (Comparable)(state.getValue((Property)AXIS) == Direction.Axis.Z ? Direction.Axis.Z : Direction.Axis.X)));
            if (!level.isClientSide && !player.isCreative()) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    player.setItemInHand(hand, ItemStack.EMPTY);
                }
            }
            return ItemInteractionResult.SUCCESS;
        }
        IPlacementHelper helper = PlacementHelpers.get((int)placementHelperId);
        if (helper.matchesItem(stack)) {
            return helper.getOffset(player, level, state, pos, hitResult).placeInWorld(level, (BlockItem)stack.getItem(), player, hand, hitResult);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @MethodsReturnNonnullByDefault
    private static class PlacementHelper
    extends PoleHelper<Direction.Axis> {
        private PlacementHelper() {
            super(state -> state.getBlock() instanceof AbstractSimpleShaftBlock || state.getBlock() instanceof PoweredShaftBlock, state -> (Direction.Axis)state.getValue(RotatedPillarKineticBlock.AXIS), RotatedPillarKineticBlock.AXIS);
        }

        public Predicate<ItemStack> getItemPredicate() {
            return i -> i.getItem() instanceof BlockItem && ((BlockItem)i.getItem()).getBlock() instanceof AbstractSimpleShaftBlock;
        }

        @Override
        public Predicate<BlockState> getStatePredicate() {
            return Predicates.or(arg_0 -> AllBlocks.SHAFT.has(arg_0), arg_0 -> AllBlocks.POWERED_SHAFT.has(arg_0));
        }

        @Override
        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            PlacementOffset offset = super.getOffset(player, world, state, pos, ray);
            if (offset.isSuccessful()) {
                offset.withTransform(offset.getTransform().andThen(s -> world.isClientSide() ? s : ShaftBlock.pickCorrectShaftType(s, world, offset.getBlockPos())));
            }
            return offset;
        }
    }
}
