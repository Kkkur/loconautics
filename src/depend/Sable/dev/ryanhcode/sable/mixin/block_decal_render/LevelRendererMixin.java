/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.client.Camera
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Constant
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyConstant
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.block_decal_render;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LevelRenderer.class})
public abstract class LevelRendererMixin {
    @Unique
    private final Quaternionf sable$orientationStorage = new Quaternionf();
    @Shadow
    @Nullable
    private ClientLevel level;

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lcom/mojang/blaze3d/vertex/PoseStack;last()Lcom/mojang/blaze3d/vertex/PoseStack$Pose;", shift=At.Shift.BEFORE)})
    private void sable$preRenderBlockDamage(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci, @Local(ordinal=0) PoseStack ps, @Local(ordinal=0) BlockPos pos) {
        Vec3 plotPos = new Vec3((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
        ClientSubLevel subLevel = (ClientSubLevel)Sable.HELPER.getContaining((Level)this.level, (Position)plotPos);
        if (subLevel == null) {
            return;
        }
        Pose3dc renderPose = subLevel.renderPose();
        Vec3 cameraPos = camera.getPosition();
        Vec3 projectedPos = renderPose.transformPosition(plotPos);
        ps.popPose();
        ps.pushPose();
        ps.translate(projectedPos.x - cameraPos.x, projectedPos.y - cameraPos.y, projectedPos.z - cameraPos.z);
        ps.mulPose(this.sable$orientationStorage.set(renderPose.orientation()));
    }

    @ModifyConstant(method={"renderLevel"}, constant={@Constant(doubleValue=1024.0, ordinal=0)})
    private double sable$blockDamageDistance(double originalBlockDamageDistanceConstant) {
        return Double.MAX_VALUE;
    }
}
