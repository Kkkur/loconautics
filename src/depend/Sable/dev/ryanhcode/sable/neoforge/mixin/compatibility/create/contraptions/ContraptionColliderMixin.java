/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.llamalad7.mixinextras.sugar.Share
 *  com.llamalad7.mixinextras.sugar.ref.LocalRef
 *  com.simibubi.create.content.contraptions.AbstractContraptionEntity
 *  com.simibubi.create.content.contraptions.AbstractContraptionEntity$ContraptionRotationState
 *  com.simibubi.create.content.contraptions.ContraptionCollider
 *  com.simibubi.create.foundation.collision.Matrix3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix3d
 *  org.joml.Quaterniondc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.contraptions;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ContraptionCollider;
import com.simibubi.create.foundation.collision.Matrix3d;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.neoforge.mixin.compatibility.create.contraptions.Matrix3dAccessor;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniondc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={ContraptionCollider.class}, remap=false)
public class ContraptionColliderMixin {
    @Unique
    private static org.joml.Matrix3d sable$toJOML(Matrix3d createMatrix) {
        org.joml.Matrix3d jomlMatrix = new org.joml.Matrix3d();
        Matrix3dAccessor accessor = (Matrix3dAccessor)createMatrix;
        jomlMatrix.set(accessor.getM00(), accessor.getM01(), accessor.getM02(), accessor.getM10(), accessor.getM11(), accessor.getM12(), accessor.getM20(), accessor.getM21(), accessor.getM22());
        return jomlMatrix;
    }

    @Unique
    private static Matrix3d sable$toCreate(org.joml.Matrix3d jomlMatrix) {
        Matrix3d createMatrix = new Matrix3d();
        Matrix3dAccessor accessor = (Matrix3dAccessor)createMatrix;
        accessor.setM00(jomlMatrix.m00);
        accessor.setM01(jomlMatrix.m01);
        accessor.setM02(jomlMatrix.m02);
        accessor.setM10(jomlMatrix.m10);
        accessor.setM11(jomlMatrix.m11);
        accessor.setM12(jomlMatrix.m12);
        accessor.setM20(jomlMatrix.m20);
        accessor.setM21(jomlMatrix.m21);
        accessor.setM22(jomlMatrix.m22);
        return createMatrix;
    }

    @Redirect(method={"collideEntities"}, at=@At(value="INVOKE", target="Lcom/simibubi/create/content/contraptions/AbstractContraptionEntity;getBoundingBox()Lnet/minecraft/world/phys/AABB;"))
    private static AABB sable$contraptionBounds(AbstractContraptionEntity instance, @Share(value="subLevel") LocalRef<SubLevel> contraptionSubLevel) {
        SubLevel subLevel = Sable.HELPER.getContaining((Entity)instance);
        contraptionSubLevel.set((Object)subLevel);
        if (subLevel != null) {
            BoundingBox3d globalBB = new BoundingBox3d(instance.getBoundingBox());
            globalBB.transform((Pose3dc)subLevel.logicalPose(), globalBB);
            return globalBB.toMojang();
        }
        return instance.getBoundingBox();
    }

