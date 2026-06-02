/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.Item$Properties
 */
package com.simibubi.create.content.legacy;

import com.simibubi.create.content.legacy.NoGravMagicalDohickyItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;

public class ShadowSteelItem
extends NoGravMagicalDohickyItem {
    public ShadowSteelItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    protected void onCreated(ItemEntity entity, CompoundTag persistentData) {
        super.onCreated(entity, persistentData);
        float yMotion = (entity.fallDistance + 3.0f) / 50.0f;
        entity.setDeltaMovement(0.0, (double)yMotion, 0.0);
    }

    @Override
    protected float getIdleParticleChance(ItemEntity entity) {
        return (float)(Mth.clamp((double)(entity.getItem().getCount() - 10), (double)Mth.clamp((double)(entity.getDeltaMovement().y * 20.0), (double)5.0, (double)20.0), (double)100.0) / 64.0);
    }
}
