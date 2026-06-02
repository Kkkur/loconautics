/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.legacy;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class NoGravMagicalDohickyItem
extends Item {
    public NoGravMagicalDohickyItem(Item.Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        Level world = entity.level();
        Vec3 pos = entity.position();
        CompoundTag persistentData = entity.getPersistentData();
        if (world.isClientSide) {
            if (world.random.nextFloat() < this.getIdleParticleChance(entity)) {
                Vec3 ppos = VecHelper.offsetRandomly((Vec3)pos, (RandomSource)world.random, (float)0.5f);
                world.addParticle((ParticleOptions)ParticleTypes.END_ROD, ppos.x, pos.y, ppos.z, 0.0, (double)-0.1f, 0.0);
            }
            if (entity.isSilent() && !persistentData.getBoolean("PlayEffects")) {
                Vec3 basemotion = new Vec3(0.0, 1.0, 0.0);
                world.addParticle((ParticleOptions)ParticleTypes.FLASH, pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
                for (int i = 0; i < 20; ++i) {
                    Vec3 motion = VecHelper.offsetRandomly((Vec3)basemotion, (RandomSource)world.random, (float)1.0f);
                    world.addParticle((ParticleOptions)ParticleTypes.WITCH, pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
                    world.addParticle((ParticleOptions)ParticleTypes.END_ROD, pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
                }
                persistentData.putBoolean("PlayEffects", true);
            }
            return false;
        }
        entity.setNoGravity(true);
        if (!persistentData.contains("JustCreated")) {
            return false;
        }
        this.onCreated(entity, persistentData);
        return false;
    }

    protected float getIdleParticleChance(ItemEntity entity) {
        return (float)Mth.clamp((int)(entity.getItem().getCount() - 10), (int)5, (int)100) / 64.0f;
    }

    protected void onCreated(ItemEntity entity, CompoundTag persistentData) {
        entity.lifespan = 6000;
        persistentData.remove("JustCreated");
        entity.setSilent(true);
    }
}
