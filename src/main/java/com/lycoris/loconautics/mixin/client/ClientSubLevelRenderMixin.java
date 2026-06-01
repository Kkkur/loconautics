package com.lycoris.loconautics.mixin.client;

import com.lycoris.loconautics.client.ClientPhysicsTrainRegistry;
import com.lycoris.loconautics.core.PhysicsTrainPose;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Couples a physics train's sub-level render pose DIRECTLY to where Create draws the carriage, every
 * frame. This is the real fix for the "body drifts behind the bogeys depending on direction": the
 * bogeys are drawn by Create at the carriage's interpolated render position, while the sub-level was
 * drawn by Sable from a server-synced pose interpolated independently — two systems out of phase.
 *
 * <p>By overriding {@link ClientSubLevel}'s {@code renderPose(float)} to return a pose built from the
 * carriage's own interpolated position and rotation, the body is drawn at EXACTLY the carriage's
 * render transform — locked to the bogeys, no lag, correct on curves (the client computes a real
 * {@code ContraptionRotationState}, unlike the server). The physics/collision body still uses the
 * server pose; only the visual is coupled here. Mirrors Create-Interactive coupling the carriage and
 * its ship transform.
 */
@Mixin(ClientSubLevel.class)
public abstract class ClientSubLevelRenderMixin {

    @Shadow @Final private Pose3d renderPose;

    @Inject(
            method = "renderPose(F)Ldev/ryanhcode/sable/companion/math/Pose3dc;",
            at = @At("HEAD"),
            cancellable = true,
            remap = false)
    private void loconautics$coupleToCarriage(float partialTick, CallbackInfoReturnable<Pose3dc> cir) {
        ClientSubLevel self = (ClientSubLevel) (Object) this;
        CarriageContraptionEntity carriage = ClientPhysicsTrainRegistry.findCarriage(self.getUniqueId());
        if (carriage == null) {
            return; // not a physics-train sub-level — let Sable's own interpolation run
        }

        // Create draws the contraption (and its bogeys) from the entity's interpolated render position
        // (ContraptionMatrices.translateToEntity = lerp(xOld, x) = getPosition(partialTick)). Match it.
        Vec3 carriagePos = carriage.getPosition(partialTick);
        Quaterniond q = PhysicsTrainPose.orientationOf(carriage); // client-side, correct on curves

        // a block at plot pos (plotAnchor+L) must render where Create draws contraption-local L. The
        // carriage entity sits at the anchor's horizontal centre (x/z + 0.5), so re-centre on x/z only.
        Vector3dc rotationPoint = self.logicalPose().rotationPoint();
        BlockPos plotAnchor = self.getPlot().getCenterBlock();
        Vector3d offset = new Vector3d(
                rotationPoint.x() - (plotAnchor.getX() + 0.5),
                rotationPoint.y() - plotAnchor.getY(),
                rotationPoint.z() - (plotAnchor.getZ() + 0.5));
        q.transform(offset);

        renderPose.position().set(carriagePos.x + offset.x, carriagePos.y + offset.y, carriagePos.z + offset.z);
        renderPose.orientation().set(q);
        renderPose.rotationPoint().set(rotationPoint);
        cir.setReturnValue(renderPose);
    }
}
