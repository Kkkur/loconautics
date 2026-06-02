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
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.kinetics.speedController;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.HorizontalAxisKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.content.kinetics.speedController.SpeedControllerBlockEntity;
import com.simibubi.create.foundation.block.IBE;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SpeedControllerBlock
extends HorizontalAxisKineticBlock
implements IBE<SpeedControllerBlockEntity> {
    private static final int placementHelperId = PlacementHelpers.register((IPlacementHelper)new PlacementHelper());

    public SpeedControllerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState above = context.getLevel().getBlockState(context.getClickedPos().above());
        if (ICogWheel.isLargeCog(above) && ((Direction.Axis)above.getValue((Property)CogWheelBlock.AXIS)).isHorizontal()) {
            return (BlockState)this.defaultBlockState().setValue(HORIZONTAL_AXIS, (Comparable)(above.getValue((Property)CogWheelBlock.AXIS) == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X));
        }
        return super.getStateForPlacement(context);
    }

    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block p_220069_4_, BlockPos neighbourPos, boolean p_220069_6_) {
        if (neighbourPos.equals((Object)pos.above())) {
            this.withBlockEntityDo((BlockGetter)world, pos, SpeedControllerBlockEntity::updateBracket);
        }
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        IPlacementHelper helper = PlacementHelpers.get((int)placementHelperId);
        if (helper.matchesItem(stack)) {
            return helper.getOffset(player, level, state, pos, hitResult).placeInWorld(level, (BlockItem)stack.getItem(), player, hand, hitResult);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AllShapes.SPEED_CONTROLLER;
    }

    @Override
    public Class<SpeedControllerBlockEntity> getBlockEntityClass() {
        return SpeedControllerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SpeedControllerBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.ROTATION_SPEED_CONTROLLER.get();
    }

    @MethodsReturnNonnullByDefault
    private static class PlacementHelper
    implements IPlacementHelper {
        private PlacementHelper() {
        }

        public Predicate<ItemStack> getItemPredicate() {
            return ((Predicate<ItemStack>)ICogWheel::isLargeCogItem).and(ICogWheel::isDedicatedCogItem);
        }

        public Predicate<BlockState> getStatePredicate() {
            return arg_0 -> AllBlocks.ROTATION_SPEED_CONTROLLER.has(arg_0);
        }

        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            Direction.Axis newAxis;
            BlockPos newPos = pos.above();
            if (!world.getBlockState(newPos).canBeReplaced()) {
                return PlacementOffset.fail();
            }
            Direction.Axis axis = newAxis = state.getValue(HorizontalAxisKineticBlock.HORIZONTAL_AXIS) == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
            if (!CogWheelBlock.isValidCogwheelPosition(true, (LevelReader)world, newPos, newAxis)) {
                return PlacementOffset.fail();
            }
            return PlacementOffset.success((Vec3i)newPos, s -> (BlockState)s.setValue((Property)CogWheelBlock.AXIS, (Comparable)newAxis));
        }
    }
}
