/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementOffset
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.util.placement_helpers;

import dev.simulated_team.simulated.util.placement_helpers.SimplePlacementHelper;
import java.util.List;
import java.util.function.Predicate;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class SymmetricSailPlacementHelper
extends SimplePlacementHelper {
    public SymmetricSailPlacementHelper(Predicate<ItemStack> itemPredicate, Predicate<BlockState> statePredicate) {
        super(itemPredicate, statePredicate);
    }

    public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
        if (!state.hasProperty((Property)BlockStateProperties.AXIS)) {
            return PlacementOffset.fail();
        }
        Direction.Axis axis = (Direction.Axis)state.getValue((Property)BlockStateProperties.AXIS);
        List validDir = IPlacementHelper.orderedByDistanceExceptAxis((BlockPos)pos, (Vec3)ray.getLocation(), (Direction.Axis)axis);
        for (Direction dir : validDir) {
            if (!world.getBlockState(pos.relative(dir)).canBeReplaced()) continue;
            return PlacementOffset.success((Vec3i)pos.relative(dir), s -> (BlockState)s.setValue((Property)BlockStateProperties.AXIS, (Comparable)axis));
        }
        return PlacementOffset.fail();
    }
}
