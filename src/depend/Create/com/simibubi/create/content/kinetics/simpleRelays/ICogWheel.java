/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.kinetics.simpleRelays;

import com.simibubi.create.content.kinetics.base.IRotate;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface ICogWheel
extends IRotate {
    public static boolean isSmallCog(BlockState state) {
        return ICogWheel.isSmallCog(state.getBlock());
    }

    public static boolean isLargeCog(BlockState state) {
        return ICogWheel.isLargeCog(state.getBlock());
    }

    public static boolean isSmallCog(Block block) {
        return block instanceof ICogWheel && ((ICogWheel)block).isSmallCog();
    }

    public static boolean isLargeCog(Block block) {
        return block instanceof ICogWheel && ((ICogWheel)block).isLargeCog();
    }

    public static boolean isDedicatedCogWheel(Block block) {
        return block instanceof ICogWheel && ((ICogWheel)block).isDedicatedCogWheel();
    }

    public static boolean isDedicatedCogItem(ItemStack test) {
        Item item = test.getItem();
        if (!(item instanceof BlockItem)) {
            return false;
        }
        return ICogWheel.isDedicatedCogWheel(((BlockItem)item).getBlock());
    }

    public static boolean isSmallCogItem(ItemStack test) {
        Item item = test.getItem();
        if (!(item instanceof BlockItem)) {
            return false;
        }
        return ICogWheel.isSmallCog(((BlockItem)item).getBlock());
    }

    public static boolean isLargeCogItem(ItemStack test) {
        Item item = test.getItem();
        if (!(item instanceof BlockItem)) {
            return false;
        }
        return ICogWheel.isLargeCog(((BlockItem)item).getBlock());
    }

    default public boolean isLargeCog() {
        return false;
    }

    default public boolean isSmallCog() {
        return !this.isLargeCog();
    }

    default public boolean isDedicatedCogWheel() {
        return false;
    }
}
