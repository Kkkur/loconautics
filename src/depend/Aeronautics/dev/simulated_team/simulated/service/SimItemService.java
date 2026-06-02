/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.DyeItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 */
package dev.simulated_team.simulated.service;

import dev.simulated_team.simulated.service.ServiceUtil;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface SimItemService {
    public static final SimItemService INSTANCE = ServiceUtil.load(SimItemService.class);

    public static DyeColor getDyeColor(ItemStack itemStack) {
        DyeColor dyeColor;
        Item item = itemStack.getItem();
        if (item instanceof DyeItem) {
            DyeItem dyeItem = (DyeItem)item;
            dyeColor = dyeItem.getDyeColor();
        } else {
            dyeColor = null;
        }
        return dyeColor;
    }

    public int getBurnTime(ItemStack var1);

    public int getSuperheatedBurnTime(ItemStack var1);
}
