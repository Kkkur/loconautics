/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.PonderScene$SceneTransform
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.simulated_team.simulated.mixin.ponder;

import com.llamalad7.mixinextras.sugar.Local;
import dev.simulated_team.simulated.mixin_interface.ponder.PonderSceneExtension;
import net.createmod.ponder.foundation.PonderScene;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={PonderScene.SceneTransform.class})
public abstract class PonderSceneTransformMixin {
    @Redirect(method={"apply(Lcom/mojang/blaze3d/vertex/PoseStack;F)Lcom/mojang/blaze3d/vertex/PoseStack;"}, at=@At(value="FIELD", target="Lnet/createmod/ponder/foundation/PonderScene;scaleFactor:F"))
    private float interpolateScaleFactor(PonderScene instance, @Local(argsOnly=true) float pt) {
        return ((PonderSceneExtension)instance).simulated$getScale(pt);
    }

    @Redirect(method={"apply(Lcom/mojang/blaze3d/vertex/PoseStack;F)Lcom/mojang/blaze3d/vertex/PoseStack;"}, at=@At(value="FIELD", target="Lnet/createmod/ponder/foundation/PonderScene;yOffset:F"))
    private float interpolateYOffset(PonderScene instance, @Local(argsOnly=true) float pt) {
        return ((PonderSceneExtension)instance).simulated$getYOffset(pt);
    }
}
