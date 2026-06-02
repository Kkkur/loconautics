/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock
 *  com.simibubi.create.content.kinetics.simpleRelays.ICogWheel
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementOffset
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.util.placement_helpers;

import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import dev.simulated_team.simulated.util.placement_helpers.SimplePlacementHelper;
import java.util.List;
import java.util.function.Predicate;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class CogwheelPlacementExtension
extends SimplePlacementHelper {
    public CogwheelPlacementExtension(Predicate<ItemStack> itemPredicate, Predicate<BlockState> statePredicate) {
        super(itemPredicate, statePredicate);
    }

    public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
        Direction.Axis facingAxis;
        ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (heldItem.isEmpty()) {
            heldItem = player.getItemInHand(InteractionHand.OFF_HAND);
        }
        if (state.hasProperty((Property)BlockStateProperties.AXIS)) {
            facingAxis = (Direction.Axis)state.getValue((Property)BlockStateProperties.AXIS);
        } else if (state.hasProperty((Property)BlockStateProperties.FACING)) {
            facingAxis = ((Direction)state.getValue((Property)BlockStateProperties.FACING)).getAxis();
        } else {
            return PlacementOffset.fail();
        }
        if (ICogWheel.isSmallCogItem((ItemStack)heldItem)) {
            List validDirections = IPlacementHelper.orderedByDistanceExceptAxis((BlockPos)pos, (Vec3)ray.getLocation(), (Direction.Axis)facingAxis);
            for (Direction dir : validDirections) {
                BlockPos newPos = pos.relative(dir);
                if (!CogWheelBlock.isValidCogwheelPosition((boolean)false, (LevelReader)world, (BlockPos)newPos, (Direction.Axis)facingAxis) || !world.getBlockState(newPos).canBeReplaced()) continue;
                return PlacementOffset.success((Vec3i)newPos, s -> (BlockState)s.setValue((Property)CogWheelBlock.AXIS, (Comparable)facingAxis));
            }
        } else {
            Direction closest = (Direction)IPlacementHelper.orderedByDistanceExceptAxis((BlockPos)pos, (Vec3)ray.getLocation(), (Direction.Axis)facingAxis).get(0);
            List directions = IPlacementHelper.orderedByDistanceExceptAxis((BlockPos)pos, (Vec3)ray.getLocation(), (Direction.Axis)facingAxis, d -> d.getAxis() != closest.getAxis());
            for (Direction dir : directions) {
                BlockPos newPos = pos.relative(dir).relative(closest);
                if (!world.getBlockState(newPos).canBeReplaced() || !CogWheelBlock.isValidCogwheelPosition((boolean)ICogWheel.isLargeCog((BlockState)state), (LevelReader)world, (BlockPos)newPos, (Direction.Axis)facingAxis)) continue;
                return PlacementOffset.success((Vec3i)newPos, s -> (BlockState)s.setValue((Property)CogWheelBlock.AXIS, (Comparable)facingAxis));
            }
        }
        return PlacementOffset.fail();
    }
}
