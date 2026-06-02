/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.renderer.block.ModelBlockRenderer
 *  net.minecraft.client.renderer.block.ModelBlockRenderer$Cache
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.BlockAndTintGetter
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 */
package dev.ryanhcode.sable.mixin.dynamic_directional_shading;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.ryanhcode.sable.mixinterface.dynamic_directional_shading.ModelBlockRendererCacheExtension;
import dev.ryanhcode.sable.render.dynamic_shade.SableDynamicDirectionalShading;
import dev.ryanhcode.sable.render.dynamic_shade.SubLevelVertexConsumer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value={ModelBlockRenderer.class})
public class ModelBlockRendererMixin {
    @Shadow
    @Final
    public static ThreadLocal<ModelBlockRenderer.Cache> CACHE;

    @ModifyVariable(method={"putQuadData"}, at=@At(value="HEAD"), ordinal=0, argsOnly=true)
    private VertexConsumer sable$modifyConsumer(VertexConsumer value) {
        return SableDynamicDirectionalShading.isEnabled() && ((ModelBlockRendererCacheExtension)CACHE.get()).sable$getOnSubLevel() ? new SubLevelVertexConsumer(value) : value;
    }

    @WrapOperation(method={"renderModelFaceFlat"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/level/BlockAndTintGetter;getShade(Lnet/minecraft/core/Direction;Z)F")})
    public float getShade(BlockAndTintGetter instance, Direction direction, boolean cull, Operation<Float> original) {
        boolean onSubLevel = SableDynamicDirectionalShading.isEnabled() && ((ModelBlockRendererCacheExtension)ModelBlockRenderer.CACHE.get()).sable$getOnSubLevel();
        return onSubLevel ? 1.0f : ((Float)original.call(new Object[]{instance, direction, cull})).floatValue();
    }
}
