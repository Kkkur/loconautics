/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicates
 *  net.createmod.catnip.placement.PlacementOffset
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.simibubi.create.content.kinetics.simpleRelays;

import com.google.common.base.Predicates;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractSimpleShaftBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.content.kinetics.steamEngine.PoweredShaftBlock;
import com.simibubi.create.foundation.placement.PoleHelper;
import java.util.function.Predicate;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@MethodsReturnNonnullByDefault
private static class ShaftBlock.PlacementHelper
extends PoleHelper<Direction.Axis> {
    private ShaftBlock.PlacementHelper() {
        super(state -> state.getBlock() instanceof AbstractSimpleShaftBlock || state.getBlock() instanceof PoweredShaftBlock, state -> (Direction.Axis)state.getValue(RotatedPillarKineticBlock.AXIS), RotatedPillarKineticBlock.AXIS);
    }

    public Predicate<ItemStack> getItemPredicate() {
        return i -> i.getItem() instanceof BlockItem && ((BlockItem)i.getItem()).getBlock() instanceof AbstractSimpleShaftBlock;
    }

    @Override
    public Predicate<BlockState> getStatePredicate() {
        return Predicates.or(arg_0 -> AllBlocks.SHAFT.has(arg_0), arg_0 -> AllBlocks.POWERED_SHAFT.has(arg_0));
    }

    @Override
    public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
        PlacementOffset offset = super.getOffset(player, world, state, pos, ray);
        if (offset.isSuccessful()) {
            offset.withTransform(offset.getTransform().andThen(s -> world.isClientSide() ? s : ShaftBlock.pickCorrectShaftType(s, world, offset.getBlockPos())));
        }
        return offset;
    }
}
