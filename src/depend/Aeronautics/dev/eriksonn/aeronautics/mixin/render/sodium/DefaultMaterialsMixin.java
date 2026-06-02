/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.DefaultMaterials
 *  net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material
 *  net.minecraft.client.renderer.RenderType
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.eriksonn.aeronautics.mixin.render.sodium;

import dev.eriksonn.aeronautics.index.client.AeroRenderTypes;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.DefaultMaterials;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={DefaultMaterials.class})
public class DefaultMaterialsMixin {
    @Shadow
    @Final
    public static Material SOLID;

    @Inject(method={"forRenderLayer"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/RenderType;translucent()Lnet/minecraft/client/renderer/RenderType;")}, cancellable=true)
    private static void aeronautics$injectMaterialMapping(RenderType layer, CallbackInfoReturnable<Material> cir) {
        if (layer == AeroRenderTypes.levitite()) {
            cir.setReturnValue((Object)SOLID);
        } else if (layer == AeroRenderTypes.levititeGhosts()) {
            cir.setReturnValue((Object)SOLID);
        }
    }
}
