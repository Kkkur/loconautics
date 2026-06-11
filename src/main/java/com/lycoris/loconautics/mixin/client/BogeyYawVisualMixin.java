package com.lycoris.loconautics.mixin.client;

import com.lycoris.loconautics.allsable.BogeyWheelAnimator;
import com.lycoris.loconautics.allsable.BogeyYawVisual;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
import com.simibubi.create.content.trains.bogey.BogeyBlockEntityVisual;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Applies an extra "local yaw" rotation to a bogey's render pose stack, around the bogey's own
 * vertical (Y) centerline.
 *
 * <p>In Loconautics' all-Sable trains, bogey blocks stay part of the cart's body sub-level (they are
 * never detached/separated like Create's contraption bogeys). To still get the visual effect of a
 * bogey pivoting under the cart on curves, {@link com.lycoris.loconautics.allsable.SableTrainDriver}
 * computes a per-bogey yaw each tick (the angle between the body's forward direction and the rail
 * tangent directly under that bogey) and stores it via {@link BogeyYawVisual}. This mixin reads that
 * value every frame and rotates the bogey's entire wheel/frame/shaft assembly to match.
 *
 * <p>{@link BogeyBlockEntityVisual#poseStack} is a single persistent matrix that is never
 * pushed/popped, so we can't apply-then-undo a rotation each frame. Instead we track the
 * <em>previously applied</em> yaw ({@link #loconautics$lastYaw}) and only multiply in the
 * <b>delta</b> between frames — net effect is the pose stack always carries exactly the bogey's
 * current target yaw.
 */
@Mixin(value = BogeyBlockEntityVisual.class, remap = false)
public abstract class BogeyYawVisualMixin {

    @Shadow(remap = false)
    private PoseStack poseStack;

    @Unique
    private float loconautics$lastYaw = 0.0f;

    @Unique
    private AbstractBogeyBlockEntity loconautics$blockEntity;

    /**
     * Captures the block entity reference once, right after the constructor has finished setting up
     * the base pose stack (translate to block + center + axis rotation + drop). At this point
     * {@code poseStack} is still at the bogey's centered pivot before being un-centered, but Create's
     * constructor already applied the final {@code translate(0, -1.5078125, 0)} by the time this
     * fires — that's fine, since rotating around Y at this point still pivots the whole assembly
     * about the bogey's vertical centerline (the un-centering only offset it vertically).
     */
    @Inject(method = "<init>", at = @At("RETURN"))
    private void loconautics$onConstructed(dev.engine_room.flywheel.api.visualization.VisualizationContext ctx,
                                           AbstractBogeyBlockEntity blockEntity, float partialTick, CallbackInfo ci) {
        this.loconautics$blockEntity = blockEntity;
        this.loconautics$lastYaw = BogeyYawVisual.getLocalYaw(blockEntity);
        if (this.loconautics$lastYaw != 0.0f) {
            this.poseStack.mulPose(Axis.YP.rotationDegrees(this.loconautics$lastYaw));
        }
    }

    /**
     * Every frame: if the target yaw changed since last frame, rotate the pose stack by the
     * difference so it tracks the new target without compounding stale rotations.
     */
    @Inject(method = "updateBogey", at = @At("HEAD"))
    private void loconautics$applyYaw(float partialTick, CallbackInfo ci) {
        if (this.loconautics$blockEntity == null) {
            return;
        }
        // Spin the wheels/side rods by the distance travelled this frame (Create's own animate() path).
        // This is the Flywheel-visual render path Sable actually uses for train bogeys, so the wheel
        // animation must be driven from here too — not only the vanilla BogeyBlockEntityRenderer path.
        BogeyWheelAnimator.frame(this.loconautics$blockEntity);

        float targetYaw = BogeyYawVisual.getLocalYaw(this.loconautics$blockEntity);
        float delta = targetYaw - this.loconautics$lastYaw;
        if (Math.abs(delta) < 1.0e-4f) {
            return;
        }
        this.poseStack.mulPose(Axis.YP.rotationDegrees(delta));
        this.loconautics$lastYaw = targetYaw;
    }
}