package com.lycoris.loconautics.mixin.client;

import com.lycoris.loconautics.allsable.BogeyVisualSmoother;
import com.lycoris.loconautics.allsable.BogeyWheelAnimator;
import com.lycoris.loconautics.allsable.BogeyYawVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
import com.simibubi.create.content.trains.bogey.BogeyBlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Applies the Loconautics "local yaw" (see {@link BogeyYawVisual}) on the <b>vanilla</b> bogey render
 * path ({@link BogeyBlockEntityRenderer}), rotating the whole bogey model around the block's vertical
 * centerline.
 *
 * <p>This is the path that actually draws a bogey riding inside a Sable sub-level: Sable renders
 * sub-level block entities through the vanilla {@code BlockEntityRenderDispatcher}
 * ({@code VanillaSubLevelBlockEntityRenderer.renderSingleBE}), NOT through Flywheel visuals — so the
 * companion mixin on {@code BogeyBlockEntityVisual} ({@link BogeyYawVisualMixin}) never fires for
 * train bogeys. Both mixins are kept so the yaw shows up regardless of which path renders the block.
 *
 * <p>Unlike the Flywheel visual's persistent pose stack, the {@code PoseStack} here is pushed/popped
 * around every frame by the caller, so we simply apply the <b>absolute</b> yaw each frame (no delta
 * bookkeeping). The rotation is sandwiched between a center/un-center translation so the model pivots
 * in place instead of sweeping around the block corner.
 *
 * <p>The injection targets the exact descriptor (with {@code AbstractBogeyBlockEntity}) so it does NOT
 * also match the synthetic bridge method {@code renderSafe(BlockEntity, ...)} — matching both would
 * apply the rotation twice.
 */
@Mixin(value = BogeyBlockEntityRenderer.class, remap = false)
public abstract class BogeyYawRendererMixin {

    @Inject(
            method = "renderSafe(Lcom/simibubi/create/content/trains/bogey/AbstractBogeyBlockEntity;"
                    + "FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At("HEAD"))
    private void loconautics$applyYaw(AbstractBogeyBlockEntity be, float partialTicks, PoseStack ms,
                                      MultiBufferSource buffer, int light, int overlay, CallbackInfo ci) {
        // Spin the wheels/side rods by the distance travelled this frame (Create's own animate() path).
        BogeyWheelAnimator.frame(be);

        // The server computes each bogey's exact rail point + tangent per tick (its RailCarriage IS the
        // pair of TravellingPoints Create's own bogeys ride on) and syncs them via NBT. Smooth those
        // 20 Hz values per frame — exactly Create's LerpedFloat on CarriageBogey.yaw — so the wheels
        // turn fluidly instead of stepping.
        Vector3f off = new Vector3f();
        float targetYaw = BogeyYawVisual.getLocalYaw(be);
        BogeyYawVisual.getLocalOffset(be, off);
        float[] eased = BogeyVisualSmoother.smooth(be, targetYaw, off.x, off.y, off.z);
        float yaw = eased[0];
        off.set(eased[1], eased[2], eased[3]);
        boolean hasYaw = Math.abs(yaw) >= 1.0e-4f;
        boolean hasOff = off.lengthSquared() >= 1.0e-8f;
        if (!hasYaw && !hasOff) {
            return;
        }
        // The caller (vanilla LevelRenderer / Sable's VanillaSubLevelBlockEntityRenderer) push/pops around
        // this render call, so mutating the stack at HEAD only affects this bogey's draw. At HEAD the stack
        // sits at the block's min corner with the sub-level's axes — the same frame the offset is expressed in.
        // 1) Slide the model from its rigid (chord) position onto its actual point on the rail.
        if (hasOff) {
            ms.translate(off.x, off.y, off.z);
        }
        // 2) Pivot about the block's vertical centerline: center, rotate around Y, un-center.
        if (hasYaw) {
            ms.translate(0.5f, 0.5f, 0.5f);
            ms.mulPose(Axis.YP.rotationDegrees(yaw));
            ms.translate(-0.5f, -0.5f, -0.5f);
        }
    }
}
