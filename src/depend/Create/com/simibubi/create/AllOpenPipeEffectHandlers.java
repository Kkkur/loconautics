/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.level.material.Fluid
 *  net.neoforged.neoforge.common.Tags$Fluids
 */
package com.simibubi.create;

import com.simibubi.create.AllFluids;
import com.simibubi.create.api.effect.OpenPipeEffectHandler;
import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.impl.effect.LavaEffectHandler;
import com.simibubi.create.impl.effect.MilkEffectHandler;
import com.simibubi.create.impl.effect.PotionEffectHandler;
import com.simibubi.create.impl.effect.TeaEffectHandler;
import com.simibubi.create.impl.effect.WaterEffectHandler;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.Tags;

public class AllOpenPipeEffectHandlers {
    public static void registerDefaults() {
        OpenPipeEffectHandler.REGISTRY.registerProvider(SimpleRegistry.Provider.forFluidTag((TagKey<Fluid>)FluidTags.WATER, new WaterEffectHandler()));
        OpenPipeEffectHandler.REGISTRY.registerProvider(SimpleRegistry.Provider.forFluidTag((TagKey<Fluid>)FluidTags.LAVA, new LavaEffectHandler()));
        OpenPipeEffectHandler.REGISTRY.registerProvider(SimpleRegistry.Provider.forFluidTag((TagKey<Fluid>)Tags.Fluids.MILK, new MilkEffectHandler()));
        OpenPipeEffectHandler.REGISTRY.register((Fluid)AllFluids.POTION.getSource(), new PotionEffectHandler());
        OpenPipeEffectHandler.REGISTRY.register((Fluid)AllFluids.TEA.getSource(), new TeaEffectHandler());
    }
}
