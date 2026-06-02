/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.simibubi.create.api.equipment.potatoCannon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import java.util.function.Function;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.BlockHitResult;

public interface PotatoProjectileBlockHitAction {
    public static final Codec<PotatoProjectileBlockHitAction> CODEC = CreateBuiltInRegistries.POTATO_PROJECTILE_BLOCK_HIT_ACTION.byNameCodec().dispatch(PotatoProjectileBlockHitAction::codec, Function.identity());

    public boolean execute(LevelAccessor var1, ItemStack var2, BlockHitResult var3);

    public MapCodec<? extends PotatoProjectileBlockHitAction> codec();
}
