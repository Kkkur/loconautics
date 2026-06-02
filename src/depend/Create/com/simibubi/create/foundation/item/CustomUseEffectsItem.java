/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.TriState
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.foundation.item;

import net.createmod.catnip.data.TriState;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface CustomUseEffectsItem {
    default public TriState shouldTriggerUseEffects(ItemStack stack, LivingEntity entity) {
        return TriState.DEFAULT;
    }

    public boolean triggerUseEffects(ItemStack var1, LivingEntity var2, int var3, RandomSource var4);
}
