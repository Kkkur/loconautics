/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.HorizontalDirectionalBlock
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.contraptions.actors.roller;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.placement.PoleHelper;
import java.util.function.Predicate;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.properties.Property;

private static class RollerBlock.PlacementHelper
extends PoleHelper<Direction> {
    public RollerBlock.PlacementHelper() {
        super(arg_0 -> AllBlocks.MECHANICAL_ROLLER.has(arg_0), state -> ((Direction)state.getValue((Property)HorizontalDirectionalBlock.FACING)).getClockWise().getAxis(), HorizontalDirectionalBlock.FACING);
    }

    public Predicate<ItemStack> getItemPredicate() {
        return arg_0 -> AllBlocks.MECHANICAL_ROLLER.isIn(arg_0);
    }
}
