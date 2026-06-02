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
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.simpleRelays;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class CogwheelBlockItem
extends BlockItem {
    boolean large;
    private final int placementHelperId;
    private final int integratedCogHelperId;

    public CogwheelBlockItem(CogWheelBlock block, Item.Properties builder) {
        super((Block)block, builder);
        this.large = block.isLarge;
        this.placementHelperId = PlacementHelpers.register((IPlacementHelper)(this.large ? new LargeCogHelper() : new SmallCogHelper()));
        this.integratedCogHelperId = PlacementHelpers.register((IPlacementHelper)(this.large ? new IntegratedLargeCogHelper() : new IntegratedSmallCogHelper()));
    }

    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        IPlacementHelper helper = PlacementHelpers.get((int)this.placementHelperId);
        Player player = context.getPlayer();
        BlockHitResult ray = new BlockHitResult(context.getClickLocation(), context.getClickedFace(), pos, true);
        if (helper.matchesState(state) && player != null && !player.isShiftKeyDown()) {
            return helper.getOffset(player, world, state, pos, ray).placeInWorld(world, (BlockItem)this, player, context.getHand(), ray).result();
        }
        if (this.integratedCogHelperId != -1 && (helper = PlacementHelpers.get((int)this.integratedCogHelperId)).matchesState(state) && player != null && !player.isShiftKeyDown()) {
            return helper.getOffset(player, world, state, pos, ray).placeInWorld(world, (BlockItem)this, player, context.getHand(), ray).result();
        }
        return super.onItemUseFirst(stack, context);
    }

    @MethodsReturnNonnullByDefault
    private static class LargeCogHelper
    extends DiagonalCogHelper {
        private LargeCogHelper() {
        }

        public Predicate<ItemStack> getItemPredicate() {
            return ((Predicate<ItemStack>)ICogWheel::isLargeCogItem).and(ICogWheel::isDedicatedCogItem);
        }

        @Override
        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            if (this.hitOnShaft(state, ray)) {
                return PlacementOffset.fail();
            }
            if (ICogWheel.isLargeCog(state)) {
                Direction.Axis axis = ((IRotate)state.getBlock()).getRotationAxis(state);
                Direction side = (Direction)IPlacementHelper.orderedByDistanceOnlyAxis((BlockPos)pos, (Vec3)ray.getLocation(), (Direction.Axis)axis).get(0);
                List directions = IPlacementHelper.orderedByDistanceExceptAxis((BlockPos)pos, (Vec3)ray.getLocation(), (Direction.Axis)axis);
                for (Direction dir : directions) {
                    BlockPos newPos = pos.relative(dir).relative(side);
                    if (!CogWheelBlock.isValidCogwheelPosition(true, (LevelReader)world, newPos, dir.getAxis()) || !world.getBlockState(newPos).canBeReplaced()) continue;
                    return PlacementOffset.success((Vec3i)newPos, s -> (BlockState)s.setValue(RotatedPillarKineticBlock.AXIS, (Comparable)dir.getAxis()));
                }
                return PlacementOffset.fail();
            }
            return super.getOffset(player, world, state, pos, ray);
        }
    }

    @MethodsReturnNonnullByDefault
    private static class SmallCogHelper
    extends DiagonalCogHelper {
        private SmallCogHelper() {
        }

        public Predicate<ItemStack> getItemPredicate() {
            return ((Predicate<ItemStack>)ICogWheel::isSmallCogItem).and(ICogWheel::isDedicatedCogItem);
        }

        @Override
        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            if (this.hitOnShaft(state, ray)) {
                return PlacementOffset.fail();
            }
            if (!ICogWheel.isLargeCog(state)) {
                Direction.Axis axis = ((IRotate)state.getBlock()).getRotationAxis(state);
                List directions = IPlacementHelper.orderedByDistanceExceptAxis((BlockPos)pos, (Vec3)ray.getLocation(), (Direction.Axis)axis);
                for (Direction dir : directions) {
                    BlockPos newPos = pos.relative(dir);
                    if (!CogWheelBlock.isValidCogwheelPosition(false, (LevelReader)world, newPos, axis) || !world.getBlockState(newPos).canBeReplaced()) continue;
                    return PlacementOffset.success((Vec3i)newPos, s -> (BlockState)s.setValue(RotatedPillarKineticBlock.AXIS, (Comparable)axis));
                }
                return PlacementOffset.fail();
            }
            return super.getOffset(player, world, state, pos, ray);
        }
    }

    @MethodsReturnNonnullByDefault
    public static class IntegratedLargeCogHelper
    implements IPlacementHelper {
        public Predicate<ItemStack> getItemPredicate() {
            return ((Predicate<ItemStack>)ICogWheel::isLargeCogItem).and(ICogWheel::isDedicatedCogItem);
        }

        public Predicate<BlockState> getStatePredicate() {
            return s -> !ICogWheel.isDedicatedCogWheel(s.getBlock()) && ICogWheel.isSmallCog(s);
        }

        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            Direction face = ray.getDirection();
            Direction.Axis newAxis = state.hasProperty(HorizontalKineticBlock.HORIZONTAL_FACING) ? ((Direction)state.getValue(HorizontalKineticBlock.HORIZONTAL_FACING)).getAxis() : (state.hasProperty((Property)DirectionalKineticBlock.FACING) ? ((Direction)state.getValue((Property)DirectionalKineticBlock.FACING)).getAxis() : (state.hasProperty(RotatedPillarKineticBlock.AXIS) ? (Direction.Axis)state.getValue(RotatedPillarKineticBlock.AXIS) : Direction.Axis.Y));
            if (face.getAxis() == newAxis) {
                return PlacementOffset.fail();
            }
            List directions = IPlacementHelper.orderedByDistanceExceptAxis((BlockPos)pos, (Vec3)ray.getLocation(), (Direction.Axis)face.getAxis(), (Direction.Axis)newAxis);
            for (Direction d : directions) {
                BlockPos newPos = pos.relative(face).relative(d);
                if (!world.getBlockState(newPos).canBeReplaced()) continue;
                if (!CogWheelBlock.isValidCogwheelPosition(false, (LevelReader)world, newPos, newAxis)) {
                    return PlacementOffset.fail();
                }
                return PlacementOffset.success((Vec3i)newPos, s -> (BlockState)s.setValue((Property)CogWheelBlock.AXIS, (Comparable)newAxis));
            }
            return PlacementOffset.fail();
        }
    }

    @MethodsReturnNonnullByDefault
    public static class IntegratedSmallCogHelper
    implements IPlacementHelper {
        public Predicate<ItemStack> getItemPredicate() {
            return ((Predicate<ItemStack>)ICogWheel::isSmallCogItem).and(ICogWheel::isDedicatedCogItem);
        }

        public Predicate<BlockState> getStatePredicate() {
            return s -> !ICogWheel.isDedicatedCogWheel(s.getBlock()) && ICogWheel.isSmallCog(s);
        }

        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            Direction face = ray.getDirection();
            Direction.Axis newAxis = state.hasProperty(HorizontalKineticBlock.HORIZONTAL_FACING) ? ((Direction)state.getValue(HorizontalKineticBlock.HORIZONTAL_FACING)).getAxis() : (state.hasProperty((Property)DirectionalKineticBlock.FACING) ? ((Direction)state.getValue((Property)DirectionalKineticBlock.FACING)).getAxis() : (state.hasProperty(RotatedPillarKineticBlock.AXIS) ? (Direction.Axis)state.getValue(RotatedPillarKineticBlock.AXIS) : Direction.Axis.Y));
            if (face.getAxis() == newAxis) {
                return PlacementOffset.fail();
            }
            List directions = IPlacementHelper.orderedByDistanceExceptAxis((BlockPos)pos, (Vec3)ray.getLocation(), (Direction.Axis)newAxis);
            for (Direction d : directions) {
                BlockPos newPos = pos.relative(d);
                if (!world.getBlockState(newPos).canBeReplaced()) continue;
                if (!CogWheelBlock.isValidCogwheelPosition(false, (LevelReader)world, newPos, newAxis)) {
                    return PlacementOffset.fail();
                }
                return PlacementOffset.success().at((Vec3i)newPos).withTransform(s -> (BlockState)s.setValue((Property)CogWheelBlock.AXIS, (Comparable)newAxis));
            }
            return PlacementOffset.fail();
        }
    }

    @MethodsReturnNonnullByDefault
    public static abstract class DiagonalCogHelper
    implements IPlacementHelper {
        public Predicate<BlockState> getStatePredicate() {
            return s -> ICogWheel.isSmallCog(s) || ICogWheel.isLargeCog(s);
        }

        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            Direction.Axis axis = ((IRotate)state.getBlock()).getRotationAxis(state);
            Direction closest = (Direction)IPlacementHelper.orderedByDistanceExceptAxis((BlockPos)pos, (Vec3)ray.getLocation(), (Direction.Axis)axis).get(0);
            List directions = IPlacementHelper.orderedByDistanceExceptAxis((BlockPos)pos, (Vec3)ray.getLocation(), (Direction.Axis)axis, d -> d.getAxis() != closest.getAxis());
            for (Direction dir : directions) {
                BlockPos newPos = pos.relative(dir).relative(closest);
                if (!world.getBlockState(newPos).canBeReplaced() || !CogWheelBlock.isValidCogwheelPosition(ICogWheel.isLargeCog(state), (LevelReader)world, newPos, axis)) continue;
                return PlacementOffset.success((Vec3i)newPos, s -> (BlockState)s.setValue(RotatedPillarKineticBlock.AXIS, (Comparable)axis));
            }
            return PlacementOffset.fail();
        }

        protected boolean hitOnShaft(BlockState state, BlockHitResult ray) {
            return AllShapes.SIX_VOXEL_POLE.get(((IRotate)state.getBlock()).getRotationAxis(state)).bounds().inflate(0.001).contains(ray.getLocation().subtract(ray.getLocation().align(Iterate.axisSet)));
        }
    }
}
