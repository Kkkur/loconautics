/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  foundry.veil.api.client.render.VeilRenderBridge
 *  foundry.veil.api.client.render.rendertype.VeilRenderType$LayeredRenderType
 *  foundry.veil.api.client.render.rendertype.VeilRenderType$RenderTypeWrapper
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.PrioritizeChunkUpdates
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.client.renderer.chunk.RenderRegionCache
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.sublevel_render.impl.vanilla;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.ClientSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.mixinterface.plot.SubLevelContainerHolder;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.render.SubLevelRenderData;
import dev.ryanhcode.sable.sublevel.render.dispatcher.SubLevelRenderDispatcher;
import foundry.veil.api.client.render.VeilRenderBridge;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.PrioritizeChunkUpdates;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={LevelRenderer.class}, priority=1002)
public abstract class LevelRendererMixin {
    @Shadow
    @Nullable
    private ClientLevel level;
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method={"compileSections"}, at={@At(value="TAIL")})
    private void sable$compileSections(Camera camera, CallbackInfo ci) {
        List<ClientSubLevel> sublevels = ((ClientSubLevelContainer)((SubLevelContainerHolder)this.level).sable$getPlotContainer()).getAllSubLevels();
        RenderRegionCache renderRegionCache = new RenderRegionCache();
        PrioritizeChunkUpdates chunkUpdates = (PrioritizeChunkUpdates)Minecraft.getInstance().options.prioritizeChunkUpdates().get();
        for (ClientSubLevel sublevel : sublevels) {
            sublevel.getRenderData().compileSections(chunkUpdates, renderRegionCache, camera);
        }
    }

    @Inject(method={"setupRender"}, at={@At(value="INVOKE_STRING", target="Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", args={"ldc=update"})})
    public void sable$cull(Camera camera, Frustum frustum, boolean hasCapturedFrustum, boolean isSpectator, CallbackInfo ci) {
        if (hasCapturedFrustum) {
            return;
        }
        SubLevelRenderDispatcher dispatcher = SubLevelRenderDispatcher.get();
        dispatcher.preRenderChunks(camera);
        ProfilerFiller profiler = this.minecraft.getProfiler();
        profiler.push("sub_level_section_occlusion_graph");
        List<ClientSubLevel> sublevels = ((ClientSubLevelContainer)((SubLevelContainerHolder)this.level).sable$getPlotContainer()).getAllSubLevels();
        Vec3 cameraPosition = camera.getPosition();
        dispatcher.updateCulling(sublevels, cameraPosition.x, cameraPosition.y, cameraPosition.z, VeilRenderBridge.create((Frustum)frustum), isSpectator);
        profiler.pop();
    }

    @Inject(method={"isSectionCompiled"}, at={@At(value="HEAD")}, cancellable=true)
    private void sable$isSectionCompiled(BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        ClientSubLevelContainer container = SubLevelContainer.getContainer(this.level);
        if (container == null) {
            return;
        }
        if (container.inBounds(blockPos)) {
            ClientSubLevel subLevel = (ClientSubLevel)Sable.HELPER.getContaining((Level)this.level, (Vec3i)blockPos);
            if (subLevel == null) {
                cir.setReturnValue((Object)false);
            } else {
                SubLevelRenderData renderData = subLevel.getRenderData();
                SectionPos sectionPos = SectionPos.of((BlockPos)blockPos);
                cir.setReturnValue((Object)renderData.isSectionCompiled(sectionPos.x(), sectionPos.y(), sectionPos.z()));
            }
        }
    }

    @Inject(method={"renderSectionLayer"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/ShaderInstance;clear()V")})
    public void sable$renderSubLevels(RenderType renderType, double x, double y, double z, Matrix4f modelView, Matrix4f projection, CallbackInfo ci, @Local ShaderInstance shader) {
        List<ClientSubLevel> sublevels = ((ClientSubLevelContainer)((SubLevelContainerHolder)this.level).sable$getPlotContainer()).getAllSubLevels();
        SubLevelRenderDispatcher.get().renderSectionLayer(sublevels, renderType, shader, x, y, z, modelView, projection, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
    }

    @Inject(method={"renderSectionLayer"}, at={@At(value="TAIL")})
    public void sable$renderSubLevelLayers(RenderType renderType, double x, double y, double z, Matrix4f modelView, Matrix4f projection, CallbackInfo ci) {
        RenderType unwrappedRenderType = renderType;
        while (unwrappedRenderType instanceof VeilRenderType.RenderTypeWrapper) {
            VeilRenderType.RenderTypeWrapper wrapper = (VeilRenderType.RenderTypeWrapper)unwrappedRenderType;
            unwrappedRenderType = wrapper.get();
        }
        if (!(unwrappedRenderType instanceof VeilRenderType.LayeredRenderType)) {
            return;
        }
        VeilRenderType.LayeredRenderType layered = (VeilRenderType.LayeredRenderType)unwrappedRenderType;
        List<ClientSubLevel> sublevels = ((ClientSubLevelContainer)((SubLevelContainerHolder)this.level).sable$getPlotContainer()).getAllSubLevels();
        SubLevelRenderDispatcher renderDispatcher = SubLevelRenderDispatcher.get();
        for (RenderType layer : layered.getLayers()) {
            layer.setupRenderState();
            ShaderInstance shader = Objects.requireNonNull(RenderSystem.getShader(), "shader");
            shader.setDefaultUniforms(VertexFormat.Mode.QUADS, modelView, projection, this.minecraft.getWindow());
            shader.apply();
            renderDispatcher.renderSectionLayer(sublevels, renderType, shader, x, y, z, modelView, projection, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
            shader.clear();
            layer.clearRenderState();
        }
    }
}
