/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.eriksonn.aeronautics.mixin.levitite;

import dev.eriksonn.aeronautics.content.components.Levitating;
import dev.eriksonn.aeronautics.index.AeroDataComponents;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ItemEntity.class})
public abstract class ItemEntityMixin
extends Entity {
    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract ItemStack getItem();

    @Inject(method={"getDefaultGravity"}, at={@At(value="HEAD")}, cancellable=true)
    private void aeronautics$levitatingGravity(CallbackInfoReturnable<Double> cir) {
        Levitating component = (Levitating)this.getItem().get(AeroDataComponents.LEVITATING);
        if (component != null) {
            cir.setReturnValue((Object)0.0);
        }
    }

    @Inject(method={"tick"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/entity/item/ItemEntity;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V")})
    private void aeronautics$levitatingDragAndSparkles(CallbackInfo ci) {
        Levitating component = (Levitating)this.getItem().get(AeroDataComponents.LEVITATING);
        if (component != null) {
            float dragFraction = Math.clamp(component.dragFraction().floatValue(), 0.0f, 1.0f);
            this.setDeltaMovement(this.getDeltaMovement().scale((double)dragFraction));
            if (this.level().isClientSide && component.particle().isPresent() && this.level().random.nextFloat() < (float)Mth.clamp((int)(this.getItem().getCount() - 10), (int)5, (int)100) / 64.0f) {
                Vec3 ppos = VecHelper.offsetRandomly((Vec3)this.getPosition(0.0f), (RandomSource)this.getRandom(), (float)0.4f).add(0.0, 0.3, 0.0);
                this.level().addParticle(component.particle().get(), ppos.x, ppos.y, ppos.z, 0.0, 0.0, 0.0);
            }
        }
    }
}
