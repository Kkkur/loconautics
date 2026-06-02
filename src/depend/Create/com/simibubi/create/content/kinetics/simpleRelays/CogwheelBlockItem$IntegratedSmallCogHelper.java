/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementOffset
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.simpleRelays;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import java.util.List;
import java.util.function.Predicate;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

@MethodsReturnNonnullByDefault
public static class CogwheelBlockItem.IntegratedSmallCogHelper
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
