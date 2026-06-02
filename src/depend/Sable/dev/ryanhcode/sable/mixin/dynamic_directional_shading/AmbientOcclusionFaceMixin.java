/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.minecraft.client.renderer.block.ModelBlockRenderer
 *  net.minecraft.client.renderer.block.ModelBlockRenderer$AmbientOcclusionFace
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.BlockAndTintGetter
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.ryanhcode.sable.mixin.dynamic_directional_shading;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ryanhcode.sable.mixinterface.dynamic_directional_shading.ModelBlockRendererCacheExtension;
import dev.ryanhcode.sable.render.dynamic_shade.SableDynamicDirectionalShading;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={ModelBlockRenderer.AmbientOcclusionFace.class})
public class AmbientOcclusionFaceMixin {
    @WrapOperation(method={"calculate"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/level/BlockAndTintGetter;getShade(Lnet/minecraft/core/Direction;Z)F")})
    private float calculate(BlockAndTintGetter instance, Direction direction, boolean cull, Operation<Float> original) {
        boolean onSubLevel = SableDynamicDirectionalShading.isEnabled() && ((ModelBlockRendererCacheExtension)ModelBlockRenderer.CACHE.get()).sable$getOnSubLevel();
        return onSubLevel ? 1.0f : ((Float)original.call(new Object[]{instance, direction, cull})).floatValue();
    }
}
