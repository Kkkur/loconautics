/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementOffset
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.simpleRelays;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import java.util.List;
import java.util.function.Predicate;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

@MethodsReturnNonnullByDefault
public static abstract class CogwheelBlockItem.DiagonalCogHelper
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
