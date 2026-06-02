/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.backend.engine.embed.EmbeddedEnvironment
 *  dev.engine_room.flywheel.backend.gl.shader.GlProgram
 *  dev.engine_room.flywheel.lib.util.ExtraMemoryOps
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.lwjgl.system.MemoryUtil
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.flywheel;

import dev.engine_room.flywheel.backend.engine.embed.EmbeddedEnvironment;
import dev.engine_room.flywheel.backend.gl.shader.GlProgram;
import dev.engine_room.flywheel.lib.util.ExtraMemoryOps;
import dev.ryanhcode.sable.neoforge.mixinterface.compatibility.flywheel.EmbeddedEnvironmentExtension;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EmbeddedEnvironment.class})
public class EmbeddedEnvironmentMixin
implements EmbeddedEnvironmentExtension {
    @Shadow
    @Final
    private Matrix4f poseComposed;
    @Unique
    private final Matrix4f sable$scene = new Matrix4f();
    @Unique
    private int sable$sceneId = 0;
    @Unique
    private float sable$skyLightScale = 1.0f;

    @Override
    public void sable$setLightingInfo(Matrix4fc sceneMatrix, int scene, float skyLightScale) {
        this.sable$scene.set(sceneMatrix);
        this.sable$sceneId = scene;
        this.sable$skyLightScale = skyLightScale;
    }

    @Inject(method={"setupDraw"}, at={@At(value="TAIL")})
    private void sable$setupDraw(GlProgram program, CallbackInfo ci) {
        program.setUInt("_flw_lightingSceneUniform", this.sable$sceneId);
        program.setFloat("_flw_lightingSkyLightScaleUniform", this.sable$skyLightScale);
        if (this.sable$sceneId == 0) {
            program.setMat4("_flw_lightingSceneMatrixUniform", (Matrix4fc)this.poseComposed);
        } else {
            program.setMat4("_flw_lightingSceneMatrixUniform", (Matrix4fc)this.sable$scene);
        }
    }

    @Inject(method={"flush"}, at={@At(value="TAIL")})
    public void sable$flush(long ptr, CallbackInfo ci) {
        MemoryUtil.memPutFloat((long)(ptr + 112L), (float)this.sable$skyLightScale);
        MemoryUtil.memPutInt((long)(ptr + 116L), (int)this.sable$sceneId);
        MemoryUtil.memPutFloat((long)(ptr + 120L), (float)0.0f);
        MemoryUtil.memPutFloat((long)(ptr + 124L), (float)0.0f);
        long sceneMatrixOffset = ptr + 128L;
        if (this.sable$sceneId == 0) {
            ExtraMemoryOps.putMatrix4f((long)sceneMatrixOffset, (Matrix4fc)this.poseComposed);
        } else {
            ExtraMemoryOps.putMatrix4f((long)sceneMatrixOffset, (Matrix4fc)this.sable$scene);
        }
    }
}
