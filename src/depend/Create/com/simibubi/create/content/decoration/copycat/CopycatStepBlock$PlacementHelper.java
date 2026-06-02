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
 *  net.minecraft.world.level.block.state.properties.Half
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.decoration.copycat;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.placement.PoleHelper;
import java.util.function.Predicate;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

private static class CopycatStepBlock.PlacementHelper
extends PoleHelper<Direction> {
    public CopycatStepBlock.PlacementHelper() {
        super(arg_0 -> AllBlocks.COPYCAT_STEP.has(arg_0), state -> ((Direction)state.getValue((Property)FACING)).getClockWise().getAxis(), FACING);
    }

    @NotNull
    public Predicate<ItemStack> getItemPredicate() {
        return arg_0 -> AllBlocks.COPYCAT_STEP.isIn(arg_0);
    }

    @Override
    @NotNull
    public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
        PlacementOffset offset = super.getOffset(player, world, state, pos, ray);
        if (offset.isSuccessful()) {
            offset.withTransform(offset.getTransform().andThen(s -> (BlockState)s.setValue(HALF, (Comparable)((Half)state.getValue(HALF)))));
        }
        return offset;
    }
}
