/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.kinetics.simpleRelays;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractSimpleShaftBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.content.kinetics.speedController.SpeedControllerBlock;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import javax.annotation.ParametersAreNonnullByDefault;
import net.createmod.catnip.data.Iterate;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CogWheelBlock
extends AbstractSimpleShaftBlock
implements ICogWheel,
EncasableBlock {
    boolean isLarge;

    protected CogWheelBlock(boolean large, BlockBehaviour.Properties properties) {
        super(properties);
        this.isLarge = large;
    }

    public static CogWheelBlock small(BlockBehaviour.Properties properties) {
        return new CogWheelBlock(false, properties);
    }

    public static CogWheelBlock large(BlockBehaviour.Properties properties) {
        return new CogWheelBlock(true, properties);
    }

    @Override
    public boolean isLargeCog() {
        return this.isLarge;
    }

    @Override
    public boolean isSmallCog() {
        return !this.isLarge;
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return (this.isLarge ? AllShapes.LARGE_GEAR : AllShapes.SMALL_GEAR).get((Direction.Axis)state.getValue((Property)AXIS));
    }

    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        return CogWheelBlock.isValidCogwheelPosition(ICogWheel.isLargeCog(state), worldIn, pos, (Direction.Axis)state.getValue((Property)AXIS));
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        if (placer instanceof Player) {
            Player player = (Player)placer;
            this.triggerShiftingGearsAdvancement(worldIn, pos, state, player);
        }
    }

    protected void triggerShiftingGearsAdvancement(Level world, BlockPos pos, BlockState state, Player player) {
        if (world.isClientSide || player == null) {
            return;
        }
        Direction.Axis axis = (Direction.Axis)state.getValue((Property)AXIS);
        for (Direction.Axis perpendicular1 : Iterate.axes) {
            if (perpendicular1 == axis) continue;
            Direction d1 = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)perpendicular1);
            for (Direction.Axis perpendicular2 : Iterate.axes) {
                if (perpendicular1 == perpendicular2 || axis == perpendicular2) continue;
                Direction d2 = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)perpendicular2);
                for (int offset1 : Iterate.positiveAndNegative) {
                    for (int offset2 : Iterate.positiveAndNegative) {
                        BlockPos connectedPos = pos.relative(d1, offset1).relative(d2, offset2);
                        BlockState blockState = world.getBlockState(connectedPos);
                        if (!(blockState.getBlock() instanceof CogWheelBlock) || blockState.getValue((Property)AXIS) != axis || ICogWheel.isLargeCog(blockState) == this.isLarge) continue;
                        AllAdvancements.COGS.awardTo(player);
                    }
                }
            }
        }
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isShiftKeyDown() || !player.mayBuild()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        ItemInteractionResult result = this.tryEncase(state, level, pos, stack, player, hand, hitResult);
        if (result.consumesAction()) {
            return result;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public static boolean isValidCogwheelPosition(boolean large, LevelReader worldIn, BlockPos pos, Direction.Axis cogAxis) {
        for (Direction facing : Iterate.directions) {
            BlockPos offsetPos;
            BlockState blockState;
            if (facing.getAxis() == cogAxis || (blockState = worldIn.getBlockState(offsetPos = pos.relative(facing))).hasProperty((Property)AXIS) && facing.getAxis() == blockState.getValue((Property)AXIS) || !ICogWheel.isLargeCog(blockState) && (!large || !ICogWheel.isSmallCog(blockState))) continue;
            return false;
        }
        return true;
    }

    protected Direction.Axis getAxisForPlacement(BlockPlaceContext context) {
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            return context.getClickedFace().getAxis();
        }
        Level world = context.getLevel();
        BlockState stateBelow = world.getBlockState(context.getClickedPos().below());
        if (AllBlocks.ROTATION_SPEED_CONTROLLER.has(stateBelow) && this.isLargeCog()) {
            return stateBelow.getValue(SpeedControllerBlock.HORIZONTAL_AXIS) == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        }
        BlockPos placedOnPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
        BlockState placedAgainst = world.getBlockState(placedOnPos);
        Block block = placedAgainst.getBlock();
        if (ICogWheel.isSmallCog(placedAgainst)) {
            return ((IRotate)block).getRotationAxis(placedAgainst);
        }
        Direction.Axis preferredAxis = CogWheelBlock.getPreferredAxis(context);
        return preferredAxis != null ? preferredAxis : context.getClickedFace().getAxis();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean shouldWaterlog = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        return (BlockState)((BlockState)this.defaultBlockState().setValue((Property)AXIS, (Comparable)this.getAxisForPlacement(context))).setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(shouldWaterlog));
    }

    @Override
    public float getParticleTargetRadius() {
        return this.isLargeCog() ? 1.125f : 0.65f;
    }

    @Override
    public float getParticleInitialRadius() {
        return this.isLargeCog() ? 1.0f : 0.75f;
    }

    @Override
    public boolean isDedicatedCogWheel() {
        return true;
    }
}
