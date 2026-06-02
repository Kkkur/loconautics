/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.data.worldgen.BootstrapContext
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.damagesource.DamageEffects
 *  net.minecraft.world.damagesource.DamageScaling
 *  net.minecraft.world.damagesource.DamageType
 */
package com.simibubi.create;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.damageTypes.DamageTypeBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public class AllDamageTypes {
    public static final ResourceKey<DamageType> CRUSH = AllDamageTypes.key("crush");
    public static final ResourceKey<DamageType> CUCKOO_SURPRISE = AllDamageTypes.key("cuckoo_surprise");
    public static final ResourceKey<DamageType> FAN_FIRE = AllDamageTypes.key("fan_fire");
    public static final ResourceKey<DamageType> FAN_LAVA = AllDamageTypes.key("fan_lava");
    public static final ResourceKey<DamageType> DRILL = AllDamageTypes.key("mechanical_drill");
    public static final ResourceKey<DamageType> ROLLER = AllDamageTypes.key("mechanical_roller");
    public static final ResourceKey<DamageType> SAW = AllDamageTypes.key("mechanical_saw");
    public static final ResourceKey<DamageType> POTATO_CANNON = AllDamageTypes.key("potato_cannon");
    public static final ResourceKey<DamageType> RUN_OVER = AllDamageTypes.key("run_over");

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create((ResourceKey)Registries.DAMAGE_TYPE, (ResourceLocation)Create.asResource(name));
    }

    public static void bootstrap(BootstrapContext<DamageType> ctx) {
        new DamageTypeBuilder(CRUSH).scaling(DamageScaling.ALWAYS).register(ctx);
        new DamageTypeBuilder(CUCKOO_SURPRISE).scaling(DamageScaling.ALWAYS).exhaustion(0.1f).register(ctx);
        new DamageTypeBuilder(FAN_FIRE).effects(DamageEffects.BURNING).register(ctx);
        new DamageTypeBuilder(FAN_LAVA).effects(DamageEffects.BURNING).register(ctx);
        new DamageTypeBuilder(DRILL).register(ctx);
        new DamageTypeBuilder(ROLLER).register(ctx);
        new DamageTypeBuilder(SAW).register(ctx);
        new DamageTypeBuilder(POTATO_CANNON).register(ctx);
        new DamageTypeBuilder(RUN_OVER).register(ctx);
    }
}
