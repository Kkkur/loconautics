/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 */
package dev.ryanhcode.sable.mixinhelpers.camera.camera_rotation;

import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.entity.EntitySubLevelUtil;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.mixinhelpers.camera.new_camera_types.SableCameraTypes;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;

public class EntitySubLevelRotationHelper {
    public static boolean shouldCameraRotate() {
        return Minecraft.getInstance().options.getCameraType() != SableCameraTypes.SUB_LEVEL_VIEW_UNLOCKED;
    }

    @Nullable
    public static Quaterniond getEntityOrientation(Entity cameraEntity, Function<SubLevel, Pose3dc> poseProvider, float partialTicks, Type type) {
        Quaterniond ridingOrientation = EntitySubLevelRotationHelper.getSubLevelInheritedOrientation(cameraEntity, poseProvider, type);
        if (ridingOrientation != null) {
            return ridingOrientation;
        }
        Quaterniondc entityOrientation = EntitySubLevelUtil.getCustomEntityOrientation(cameraEntity, partialTicks);
        if (entityOrientation != null) {
            return new Quaterniond(entityOrientation);
        }
        return null;
    }

    public static Quaterniond getSubLevelInheritedOrientation(Entity cameraEntity, Function<SubLevel, Pose3dc> poseProvider, Type type) {
        SubLevel subLevel;
        Optional sleepingPos;
        LivingEntity livingEntity;
        Player player;
        if (type == Type.CAMERA && cameraEntity instanceof Player && (player = (Player)cameraEntity).isLocalPlayer() && !EntitySubLevelRotationHelper.shouldCameraRotate()) {
            return null;
        }
        ActiveSableCompanion helper = Sable.HELPER;
        if (cameraEntity instanceof LivingEntity && (livingEntity = (LivingEntity)cameraEntity).isSleeping() && (sleepingPos = livingEntity.getSleepingPos()).isPresent()) {
            BlockPos pos = (BlockPos)sleepingPos.get();
            SubLevel subLevel2 = helper.getContaining(livingEntity.level(), (Vec3i)pos);
            if (subLevel2 instanceof ClientSubLevel) {
                ClientSubLevel clientSubLevel = (ClientSubLevel)subLevel2;
                return new Quaterniond(clientSubLevel.renderPose().orientation());
            }
        }
        if (cameraEntity == null) {
            return null;
        }
        Entity entity = cameraEntity.getVehicle();
        if (entity == null) {
            if (cameraEntity instanceof Player) {
                return null;
            }
            if (helper.getContaining(cameraEntity) != null) {
                entity = cameraEntity;
            } else {
                return null;
            }
        }
        if ((subLevel = helper.getContaining(entity)) == null) {
            return null;
        }
        return new Quaterniond(poseProvider.apply(subLevel).orientation());
    }

    public static enum Type {
        CAMERA,
        ENTITY;

    }
}
