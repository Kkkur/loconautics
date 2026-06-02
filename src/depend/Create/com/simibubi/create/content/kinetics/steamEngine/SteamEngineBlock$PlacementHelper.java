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
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.simibubi.create.content.kinetics.steamEngine;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.content.kinetics.steamEngine.PoweredShaftBlock;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlock;
import com.simibubi.create.foundation.utility.BlockHelper;
import java.util.function.Predicate;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

@MethodsReturnNonnullByDefault
private static class SteamEngineBlock.PlacementHelper
implements IPlacementHelper {
    private SteamEngineBlock.PlacementHelper() {
    }

    public Predicate<ItemStack> getItemPredicate() {
        return arg_0 -> AllBlocks.SHAFT.isIn(arg_0);
    }

    public Predicate<BlockState> getStatePredicate() {
        return s -> s.getBlock() instanceof SteamEngineBlock;
    }

    public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
        Direction direction;
        BlockPos shaftPos = SteamEngineBlock.getShaftPos(state, pos);
        BlockState shaft = AllBlocks.SHAFT.getDefaultState();
        Direction[] directionArray = Direction.orderedByNearest((Entity)player);
        int n = directionArray.length;
        for (int i = 0; i < n && !SteamEngineBlock.isShaftValid(state, shaft = (BlockState)shaft.setValue((Property)ShaftBlock.AXIS, (Comparable)(direction = directionArray[i]).getAxis())); ++i) {
        }
        BlockState newState = world.getBlockState(shaftPos);
        if (!newState.canBeReplaced()) {
            return PlacementOffset.fail();
        }
        Direction.Axis axis = (Direction.Axis)shaft.getValue((Property)ShaftBlock.AXIS);
        return PlacementOffset.success((Vec3i)shaftPos, s -> (BlockState)BlockHelper.copyProperties(s, (world.isClientSide ? AllBlocks.SHAFT : AllBlocks.POWERED_SHAFT).getDefaultState()).setValue((Property)PoweredShaftBlock.AXIS, (Comparable)axis));
    }
}