    @Redirect(method={"collideEntities"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/phys/AABB;expandTowards(DDD)Lnet/minecraft/world/phys/AABB;"))
    private static AABB sable$entityQueryBounds(AABB instance, double d, double e, double f, @Local(argsOnly=true) AbstractContraptionEntity contraption, @Share(value="subLevel") LocalRef<SubLevel> contraptionSubLevel) {
        SubLevel subLevel = (SubLevel)contraptionSubLevel.get();
        if (subLevel != null) {
            BoundingBox3d globalBB = new BoundingBox3d(contraption.getBoundingBox().inflate(2.0).expandTowards(d, e, f));
            globalBB.transform((Pose3dc)subLevel.logicalPose(), globalBB);
            return globalBB.toMojang();
        }
        return instance.expandTowards(d, e, f);
    }

    @Redirect(method={"collideEntities"}, at=@At(value="INVOKE", target="Lcom/simibubi/create/content/contraptions/AbstractContraptionEntity;position()Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 sable$contraptionPosition(AbstractContraptionEntity instance, @Share(value="subLevel") LocalRef<SubLevel> contraptionSubLevel) {
        SubLevel subLevel = (SubLevel)contraptionSubLevel.get();
        if (subLevel != null) {
            return subLevel.logicalPose().transformPosition(instance.position());
        }
        return instance.position();
    }

    @Redirect(method={"collideEntities"}, at=@At(value="INVOKE", target="Lcom/simibubi/create/content/contraptions/AbstractContraptionEntity;getPrevPositionVec()Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 sable$getPrevPositionVec(AbstractContraptionEntity instance, @Share(value="subLevel") LocalRef<SubLevel> contraptionSubLevel) {
        SubLevel subLevel = (SubLevel)contraptionSubLevel.get();
        if (subLevel != null) {
            return subLevel.logicalPose().transformPosition(instance.getPrevPositionVec());
        }
        return instance.getPrevPositionVec();
    }

    @Redirect(method={"collideEntities"}, at=@At(value="INVOKE", target="Lcom/simibubi/create/content/contraptions/AbstractContraptionEntity;getAnchorVec()Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 sable$getAnchorVec(AbstractContraptionEntity instance, @Share(value="subLevel") LocalRef<SubLevel> contraptionSubLevel) {
        SubLevel subLevel = (SubLevel)contraptionSubLevel.get();
        if (subLevel != null) {
            return subLevel.logicalPose().transformPosition(instance.getAnchorVec().add(0.5, 0.5, 0.5)).subtract(0.5, 0.5, 0.5);
        }
        return instance.getAnchorVec();
    }

    @Redirect(method={"collideEntities"}, at=@At(value="INVOKE", target="Lcom/simibubi/create/content/contraptions/AbstractContraptionEntity$ContraptionRotationState;asMatrix()Lcom/simibubi/create/foundation/collision/Matrix3d;"))
    private static Matrix3d sable$rotationMatrix(AbstractContraptionEntity.ContraptionRotationState rotationState, @Local(argsOnly=true) AbstractContraptionEntity contraption, @Share(value="subLevel") LocalRef<SubLevel> contraptionSubLevel) {
        SubLevel subLevel = (SubLevel)contraptionSubLevel.get();
        if (subLevel != null) {
            Pose3d pose = subLevel.logicalPose();
            org.joml.Matrix3d jomlMatrix = ContraptionColliderMixin.sable$toJOML(rotationState.asMatrix());
            jomlMatrix.rotateLocal((Quaterniondc)pose.orientation());
            return ContraptionColliderMixin.sable$toCreate(jomlMatrix);
        }
        return rotationState.asMatrix();
    }

    @Redirect(method={"collideEntities"}, at=@At(value="INVOKE", target="Lcom/simibubi/create/content/contraptions/AbstractContraptionEntity;toLocalVector(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 sable$toLocalVector(AbstractContraptionEntity instance, Vec3 localVec, float partialTicks, @Share(value="subLevel") LocalRef<SubLevel> contraptionSubLevel) {
        SubLevel subLevel = (SubLevel)contraptionSubLevel.get();
        if (subLevel != null) {
            Pose3d pose = subLevel.logicalPose();
            return instance.toLocalVector(pose.transformPositionInverse(localVec), partialTicks);
        }
        return instance.toLocalVector(localVec, partialTicks);
    }

    @Redirect(method={"collideEntities"}, at=@At(value="INVOKE", target="Lcom/simibubi/create/content/contraptions/AbstractContraptionEntity;getContactPointMotion(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 sable$getContactPointMotion(AbstractContraptionEntity instance, Vec3 globalContactPoint, @Share(value="subLevel") LocalRef<SubLevel> contraptionSubLevel) {
        SubLevel subLevel = (SubLevel)contraptionSubLevel.get();
        if (subLevel != null) {
            Pose3d pose = subLevel.logicalPose();
            Vec3 localContactPoint = pose.transformPositionInverse(globalContactPoint);
            Vec3 motion = pose.transformNormal(instance.getContactPointMotion(localContactPoint)).add(globalContactPoint.subtract(subLevel.lastPose().transformPosition(localContactPoint)));
            return motion;
        }
        return instance.getContactPointMotion(globalContactPoint);
    }
}
