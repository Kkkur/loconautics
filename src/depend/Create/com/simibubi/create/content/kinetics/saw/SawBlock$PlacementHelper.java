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
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.saw;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

@MethodsReturnNonnullByDefault
private static class SawBlock.PlacementHelper
implements IPlacementHelper {
    private SawBlock.PlacementHelper() {
    }

    public Predicate<ItemStack> getItemPredicate() {
        return arg_0 -> AllBlocks.MECHANICAL_SAW.isIn(arg_0);
    }

    public Predicate<BlockState> getStatePredicate() {
        return state -> AllBlocks.MECHANICAL_SAW.has(state);
    }

    public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
        List directions = IPlacementHelper.orderedByDistanceExceptAxis((BlockPos)pos, (Vec3)ray.getLocation(), (Direction.Axis)((Direction)state.getValue((Property)DirectionalKineticBlock.FACING)).getAxis(), dir -> world.getBlockState(pos.relative(dir)).canBeReplaced());
        if (directions.isEmpty()) {
            return PlacementOffset.fail();
        }
        return PlacementOffset.success((Vec3i)pos.relative((Direction)directions.get(0)), s -> (BlockState)((BlockState)((BlockState)s.setValue((Property)DirectionalKineticBlock.FACING, (Comparable)((Direction)state.getValue((Property)DirectionalKineticBlock.FACING)))).setValue((Property)DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE, (Comparable)((Boolean)state.getValue((Property)DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE)))).setValue((Property)FLIPPED, (Comparable)((Boolean)state.getValue((Property)FLIPPED))));
    }
}
