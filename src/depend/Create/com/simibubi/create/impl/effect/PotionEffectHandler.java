/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.alchemy.PotionContents
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.impl.effect;

import com.simibubi.create.api.effect.OpenPipeEffectHandler;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;

public class PotionEffectHandler
implements OpenPipeEffectHandler {
    @Override
    public void apply(Level level, AABB area, FluidStack fluid) {
        PotionContents contents = PotionEffectHandler.getContents(fluid);
        if (contents == PotionContents.EMPTY) {
            return;
        }
        List entities = level.getEntitiesOfClass(LivingEntity.class, area, LivingEntity::isAffectedByPotions);
        for (LivingEntity entity : entities) {
            contents.forEachEffect(effectInstance -> {
                MobEffect effect = (MobEffect)effectInstance.getEffect().value();
                if (effect.isInstantenous()) {
                    effect.applyInstantenousEffect(null, null, entity, effectInstance.getAmplifier(), 0.5);
                } else {
                    entity.addEffect(new MobEffectInstance(effectInstance));
                }
            });
        }
    }

    private static PotionContents getContents(FluidStack fluid) {
        FluidStack copy = fluid.copy();
        copy.setAmount(250);
        ItemStack bottle = PotionFluidHandler.fillBottle(new ItemStack((ItemLike)Items.GLASS_BOTTLE), copy);
        return (PotionContents)bottle.getOrDefault(DataComponents.POTION_CONTENTS, (Object)PotionContents.EMPTY);
    }
}
