/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  foundry.veil.api.client.render.VeilRenderBridge
 *  foundry.veil.api.client.render.rendertype.VeilRenderType$LayeredRenderType
 *  foundry.veil.api.client.render.rendertype.VeilRenderType$RenderTypeWrapper
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  net.caffeinemc.mods.sodium.client.SodiumClientMod
 *  net.caffeinemc.mods.sodium.client.gl.device.CommandList
 *  net.caffeinemc.mods.sodium.client.gl.device.RenderDevice
 *  net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer
 *  net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderMatrices
 *  net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager
 *  net.caffeinemc.mods.sodium.client.render.viewport.Viewport
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.PrioritizeChunkUpdates
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.client.renderer.chunk.RenderRegionCache
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.core.Position
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4f
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.sublevel_render.impl.sodium;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.ClientSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.mixinterface.plot.SubLevelContainerHolder;
import dev.ryanhcode.sable.mixinterface.sublevel_render.sodium.SodiumWorldRendererExtension;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.render.dispatcher.SodiumSubLevelRenderDispatcher;
import dev.ryanhcode.sable.sublevel.render.dispatcher.SubLevelRenderDispatcher;
import dev.ryanhcode.sable.sublevel.render.sodium.SodiumSubLevelRenderData;
import dev.ryanhcode.sable.sublevel.render.sodium.SubLevelRenderSectionManager;
import foundry.veil.api.client.render.VeilRenderBridge;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.caffeinemc.mods.sodium.client.SodiumClientMod;
import net.caffeinemc.mods.sodium.client.gl.device.CommandList;
import net.caffeinemc.mods.sodium.client.gl.device.RenderDevice;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import net.caffeinemc.mods.sodium.client.render.viewport.Viewport;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.PrioritizeChunkUpdates;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.Position;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={SodiumWorldRenderer.class}, remap=false)
public abstract class SodiumWorldRendererMixin
implements SodiumWorldRendererExtension {
    @Unique
    private final Object2ObjectMap<ClientSubLevel, RenderSectionManager> sable$subLevelSectionManagers = new Object2ObjectOpenHashMap();
    @Shadow
    private RenderSectionManager renderSectionManager;
    @Shadow
    private ClientLevel level;
    @Shadow
    @Final
    private Minecraft client;

    @Inject(method={"unloadLevel"}, at={@At(value="HEAD")})
    private void sable$onUnloadLevel(CallbackInfo ci) {
        for (RenderSectionManager manager : this.sable$subLevelSectionManagers.values()) {
            manager.destroy();
        }
        this.sable$subLevelSectionManagers.clear();
    }

    @Inject(method={"scheduleTerrainUpdate"}, at={@At(value="HEAD")})
    private void sable$onScheduleTerrainUpdate(CallbackInfo ci) {
        for (RenderSectionManager manager : this.sable$subLevelSectionManagers.values()) {
            manager.markGraphDirty();
        }
    }

    @Overwrite
    public int getVisibleChunkCount() {
        int sum = this.renderSectionManager.getVisibleChunkCount();
        for (RenderSectionManager manager : this.sable$subLevelSectionManagers.values()) {
            sum += manager.getVisibleChunkCount();
        }
        return sum;
    }

    @Inject(method={"isTerrainRenderComplete"}, at={@At(value="HEAD")}, cancellable=true)
    public void sable$isTerrainRenderComplete(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            for (RenderSectionManager sectionManager : this.sable$subLevelSectionManagers.values()) {
                if (sectionManager.getBuilder().isBuildQueueEmpty()) continue;
                cir.setReturnValue((Object)false);
                break;
            }
        }
    }

    @Inject(method={"setupTerrain"}, at={@At(value="INVOKE", target="Lnet/caffeinemc/mods/sodium/client/render/chunk/RenderSectionManager;markGraphDirty()V")})
    public void sable$markGraphDirty(Camera camera, Viewport viewport, boolean spectator, boolean updateChunksImmediately, CallbackInfo ci) {
        List<ClientSubLevel> sublevels = ((ClientSubLevelContainer)((SubLevelContainerHolder)this.level).sable$getPlotContainer()).getAllSubLevels();
        Vec3 cameraPosition = camera.getPosition();
        Minecraft minecraft = Minecraft.getInstance();
        Frustum frustum = minecraft.levelRenderer.cullingFrustum;
        SubLevelRenderDispatcher.get().updateCulling(sublevels, cameraPosition.x, cameraPosition.y, cameraPosition.z, VeilRenderBridge.create((Frustum)frustum), minecraft.player.isSpectator());
        this.sable$subLevelSectionManagers.values().forEach(RenderSectionManager::markGraphDirty);
    }

    @Inject(method={"setupTerrain"}, at={@At(value="TAIL")})
    public void sable$setupTerrain(Camera camera, Viewport viewport, boolean spectator, boolean updateChunksImmediately, CallbackInfo ci) {
        ProfilerFiller profiler = this.client.getProfiler();
        SubLevelRenderDispatcher dispatcher = SubLevelRenderDispatcher.get();
        if (!(dispatcher instanceof SodiumSubLevelRenderDispatcher)) {
            dispatcher.preRenderChunks(camera);
            List<ClientSubLevel> sublevels = SubLevelContainer.getContainer(this.level).getAllSubLevels();
            RenderRegionCache renderRegionCache = new RenderRegionCache();
            PrioritizeChunkUpdates chunkUpdates = SodiumClientMod.options().performance.alwaysDeferChunkUpdates ? PrioritizeChunkUpdates.NONE : PrioritizeChunkUpdates.NEARBY;
            for (ClientSubLevel sublevel : sublevels) {
                sublevel.getRenderData().compileSections(chunkUpdates, renderRegionCache, camera);
            }
            return;
        }
        for (ClientSubLevel clientSubLevel : SubLevelContainer.getContainer(this.level).getAllSubLevels()) {
            this.sable$getOrCreateSubLevelRenderSectionManager(clientSubLevel);
        }
        ObjectIterator iter = this.sable$subLevelSectionManagers.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            ClientSubLevel subLevel = (ClientSubLevel)entry.getKey();
            RenderSectionManager renderSectionManager = (RenderSectionManager)entry.getValue();
            if (subLevel.isRemoved()) {
                renderSectionManager.destroy();
                iter.remove();
                continue;
            }
            Vector3d cameraPos = JOMLConversion.toJOML((Position)camera.getPosition());
            subLevel.renderPose().transformPositionInverse(cameraPos);
            renderSectionManager.updateCameraState((Vector3dc)cameraPos, camera);
            ((SodiumSubLevelRenderData)subLevel.getRenderData()).updateChunks(updateChunksImmediately);
        }
        for (RenderSectionManager renderSectionManager : this.sable$subLevelSectionManagers.values()) {
            profiler.push("chunk_update");
            renderSectionManager.updateChunks(updateChunksImmediately);
            profiler.popPush("chunk_upload");
            renderSectionManager.uploadChunks();
            profiler.popPush("chunk_render_lists");
            renderSectionManager.update(camera, viewport, spectator);
            if (updateChunksImmediately) {
                profiler.popPush("chunk_upload_immediately");
                renderSectionManager.uploadChunks();
            }
            profiler.popPush("chunk_render_tick");
            renderSectionManager.tickVisibleRenders();
            profiler.pop();
        }
    }

    @Inject(method={"scheduleRebuildForChunk(IIIZ)V"}, at={@At(value="TAIL")})
    public void sable$scheduleRebuildForChunk(int x, int y, int z, boolean playerChanged, CallbackInfo ci) {
        ClientSubLevel subLevel;
        ClientSubLevelContainer container = SubLevelContainer.getContainer(this.level);
        if (container != null && container.inBounds(x, z) && (subLevel = (ClientSubLevel)Sable.HELPER.getContaining((Level)this.level, new ChunkPos(x, z))) != null) {
            subLevel.getRenderData().setDirty(x, y, z, playerChanged);
        }
        for (RenderSectionManager manager : this.sable$subLevelSectionManagers.values()) {
            manager.scheduleRebuild(x, y, z, playerChanged);
        }
    }

    @Inject(method={"drawChunkLayer"}, at={@At(value="TAIL")})
    public void sable$drawRenderSources(RenderType renderType, ChunkRenderMatrices matrices, double camX, double camY, double camZ, CallbackInfo ci) {
        SubLevelRenderDispatcher renderDispatcher = SubLevelRenderDispatcher.get();
        if (!(renderDispatcher instanceof SodiumSubLevelRenderDispatcher)) {
            Minecraft minecraft = Minecraft.getInstance();
            float partialTicks = minecraft.getTimer().getGameTimeDeltaPartialTick(false);
            List<ClientSubLevel> subLevels = SubLevelContainer.getContainer(this.level).getAllSubLevels();
            Matrix4f modelView = new Matrix4f(matrices.modelView());
            Matrix4f projection = new Matrix4f(matrices.projection());
            renderType.setupRenderState();
            ShaderInstance shader = Objects.requireNonNull(RenderSystem.getShader(), "shader");
            shader.setDefaultUniforms(VertexFormat.Mode.QUADS, modelView, projection, minecraft.getWindow());
            shader.apply();
            renderDispatcher.renderSectionLayer(subLevels, renderType, shader, camX, camY, camZ, modelView, projection, partialTicks);
            shader.clear();
            renderType.clearRenderState();
            RenderType unwrappedRenderType = renderType;
            while (unwrappedRenderType instanceof VeilRenderType.RenderTypeWrapper) {
                VeilRenderType.RenderTypeWrapper wrapper = (VeilRenderType.RenderTypeWrapper)unwrappedRenderType;
                unwrappedRenderType = wrapper.get();
            }
            if (unwrappedRenderType instanceof VeilRenderType.LayeredRenderType) {
                VeilRenderType.LayeredRenderType layered = (VeilRenderType.LayeredRenderType)unwrappedRenderType;
                for (RenderType layer : layered.getLayers()) {
                    layer.setupRenderState();
                    ShaderInstance shader2 = Objects.requireNonNull(RenderSystem.getShader(), "shader");
                    shader2.setDefaultUniforms(VertexFormat.Mode.QUADS, modelView, projection, minecraft.getWindow());
                    shader2.apply();
                    renderDispatcher.renderSectionLayer(subLevels, layer, shader2, camX, camY, camZ, modelView, projection, partialTicks);
                    shader2.clear();
                    layer.clearRenderState();
                }
            }
            return;
        }
        if (renderType == RenderType.solid() || renderType == RenderType.translucent()) {
            for (Map.Entry entry : this.sable$subLevelSectionManagers.entrySet()) {
                ClientSubLevel subLevel = (ClientSubLevel)entry.getKey();
                RenderSectionManager manager = (RenderSectionManager)entry.getValue();
                ((SodiumSubLevelRenderData)subLevel.getRenderData()).renderAdditional();
                SubLevelRenderSectionManager subLevelManager = (SubLevelRenderSectionManager)manager;
                subLevelManager.apply(matrices, camX, camY, camZ);
                subLevelManager.render(matrices, renderType, camX, camY, camZ);
            }
        }
    }

    @Override
    public SubLevelRenderSectionManager sable$getSubLevelRenderSectionManager(ClientSubLevel subLevel) {
        return (SubLevelRenderSectionManager)((Object)this.sable$subLevelSectionManagers.get((Object)subLevel));
    }

    @Override
    public void sable$freeRenderSectionManager(ClientSubLevel subLevel) {
        SubLevelRenderSectionManager manager = (SubLevelRenderSectionManager)((Object)this.sable$subLevelSectionManagers.remove((Object)subLevel));
        if (manager != null) {
            manager.destroy();
        }
    }

    @Unique
    private SubLevelRenderSectionManager sable$getOrCreateSubLevelRenderSectionManager(ClientSubLevel subLevel) {
        return (SubLevelRenderSectionManager)((Object)this.sable$subLevelSectionManagers.computeIfAbsent((Object)subLevel, s -> {
            try (CommandList commandList = RenderDevice.INSTANCE.createCommandList();){
                SubLevelRenderSectionManager subLevelRenderSectionManager = new SubLevelRenderSectionManager(subLevel, subLevel.getLevel(), this.client.options.getEffectiveRenderDistance(), commandList);
                return subLevelRenderSectionManager;
            }
        }));
    }
}
