/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.neoforged.neoforge.common.EffectCures
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.impl.effect;

import com.simibubi.create.api.effect.OpenPipeEffectHandler;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.fluids.FluidStack;

public class MilkEffectHandler
implements OpenPipeEffectHandler {
    @Override
    public void apply(Level level, AABB area, FluidStack fluid) {
        if (level.getGameTime() % 5L != 0L) {
            return;
        }
        List entities = level.getEntitiesOfClass(LivingEntity.class, area, LivingEntity::isAffectedByPotions);
        for (LivingEntity entity : entities) {
            entity.removeEffectsCuredBy(EffectCures.MILK);
        }
    }
}
