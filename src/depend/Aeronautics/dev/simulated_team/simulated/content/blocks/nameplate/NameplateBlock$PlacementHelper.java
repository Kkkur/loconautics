/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.util.entry.BlockEntry
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementOffset
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.HorizontalDirectionalBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.blocks.nameplate;

import com.tterrag.registrate.util.entry.BlockEntry;
import dev.simulated_team.simulated.index.SimBlocks;
import java.util.List;
import java.util.function.Predicate;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

private static class NameplateBlock.PlacementHelper
implements IPlacementHelper {
    private NameplateBlock.PlacementHelper() {
    }

    public Predicate<ItemStack> getItemPredicate() {
        return stack -> {
            for (BlockEntry nameplate : SimBlocks.NAMEPLATES) {
                if (!nameplate.is(stack.getItem())) continue;
                return true;
            }
            return false;
        };
    }

    public Predicate<BlockState> getStatePredicate() {
        return state -> {
            for (BlockEntry nameplate : SimBlocks.NAMEPLATES) {
                if (!nameplate.has(state)) continue;
                return true;
            }
            return false;
        };
    }

    public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray, ItemStack heldItem) {
        BlockItem bi;
        Item item = heldItem.getItem();
        if (item instanceof BlockItem && state.is((bi = (BlockItem)item).getBlock())) {
            return super.getOffset(player, world, state, pos, ray, heldItem);
        }
        return PlacementOffset.fail();
    }

    public PlacementOffset getOffset(Player player, Level level, BlockState blockState, BlockPos blockPos, BlockHitResult blockHitResult) {
        List directions = IPlacementHelper.orderedByDistance((BlockPos)blockPos, (Vec3)blockHitResult.getLocation(), dir -> {
            if (dir.getAxis() != ((Direction)blockState.getValue((Property)HorizontalDirectionalBlock.FACING)).getClockWise().getAxis()) {
                return false;
            }
            BlockPos relPos = blockPos.relative(dir);
            return level.getBlockState(relPos).canBeReplaced() && blockState.canSurvive((LevelReader)level, relPos);
        });
        if (directions.isEmpty()) {
            return PlacementOffset.fail();
        }
        return PlacementOffset.success((Vec3i)blockPos.relative((Direction)directions.getFirst()), s -> (BlockState)s.setValue((Property)HorizontalDirectionalBlock.FACING, (Comparable)((Direction)blockState.getValue((Property)HorizontalDirectionalBlock.FACING))));
    }
}
