/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.component.DataComponentPatch
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.eriksonn.aeronautics.mixin.item_converter;

import dev.eriksonn.aeronautics.content.components.Converter;
import dev.eriksonn.aeronautics.content.components.Levitating;
import dev.eriksonn.aeronautics.index.AeroDataComponents;
import dev.eriksonn.aeronautics.index.AeroTags;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ItemEntity.class})
public abstract class ItemEntityMixin {
    @Inject(method={"tick"}, at={@At(value="TAIL")})
    private void aeronautics$tick(CallbackInfo ci) {
        ItemEntity entity = (ItemEntity)this;
        if (entity.getItem().isEmpty() || entity.isRemoved()) {
            return;
        }
        ItemStack item = entity.getItem();
        Level level = entity.level();
        if (item.has(AeroDataComponents.CONVERTER)) {
            Converter converter = (Converter)item.get(AeroDataComponents.CONVERTER);
            Converter.tick(level, entity, item, converter);
        }
        if (level.dimension().equals(Level.OVERWORLD) && item.is(AeroTags.ItemTags.CONVERTS_TO_CLOUD_SKIPPER) && entity.getY() >= 192.0 && entity.getY() <= 196.0 && !item.has(AeroDataComponents.CONVERTER)) {
            DataComponentPatch patch = DataComponentPatch.builder().set(AeroDataComponents.CONVERTER, (Object)Converter.cloudSkipper()).set(AeroDataComponents.LEVITATING, (Object)Levitating.DEFAULT).build();
            item.applyComponents(patch);
            entity.setDeltaMovement(entity.getDeltaMovement().scale(0.5));
        }
    }
}
