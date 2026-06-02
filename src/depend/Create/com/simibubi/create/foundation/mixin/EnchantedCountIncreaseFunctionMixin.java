/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.storage.loot.LootContext
 *  net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction
 *  net.minecraft.world.level.storage.loot.parameters.LootContextParams
 *  net.minecraft.world.level.storage.loot.providers.number.NumberProvider
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.simibubi.create.foundation.mixin;

import com.simibubi.create.AllDamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={EnchantedCountIncreaseFunction.class})
public abstract class EnchantedCountIncreaseFunctionMixin {
    @Shadow
    @Final
    private NumberProvider value;
    @Shadow
    @Final
    private int limit;

    @Shadow
    protected abstract boolean hasLimit();

    @Inject(method={"run"}, at={@At(value="TAIL")})
    private void create$crushingWheelLooting(ItemStack stack, LootContext context, CallbackInfoReturnable<ItemStack> cir) {
        DamageSource damageSource = (DamageSource)context.getParamOrNull(LootContextParams.DAMAGE_SOURCE);
        if (damageSource != null && damageSource.is(AllDamageTypes.CRUSH)) {
            int lootingLevel = 2;
            float f = (float)lootingLevel * this.value.getFloat(context);
            stack.grow(Math.round(f));
            if (this.hasLimit()) {
                stack.limitSize(this.limit);
            }
        }
    }
}
