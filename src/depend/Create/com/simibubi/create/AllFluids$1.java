/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.dispenser.BlockSource
 *  net.minecraft.core.dispenser.DefaultDispenseItemBehavior
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.item.DispensibleContainerItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.DispenserBlock
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.properties.Property;

class AllFluids.1
extends DefaultDispenseItemBehavior {
    AllFluids.1() {
    }

    protected ItemStack execute(BlockSource pSource, ItemStack pStack) {
        DispensibleContainerItem dispensibleContainerItem = (DispensibleContainerItem)pStack.getItem();
        BlockPos pos = pSource.pos().relative((Direction)pSource.state().getValue((Property)DispenserBlock.FACING));
        ServerLevel level = pSource.level();
        if (dispensibleContainerItem.emptyContents(null, (Level)level, pos, null, pStack)) {
            return new ItemStack((ItemLike)Items.BUCKET);
        }
        return DEFAULT.dispense(pSource, pStack);
    }
}
