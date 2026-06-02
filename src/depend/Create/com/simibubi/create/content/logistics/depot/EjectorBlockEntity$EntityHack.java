/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package com.simibubi.create.content.logistics.depot;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

private static abstract class EjectorBlockEntity.EntityHack
extends Entity {
    public EjectorBlockEntity.EntityHack(EntityType<?> p_i48580_1_, Level p_i48580_2_) {
        super(p_i48580_1_, p_i48580_2_);
    }

    public static void setElytraFlying(Entity e) {
        SynchedEntityData data = e.getEntityData();
        data.set(DATA_SHARED_FLAGS_ID, (Object)((byte)((Byte)data.get(DATA_SHARED_FLAGS_ID) | 0x80)));
    }
}
