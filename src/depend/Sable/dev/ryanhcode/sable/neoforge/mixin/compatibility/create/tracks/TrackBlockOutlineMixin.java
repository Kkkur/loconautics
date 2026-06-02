/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.llamalad7.mixinextras.sugar.Share
 *  com.llamalad7.mixinextras.sugar.ref.LocalRef
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.content.trains.track.BezierConnection
 *  com.simibubi.create.content.trains.track.TrackBlockEntity
 *  com.simibubi.create.content.trains.track.TrackBlockOutline
 *  com.simibubi.create.content.trains.track.TrackBlockOutline$BezierPointSelection
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.Translate
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.tracks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.simibubi.create.content.trains.track.TrackBlockOutline;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.Translate;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={TrackBlockOutline.class})
public class TrackBlockOutlineMixin {
    @WrapOperation(method={"drawCurveSelection"}, at={@At(value="INVOKE", target="Ldev/engine_room/flywheel/lib/transform/PoseTransformStack;translate(DDD)Ldev/engine_room/flywheel/lib/transform/Translate;", ordinal=0)})
    private static Translate<?> sable$translateCurveFactoringSubLevels(PoseTransformStack ms, double x, double y, double z, Operation<Translate<?>> original, @Local(name={"result"}) TrackBlockOutline.BezierPointSelection result, @Local(name={"camera"}) Vec3 camera) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return (Translate)original.call(new Object[]{ms, x, y, z});
        }
        Vec3 bezierPos = result.vec();
        ClientSubLevel subLevel = (ClientSubLevel)Sable.HELPER.getContaining((Level)level, (Position)bezierPos);
        if (subLevel == null) {
            return (Translate)original.call(new Object[]{ms, x, y, z});
        }
        Vec3 worldPos = subLevel.renderPose().transformPosition(bezierPos);
        worldPos = worldPos.subtract(camera);
        return ((PoseTransformStack)ms.translate(worldPos.x, worldPos.y, worldPos.z)).rotate((Quaternionfc)new Quaternionf(subLevel.renderPose().orientation()));
    }

    @Redirect(method={"drawCustomBlockSelection"}, at=@At(value="INVOKE", target="Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V"))
    private static void sable$translateBlockFactoringSubLevels(PoseStack instance, double x, double y, double z, @Local(name={"camPos"}) Vec3 camPos, @Local(name={"pos"}) BlockPos pos) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            instance.translate(x, y, z);
            return;
        }
        ClientSubLevel subLevel = (ClientSubLevel)Sable.HELPER.getContaining((Level)level, (Vec3i)pos);
        if (subLevel == null) {
            instance.translate(x, y, z);
            return;
        }
        Vec3 localPos = subLevel.renderPose().transformPosition(Vec3.atLowerCornerOf((Vec3i)pos));
        instance.translate(localPos.x - camPos.x, localPos.y - camPos.y, localPos.z - camPos.z);
        instance.mulPose(new Quaternionf(subLevel.renderPose().orientation()));
    }

    @Inject(method={"pickCurves"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/content/trains/track/BezierConnection;isPrimary()Z")})
    private static void sable$findBlockEntitySubLevel(CallbackInfo ci, @Share(value="currentBlockEntitySubLevel") LocalRef<ClientSubLevel> subLevel, @Local(name={"be"}) TrackBlockEntity be) {
        subLevel.set((Object)((ClientSubLevel)Sable.HELPER.getContaining((BlockEntity)be)));
    }

    @Redirect(method={"pickCurves"}, at=@At(value="INVOKE", target="Lcom/simibubi/create/content/trains/track/BezierConnection;getBounds()Lnet/minecraft/world/phys/AABB;"))
    private static AABB sable$getWorldSpaceBounds(BezierConnection instance, @Share(value="currentBlockEntitySubLevel") LocalRef<ClientSubLevel> subLevel, @Local(name={"bc"}) BezierConnection bc) {
        if (subLevel.get() == null) {
            return instance.getBounds();
        }
        float partialTicks = AnimationTickHolder.getPartialTicks((LevelAccessor)Minecraft.getInstance().level);
        BoundingBox3d localBounds = new BoundingBox3d(instance.getBounds()).transform(((ClientSubLevel)subLevel.get()).renderPose(partialTicks));
        return localBounds.toMojang();
    }

    @Redirect(method={"pickCurves"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/player/LocalPlayer;getEyePosition(F)Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 sable$getEyePosition(LocalPlayer entity, float partialTicks) {
        return Sable.HELPER.getEyePositionInterpolated((Entity)entity, partialTicks);
    }

    @Redirect(method={"pickCurves"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/phys/Vec3;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D"))
    private static double sable$distanceToHitSquared(Vec3 vecA, Vec3 vecB) {
        return Sable.HELPER.distanceSquaredWithSubLevels((Level)Minecraft.getInstance().level, (Position)vecA, (Position)vecB);
    }

    @Redirect(method={"pickCurves"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/phys/Vec3;subtract(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;", ordinal=1))
    private static Vec3 sable$getLocalOrigin(Vec3 origin, Vec3 anchor, @Share(value="currentBlockEntitySubLevel") LocalRef<ClientSubLevel> subLevel, @Local(name={"bc"}) BezierConnection bc) {
        if (subLevel.get() == null) {
            return origin.subtract(anchor);
        }
        float partialTicks = AnimationTickHolder.getPartialTicks((LevelAccessor)Minecraft.getInstance().level);
        Vec3 localOrigin = ((ClientSubLevel)subLevel.get()).renderPose(partialTicks).transformPositionInverse(origin);
        return localOrigin.subtract(anchor);
    }

    @Redirect(method={"pickCurves"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/phys/Vec3;subtract(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;", ordinal=2))
    private static Vec3 sable$getLocalTarget(Vec3 target, Vec3 origin, @Share(value="currentBlockEntitySubLevel") LocalRef<ClientSubLevel> subLevel, @Local(name={"bc"}) BezierConnection bc) {
        if (subLevel.get() == null) {
            return target.subtract(origin);
        }
        float partialTicks = AnimationTickHolder.getPartialTicks((LevelAccessor)Minecraft.getInstance().level);
        Vec3 localTarget = ((ClientSubLevel)subLevel.get()).renderPose(partialTicks).transformPositionInverse(target);
        Vec3 localOrigin = ((ClientSubLevel)subLevel.get()).renderPose(partialTicks).transformPositionInverse(origin);
        return localTarget.subtract(localOrigin);
    }
}
