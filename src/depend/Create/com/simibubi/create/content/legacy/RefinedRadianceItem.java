/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.legacy;

import com.simibubi.create.content.legacy.NoGravMagicalDohickyItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RefinedRadianceItem
extends NoGravMagicalDohickyItem {
    public RefinedRadianceItem(Item.Properties properties) {
        super(properties);
    }

    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    protected void onCreated(ItemEntity entity, CompoundTag persistentData) {
        super.onCreated(entity, persistentData);
        entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, 0.25, 0.0));
    }
}
