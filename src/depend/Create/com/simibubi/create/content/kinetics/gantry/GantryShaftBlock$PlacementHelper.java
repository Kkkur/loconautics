/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.placement.PlacementOffset
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.simibubi.create.content.kinetics.gantry;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.placement.PoleHelper;
import java.util.function.Predicate;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public static class GantryShaftBlock.PlacementHelper
extends PoleHelper<Direction> {
    public GantryShaftBlock.PlacementHelper() {
        super(arg_0 -> AllBlocks.GANTRY_SHAFT.has(arg_0), s -> ((Direction)s.getValue((Property)DirectionalKineticBlock.FACING)).getAxis(), DirectionalKineticBlock.FACING);
    }

    public Predicate<ItemStack> getItemPredicate() {
        return arg_0 -> AllBlocks.GANTRY_SHAFT.isIn(arg_0);
    }

    @Override
    public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
        PlacementOffset offset = super.getOffset(player, world, state, pos, ray);
        offset.withTransform(offset.getTransform().andThen(s -> (BlockState)s.setValue((Property)POWERED, (Comparable)((Boolean)state.getValue((Property)POWERED)))));
        return offset;
    }
}
