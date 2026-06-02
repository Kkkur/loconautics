/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.joml.Quaterniondc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.frogports;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={SmartBlockEntityRenderer.class})
public class SmartBlockEntityRendererMixin<T extends SmartBlockEntity> {
    @Redirect(method={"renderNameplateOnHover"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;cameraOrientation()Lorg/joml/Quaternionf;"))
    private Quaternionf sable$renderNameTag(EntityRenderDispatcher instance, @Local(argsOnly=true) T be) {
        SubLevel subLevel = Sable.HELPER.getContaining((BlockEntity)be);
        if (subLevel == null) {
            return instance.cameraOrientation();
        }
        Quaterniondc subLevelOrientation = ((ClientSubLevel)subLevel).renderPose().orientation();
        return instance.cameraOrientation().premul((Quaternionfc)new Quaternionf(subLevelOrientation).conjugate());
    }
}
