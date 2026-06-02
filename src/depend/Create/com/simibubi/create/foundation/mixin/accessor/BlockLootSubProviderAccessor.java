/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.data.loot.BlockLootSubProvider
 *  net.minecraft.world.level.storage.loot.predicates.LootItemCondition$Builder
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package com.simibubi.create.foundation.mixin.accessor;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={BlockLootSubProvider.class})
public interface BlockLootSubProviderAccessor {
    @Invoker(value="hasSilkTouch")
    public LootItemCondition.Builder create$hasSilkTouch();
}
