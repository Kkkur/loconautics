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
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.trains.display;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.trains.display.FlapDisplayBlock;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

@MethodsReturnNonnullByDefault
private static class FlapDisplayBlock.PlacementHelper
implements IPlacementHelper {
    private FlapDisplayBlock.PlacementHelper() {
    }

    public Predicate<ItemStack> getItemPredicate() {
        return arg_0 -> AllBlocks.DISPLAY_BOARD.isIn(arg_0);
    }

    public Predicate<BlockState> getStatePredicate() {
        return arg_0 -> AllBlocks.DISPLAY_BOARD.has(arg_0);
    }

    public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
        List directions = IPlacementHelper.orderedByDistanceExceptAxis((BlockPos)pos, (Vec3)ray.getLocation(), (Direction.Axis)((Direction)state.getValue(HORIZONTAL_FACING)).getAxis(), dir -> world.getBlockState(pos.relative(dir)).canBeReplaced());
        return directions.isEmpty() ? PlacementOffset.fail() : PlacementOffset.success((Vec3i)pos.relative((Direction)directions.get(0)), s -> ((FlapDisplayBlock)AllBlocks.DISPLAY_BOARD.get()).updateColumn(world, pos.relative((Direction)directions.get(0)), (BlockState)s.setValue(HorizontalKineticBlock.HORIZONTAL_FACING, (Comparable)((Direction)state.getValue(HORIZONTAL_FACING))), true));
    }
}
