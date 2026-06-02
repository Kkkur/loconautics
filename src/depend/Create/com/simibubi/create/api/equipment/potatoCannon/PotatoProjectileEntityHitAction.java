/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.EntityHitResult
 */
package com.simibubi.create.api.equipment.potatoCannon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import java.util.function.Function;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;

public interface PotatoProjectileEntityHitAction {
    public static final Codec<PotatoProjectileEntityHitAction> CODEC = CreateBuiltInRegistries.POTATO_PROJECTILE_ENTITY_HIT_ACTION.byNameCodec().dispatch(PotatoProjectileEntityHitAction::codec, Function.identity());

    public boolean execute(ItemStack var1, EntityHitResult var2, Type var3);

    public MapCodec<? extends PotatoProjectileEntityHitAction> codec();

    public static enum Type {
        PRE_HIT,
        ON_HIT;

    }
}
