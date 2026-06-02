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
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.logistics.tableCloth;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.logistics.tableCloth.TableClothBlock;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

private static class TableClothBlock.PlacementHelper
implements IPlacementHelper {
    private TableClothBlock.PlacementHelper() {
    }

    public Predicate<ItemStack> getItemPredicate() {
        return i -> AllTags.AllItemTags.TABLE_CLOTHS.matches(i.getItem());
    }

    public Predicate<BlockState> getStatePredicate() {
        return s -> s.getBlock() instanceof TableClothBlock;
    }

    public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
        List directions = IPlacementHelper.orderedByDistanceExceptAxis((BlockPos)pos, (Vec3)ray.getLocation(), (Direction.Axis)Direction.Axis.Y, dir -> world.getBlockState(pos.relative(dir)).canBeReplaced());
        if (directions.isEmpty()) {
            return PlacementOffset.fail();
        }
        return PlacementOffset.success((Vec3i)pos.relative((Direction)directions.get(0)), s -> s);
    }
}
