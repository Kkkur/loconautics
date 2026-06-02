/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.world.entity.Entity
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.mixin.entity.entity_rotations_and_riding;

import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.api.entity.EntitySubLevelUtil;
import dev.ryanhcode.sable.mixinhelpers.camera.camera_rotation.EntitySubLevelRotationHelper;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={EntityRenderer.class})
public class EntityRendererMixin {
    @Redirect(method={"renderNameTag"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;cameraOrientation()Lorg/joml/Quaternionf;"))
    private Quaternionf sable$renderNameTag(EntityRenderDispatcher instance, @Local(argsOnly=true) Entity entity) {
        if (!EntitySubLevelUtil.shouldKick(entity)) {
            return instance.cameraOrientation();
        }
        float pt = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        Quaterniond orientation = EntitySubLevelRotationHelper.getEntityOrientation(entity, x -> ((ClientSubLevel)x).renderPose(), pt, EntitySubLevelRotationHelper.Type.ENTITY);
        if (orientation == null) {
            return instance.cameraOrientation();
        }
        return new Quaternionf((Quaterniondc)orientation).conjugate().mul((Quaternionfc)instance.cameraOrientation());
    }
}
