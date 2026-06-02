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
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.simpleRelays;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.CogwheelBlockItem;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

@MethodsReturnNonnullByDefault
private static class CogwheelBlockItem.SmallCogHelper
extends CogwheelBlockItem.DiagonalCogHelper {
    private CogwheelBlockItem.SmallCogHelper() {
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
