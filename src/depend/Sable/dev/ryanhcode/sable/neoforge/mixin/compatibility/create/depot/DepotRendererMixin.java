/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyExpressionValue
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.content.logistics.depot.DepotRenderer
 *  net.minecraft.core.Position
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.depot;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.depot.DepotRenderer;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.core.Position;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={DepotRenderer.class})
public class DepotRendererMixin {
    @ModifyExpressionValue(method={"renderItem"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/Camera;getPosition()Lnet/minecraft/world/phys/Vec3;")})
    private static Vec3 sable$renderViewEntityPosition(Vec3 original, @Local(argsOnly=true) Vec3 position) {
        ClientSubLevel subLevel = Sable.HELPER.getContainingClient((Position)position);
        if (subLevel != null) {
            return subLevel.renderPose().transformPositionInverse(original);
        }
        return original;
    }
}
