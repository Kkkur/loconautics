/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementOffset
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.simibubi.create.content.kinetics.speedController;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.HorizontalAxisKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
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

@MethodsReturnNonnullByDefault
private static class SpeedControllerBlock.PlacementHelper
implements IPlacementHelper {
    private SpeedControllerBlock.PlacementHelper() {
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
