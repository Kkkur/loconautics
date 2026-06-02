/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.render.SuperRenderTypeBuffer
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.PonderScene$SceneCamera
 *  net.createmod.ponder.foundation.PonderScene$SceneTransform
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.simulated_team.simulated.mixin.ponder;

import dev.simulated_team.simulated.mixin_interface.ponder.PonderSceneExtension;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.createmod.ponder.foundation.PonderScene;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={PonderScene.class})
public class PonderSceneMixin
implements PonderSceneExtension {
    @Shadow
    float scaleFactor;
    @Shadow
    float yOffset;
    @Unique
    LerpedFloat simulated$shadowAlpha = LerpedFloat.linear();
    @Unique
    Vec3 simulated$shadowOffset;
    @Unique
    Vec3 simulated$oldShadowOffset;
    @Unique
    float simulated$oldScaleFactor;
    @Unique
    float simulated$oldYOffset;
    @Shadow
    private PonderScene.SceneTransform transform;

    @Inject(remap=false, method={"begin"}, at={@At(value="TAIL")})
    public void tailBegin(CallbackInfo ci) {
        this.simulated$shadowAlpha.chase(1.0, 0.1, LerpedFloat.Chaser.LINEAR).startWithValue(1.0);
        this.simulated$shadowOffset = Vec3.ZERO;
        this.simulated$oldShadowOffset = Vec3.ZERO;
    }

    @Inject(remap=false, method={"tick"}, at={@At(value="HEAD")})
    public void headTick(CallbackInfo ci) {
        this.simulated$shadowAlpha.tickChaser();
        this.simulated$oldShadowOffset = this.simulated$shadowOffset;
    }

    @Override
    public void simulated$toggleRenderBasePlateShadow() {
        this.simulated$shadowAlpha.updateChaseTarget(1.0f - this.simulated$shadowAlpha.getChaseTarget());
    }

    @Override
    public float simulated$getBasePlateAnimationTimer(float partialTicks) {
        return this.simulated$shadowAlpha.getValue(partialTicks);
    }

    @Override
    public Vec3 simulated$getShadowOffset(float pt) {
        return this.simulated$shadowOffset.scale((double)pt).add(this.simulated$oldShadowOffset.scale((double)(1.0f - pt)));
    }

    @Override
    public void simulated$setShadowOffset(Vec3 shadowOffset) {
        this.simulated$shadowOffset = shadowOffset;
    }

    @Override
    public void simulated$setOldShadowOffset(Vec3 oldShadowOffset) {
        this.simulated$oldShadowOffset = oldShadowOffset;
    }

    @Override
    public void simulated$moveShadowOffset(Vec3 shadowDelta) {
        this.simulated$shadowOffset = this.simulated$shadowOffset.add(shadowDelta);
    }

    @Override
    public void simulated$setScaleFactor(float scale) {
        this.scaleFactor = scale;
    }

    @Override
    public float simulated$getScale(float pt) {
        return Mth.lerp((float)pt, (float)this.simulated$oldScaleFactor, (float)this.scaleFactor);
    }

    @Override
    public void simulated$setYOffset(float yOffset) {
        this.yOffset = yOffset;
    }

    @Override
    public float simulated$getYOffset(float pt) {
        return Mth.lerp((float)pt, (float)this.simulated$oldYOffset, (float)this.yOffset);
    }

    @Inject(method={"tick"}, at={@At(value="HEAD")})
    private void recordOldValues(CallbackInfo ci) {
        this.simulated$oldYOffset = this.yOffset;
        this.simulated$oldScaleFactor = this.scaleFactor;
    }

    @Redirect(remap=false, method={"renderScene"}, at=@At(value="INVOKE", target="Lnet/createmod/ponder/foundation/PonderScene$SceneCamera;set(FF)V"))
    public void onCameraSet(PonderScene.SceneCamera instance, float xRotation, float yRotation, SuperRenderTypeBuffer buffer, GuiGraphics graphics, float pt) {
        instance.set(-this.transform.xRotation.getValue(pt), this.transform.yRotation.getValue(pt) + 180.0f);
    }
}
