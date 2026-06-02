/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.item.ItemStack
 */
package dev.simulated_team.simulated.content.blocks.auger_shaft;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

public interface ItemReciever {
    public ItemStack onRecieveItem(ItemStack var1, BlockPos var2);

    public boolean removed();

    public boolean isActive();
}
