/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.util.ExtraCodecs
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.EntityHitResult
 */
package com.simibubi.create.content.equipment.potatoCannon;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileEntityHitAction;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;

public record AllPotatoProjectileEntityHitActions.SetOnFire(int ticks) implements PotatoProjectileEntityHitAction
{
    public static final MapCodec<AllPotatoProjectileEntityHitActions.SetOnFire> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)ExtraCodecs.POSITIVE_INT.fieldOf("ticks").forGetter(AllPotatoProjectileEntityHitActions.SetOnFire::ticks)).apply((Applicative)instance, AllPotatoProjectileEntityHitActions.SetOnFire::new));

    public static AllPotatoProjectileEntityHitActions.SetOnFire seconds(int seconds) {
        return new AllPotatoProjectileEntityHitActions.SetOnFire(seconds * 20);
    }

    @Override
    public boolean execute(ItemStack projectile, EntityHitResult ray, PotatoProjectileEntityHitAction.Type type) {
        ray.getEntity().setRemainingFireTicks(this.ticks);
        return false;
    }

    @Override
    public MapCodec<? extends PotatoProjectileEntityHitAction> codec() {
        return CODEC;
    }
}
