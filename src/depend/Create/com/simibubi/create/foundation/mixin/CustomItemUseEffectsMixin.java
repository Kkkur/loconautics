/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.TriState
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.simibubi.create.foundation.mixin;

import com.simibubi.create.foundation.item.CustomUseEffectsItem;
import net.createmod.catnip.data.TriState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={LivingEntity.class})
public abstract class CustomItemUseEffectsMixin
extends Entity {
    private CustomItemUseEffectsMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract ItemStack getUseItem();

    @Inject(method={"shouldTriggerItemUseEffects()Z"}, at={@At(value="HEAD")}, cancellable=true)
    private void create$onShouldTriggerUseEffects(CallbackInfoReturnable<Boolean> cir) {
        CustomUseEffectsItem handler;
        TriState result;
        ItemStack using = this.getUseItem();
        Item item = using.getItem();
        if (item instanceof CustomUseEffectsItem && (result = (handler = (CustomUseEffectsItem)item).shouldTriggerUseEffects(using, (LivingEntity)this)) != TriState.DEFAULT) {
            cir.setReturnValue((Object)result.getValue());
        }
    }

    @Inject(method={"triggerItemUseEffects(Lnet/minecraft/world/item/ItemStack;I)V"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/item/ItemStack;getUseAnimation()Lnet/minecraft/world/item/UseAnim;", ordinal=0)}, cancellable=true)
    private void create$onTriggerUseEffects(ItemStack stack, int count, CallbackInfo ci) {
        CustomUseEffectsItem handler;
        Item item = stack.getItem();
        if (item instanceof CustomUseEffectsItem && (handler = (CustomUseEffectsItem)item).triggerUseEffects(stack, (LivingEntity)this, count, this.random)) {
            ci.cancel();
        }
    }
}
