/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3ic
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  it.unimi.dsi.fastutil.Function
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.Position
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.camera.camera_zoom;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.SubLevelHelper;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.mixinhelpers.camera.new_camera_types.SableCameraTypes;
import dev.ryanhcode.sable.mixinterface.camera.camera_zoom.CameraZoomExtension;
import dev.ryanhcode.sable.mixinterface.clip_overwrite.ClipContextExtension;
import dev.ryanhcode.sable.mixinterface.clip_overwrite.LevelPoseProviderExtension;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import it.unimi.dsi.fastutil.Function;
import java.util.Collection;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Position;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Camera.class})
public abstract class CameraMixin
implements CameraZoomExtension {
    @Shadow
    private BlockGetter level;
    @Shadow
    private Vec3 position;
    @Shadow
    @Final
    private Vector3f forwards;
    @Shadow
    private Entity entity;
    @Unique
    private boolean sable$pushed = false;
    @Unique
    private float sable$zoomAmount;
    @Unique
    private float sable$interpolatedZoom;
    @Unique
    private float sable$lastInterpolatedZoom;

    @Shadow
    protected abstract void setPosition(double var1, double var3, double var5);

    @Inject(method={"tick"}, at={@At(value="HEAD")})
    private void sable$preTick(CallbackInfo ci) {
        this.sable$lastInterpolatedZoom = this.sable$interpolatedZoom;
        this.sable$interpolatedZoom = Mth.lerp((float)0.725f, (float)this.sable$interpolatedZoom, (float)this.sable$zoomAmount);
    }

    @Inject(method={"setup"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/Camera;setPosition(DDD)V", shift=At.Shift.AFTER)})
    private void sable$setup(BlockGetter blockGetter, Entity entity, boolean bl, boolean bl2, float f, CallbackInfo ci) {
        SubLevel subLevel;
        Entity cameraEntity;
        Entity vehicle;
        Minecraft minecraft = Minecraft.getInstance();
        if ((minecraft.options.getCameraType() == SableCameraTypes.SUB_LEVEL_VIEW || minecraft.options.getCameraType() == SableCameraTypes.SUB_LEVEL_VIEW_UNLOCKED) && (vehicle = (cameraEntity = minecraft.cameraEntity).getVehicle()) != null && (subLevel = Sable.HELPER.getContaining((Level)minecraft.level, (Position)vehicle.position())) instanceof ClientSubLevel) {
            ClientSubLevel clientSubLevel = (ClientSubLevel)subLevel;
            Vector3dc pos = clientSubLevel.renderPose().position();
            this.setPosition(pos.x(), pos.y(), pos.z());
        }
    }

    @Unique
    private float sable$clampZoom(float maxZoom, SubLevel ignoredSubLevel) {
        float zoom = maxZoom;
        float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
        Level level = this.entity.level();
        LevelPoseProviderExtension extension = (LevelPoseProviderExtension)this.level;
        assert (extension != null);
        Collection<SubLevel> ignoredChain = SubLevelHelper.getConnectedChain(ignoredSubLevel);
        extension.sable$pushPoseSupplier((Function<SubLevel, Pose3dc>)((Function)subLevel -> ((ClientSubLevel)subLevel).renderPose(partialTick)));
        for (int i = 0; i < 8; ++i) {
            float l;
            float offsetX = (i & 1) * 2 - 1;
            float offsetY = (i >> 1 & 1) * 2 - 1;
            float offsetZ = (i >> 2 & 1) * 2 - 1;
            Vec3 vec3 = this.position.add((double)(offsetX * 0.1f), (double)(offsetY * 0.1f), (double)(offsetZ * 0.1f));
            Vec3 vec32 = vec3.add(new Vec3(this.forwards).scale((double)(-zoom)));
            ClipContext clipContext = new ClipContext(vec3, vec32, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, this.entity);
            ((ClipContextExtension)clipContext).sable$setSubLevelIgnoring(ignoredChain::contains);
            BlockHitResult hitResult = this.level.clip(clipContext);
            if (hitResult.getType() == HitResult.Type.MISS || !((l = (float)Sable.HELPER.distanceSquaredWithSubLevels(level, (Position)hitResult.getLocation(), (Position)this.position)) < Mth.square((float)zoom))) continue;
            zoom = Mth.sqrt((float)l);
        }
        extension.sable$popPoseSupplier();
        return zoom;
    }

    @Inject(method={"getMaxZoom"}, at={@At(value="HEAD")}, cancellable=true)
    private void sable$getMaxZoomHead(float f, CallbackInfoReturnable<Float> cir) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.getCameraType() == SableCameraTypes.SUB_LEVEL_VIEW || minecraft.options.getCameraType() == SableCameraTypes.SUB_LEVEL_VIEW_UNLOCKED) {
            SubLevel subLevel2;
            boolean isTypeValid;
            Entity cameraEntity = minecraft.cameraEntity;
            Entity vehicle = cameraEntity.getVehicle();
            boolean bl = isTypeValid = vehicle != null;
            if (isTypeValid && (subLevel2 = Sable.HELPER.getContaining((Level)minecraft.level, (Position)vehicle.position())) != null) {
                float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
                float zoomAmount = Mth.lerp((float)partialTick, (float)this.sable$lastInterpolatedZoom, (float)this.sable$interpolatedZoom);
                BoundingBox3ic boundingBox = subLevel2.getPlot().getBoundingBox();
                Vec3 extents = new Vec3((double)(boundingBox.maxX() - boundingBox.minX()), (double)(boundingBox.maxY() - boundingBox.minY()), (double)(boundingBox.maxZ() - boundingBox.minZ()));
                double maxDist = extents.scale(0.5).length();
                float desiredDistance = (float)Math.max((double)f, maxDist) * (1.75f + zoomAmount);
                cir.setReturnValue((Object)Float.valueOf(this.sable$clampZoom(desiredDistance, subLevel2)));
                this.sable$pushed = false;
                return;
            }
        }
        LevelPoseProviderExtension extension = (LevelPoseProviderExtension)minecraft.level;
        assert (extension != null);
        extension.sable$pushPoseSupplier((Function<SubLevel, Pose3dc>)((Function)subLevel -> ((ClientSubLevel)subLevel).renderPose(minecraft.getTimer().getGameTimeDeltaPartialTick(false))));
        this.sable$pushed = true;
    }

    @Redirect(method={"getMaxZoom"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/phys/Vec3;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D"))
    private double sable$getMaxZoom(Vec3 instance, Vec3 vec3) {
        return Sable.HELPER.distanceSquaredWithSubLevels((Level)this.level, (Position)instance, (Position)vec3);
    }

    @Inject(method={"getMaxZoom"}, at={@At(value="RETURN")})
    private void sable$getMaxZoomTail(float f, CallbackInfoReturnable<Float> cir) {
        if (this.sable$pushed) {
            LevelPoseProviderExtension extension = (LevelPoseProviderExtension)Minecraft.getInstance().level;
            assert (extension != null);
            extension.sable$popPoseSupplier();
            this.sable$pushed = false;
        }
    }

    @Override
    public float sable$getZoomAmount() {
        return this.sable$zoomAmount;
    }

    @Override
    public void sable$setZoomAmount(float sable$zoomAmount) {
        this.sable$zoomAmount = Mth.clamp((float)sable$zoomAmount, (float)0.0f, (float)4.0f);
    }
}
