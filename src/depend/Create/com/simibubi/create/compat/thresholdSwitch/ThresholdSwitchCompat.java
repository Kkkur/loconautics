/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.neoforge.items.IItemHandler
 */
package com.simibubi.create.compat.thresholdSwitch;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;

public interface ThresholdSwitchCompat {
    public boolean isFromThisMod(BlockEntity var1);

    public long getSpaceInSlot(IItemHandler var1, int var2);
}
