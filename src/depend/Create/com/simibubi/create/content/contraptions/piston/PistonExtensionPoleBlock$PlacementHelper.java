/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.Direction
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.DirectionalBlock
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.contraptions.piston;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.placement.PoleHelper;
import java.util.function.Predicate;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.properties.Property;

@MethodsReturnNonnullByDefault
public static class PistonExtensionPoleBlock.PlacementHelper
extends PoleHelper<Direction> {
    private static final PistonExtensionPoleBlock.PlacementHelper instance = new PistonExtensionPoleBlock.PlacementHelper();

    public static PistonExtensionPoleBlock.PlacementHelper get() {
        return instance;
    }

    private PistonExtensionPoleBlock.PlacementHelper() {
        super(arg_0 -> AllBlocks.PISTON_EXTENSION_POLE.has(arg_0), state -> ((Direction)state.getValue((Property)DirectionalBlock.FACING)).getAxis(), DirectionalBlock.FACING);
    }

    public Predicate<ItemStack> getItemPredicate() {
        return arg_0 -> AllBlocks.PISTON_EXTENSION_POLE.isIn(arg_0);
    }
}
