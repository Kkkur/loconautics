/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.damagesource.CombatTracker
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.ryanhcode.sable.mixin.death_message;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={CombatTracker.class})
public class CombatTrackerMixin {
    @Shadow
    @Final
    private LivingEntity mob;

    @WrapOperation(method={"getFallMessage"}, at={@At(value="INVOKE", target="Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;")})
    private MutableComponent sable$getFallMessage(String string, Object[] objects, Operation<MutableComponent> original) {
        LivingEntity entity = this.mob;
        SubLevel subLevel = Sable.HELPER.getLastTrackingSubLevel((Entity)entity);
        if (subLevel != null && subLevel.getName() != null && !subLevel.getName().isEmpty() && Sable.HELPER.getTrackingSubLevel((Entity)entity) != subLevel) {
            return Component.translatable((String)"death.attack.fall.from_sublevel", (Object[])new Object[]{entity.getDisplayName(), subLevel.getName()});
        }
        return (MutableComponent)original.call(new Object[]{string, objects});
    }
}
