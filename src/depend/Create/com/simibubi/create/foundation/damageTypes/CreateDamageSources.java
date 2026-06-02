/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.damagesource.DamageType
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.damageTypes;

import com.simibubi.create.AllDamageTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

public class CreateDamageSources {
    public static DamageSource crush(Level level) {
        return CreateDamageSources.source(AllDamageTypes.CRUSH, (LevelReader)level);
    }

    public static DamageSource cuckooSurprise(Level level) {
        return CreateDamageSources.source(AllDamageTypes.CUCKOO_SURPRISE, (LevelReader)level);
    }

    public static DamageSource fanFire(Level level) {
        return CreateDamageSources.source(AllDamageTypes.FAN_FIRE, (LevelReader)level);
    }

    public static DamageSource fanLava(Level level) {
        return CreateDamageSources.source(AllDamageTypes.FAN_LAVA, (LevelReader)level);
    }

    public static DamageSource drill(Level level) {
        return CreateDamageSources.source(AllDamageTypes.DRILL, (LevelReader)level);
    }

    public static DamageSource roller(Level level) {
        return CreateDamageSources.source(AllDamageTypes.ROLLER, (LevelReader)level);
    }

    public static DamageSource saw(Level level) {
        return CreateDamageSources.source(AllDamageTypes.SAW, (LevelReader)level);
    }

    public static DamageSource potatoCannon(Level level, Entity causingEntity, Entity directEntity) {
        return CreateDamageSources.source(AllDamageTypes.POTATO_CANNON, (LevelReader)level, causingEntity, directEntity);
    }

    public static DamageSource runOver(Level level, Entity entity) {
        return CreateDamageSources.source(AllDamageTypes.RUN_OVER, (LevelReader)level, entity);
    }

    private static DamageSource source(ResourceKey<DamageType> key, LevelReader level) {
        Registry registry = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        return new DamageSource((Holder)registry.getHolderOrThrow(key));
    }

    private static DamageSource source(ResourceKey<DamageType> key, LevelReader level, @Nullable Entity entity) {
        Registry registry = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        return new DamageSource((Holder)registry.getHolderOrThrow(key), entity);
    }

    private static DamageSource source(ResourceKey<DamageType> key, LevelReader level, @Nullable Entity causingEntity, @Nullable Entity directEntity) {
        Registry registry = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        return new DamageSource((Holder)registry.getHolderOrThrow(key), causingEntity, directEntity);
    }
}
