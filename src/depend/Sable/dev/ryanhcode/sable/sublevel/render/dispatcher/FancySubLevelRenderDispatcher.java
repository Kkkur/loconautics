/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  foundry.veil.api.client.render.CullFrustum
 *  foundry.veil.api.client.render.VeilRenderSystem
 *  foundry.veil.api.client.render.shader.program.ShaderProgram
 *  foundry.veil.api.client.render.shader.uniform.ShaderUniform
 *  foundry.veil.api.client.render.vertex.VertexArray
 *  foundry.veil.api.client.render.vertex.VertexArray$DrawUsage
 *  foundry.veil.api.client.render.vertex.VertexArray$IndexType
 *  foundry.veil.api.client.render.vertex.VertexArrayBuilder$DataType
 *  foundry.veil.impl.client.render.dynamicbuffer.VanillaShaderCompiler
 *  it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.BlockGetter
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaterniondc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3ic
 *  org.lwjgl.opengl.GL20C
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.NativeResource
 */
package dev.ryanhcode.sable.sublevel.render.dispatcher;

import com.mojang.blaze3d.vertex.VertexFormat;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.render.sky_light_shadow.SableSkyLightShadows;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.render.SubLevelRenderData;
import dev.ryanhcode.sable.sublevel.render.dispatcher.SubLevelRenderDispatcher;
import dev.ryanhcode.sable.sublevel.render.dispatcher.SubLevelTextureCache;
import dev.ryanhcode.sable.sublevel.render.fancy.FancySubLevelCommandBuilder;
import dev.ryanhcode.sable.sublevel.render.fancy.FancySubLevelOcclusionData;
import dev.ryanhcode.sable.sublevel.render.fancy.FancySubLevelRenderData;
import dev.ryanhcode.sable.sublevel.render.fancy.FancySubLevelSectionCompiler;
import dev.ryanhcode.sable.sublevel.render.staging.StagingBuffer;
import foundry.veil.api.client.render.CullFrustum;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.render.shader.uniform.ShaderUniform;
import foundry.veil.api.client.render.vertex.VertexArray;
import foundry.veil.api.client.render.vertex.VertexArrayBuilder;
import foundry.veil.impl.client.render.dynamicbuffer.VanillaShaderCompiler;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3ic;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.NativeResource;

public class FancySubLevelRenderDispatcher
implements SubLevelRenderDispatcher {
    private static final Matrix4f TRANSFORM = new Matrix4f();
    private static final int VERTEX_SIZE = 8;
    private final Map<String, CompletableFuture<ShaderProgram>> dynamicPrograms = new Object2ObjectArrayMap();
    private final StagingBuffer stagingBuffer = StagingBuffer.create();
    private final FancySubLevelSectionCompiler sectionCompiler = new FancySubLevelSectionCompiler(this.stagingBuffer, Minecraft.getInstance().getBlockRenderer(), Minecraft.getInstance().getBlockEntityRenderDispatcher());
    private final FancySubLevelCommandBuilder commandBuilder = new FancySubLevelCommandBuilder(this.stagingBuffer);
    private final VertexArray vertexArray = VertexArray.create();

    public FancySubLevelRenderDispatcher() {
        int vbo = this.vertexArray.getOrCreateBuffer(0);
        try (MemoryStack stack = MemoryStack.stackPush();){
            ByteBuffer buffer = stack.malloc(192);
            buffer.put((byte)0).put((byte)0).put((byte)1).put((byte)0).put((byte)0).put((byte)-1).put((byte)0).put((byte)0);
            buffer.put((byte)0).put((byte)0).put((byte)0).put((byte)0).put((byte)0).put((byte)-1).put((byte)0).put((byte)0);
            buffer.put((byte)1).put((byte)0).put((byte)0).put((byte)0).put((byte)0).put((byte)-1).put((byte)0).put((byte)0);
            buffer.put((byte)1).put((byte)0).put((byte)1).put((byte)0).put((byte)0).put((byte)-1).put((byte)0).put((byte)0);
            buffer.put((byte)0).put((byte)1).put((byte)0).put((byte)0).put((byte)0).put((byte)1).put((byte)0).put((byte)0);
            buffer.put((byte)0).put((byte)1).put((byte)1).put((byte)0).put((byte)0).put((byte)1).put((byte)0).put((byte)0);
            buffer.put((byte)1).put((byte)1).put((byte)1).put((byte)0).put((byte)0).put((byte)1).put((byte)0).put((byte)0);
            buffer.put((byte)1).put((byte)1).put((byte)0).put((byte)0).put((byte)0).put((byte)1).put((byte)0).put((byte)0);
            buffer.put((byte)1).put((byte)1).put((byte)0).put((byte)0).put((byte)0).put((byte)0).put((byte)-1).put((byte)0);
            buffer.put((byte)1).put((byte)0).put((byte)0).put((byte)0).put((byte)0).put((byte)0).put((byte)-1).put((byte)0);
            buffer.put((byte)0).put((byte)0).put((byte)0).put((byte)0).put((byte)0).put((byte)0).put((byte)-1).put((byte)0);
            buffer.put((byte)0).put((byte)1).put((byte)0).put((byte)0).put((byte)0).put((byte)0).put((byte)-1).put((byte)0);
            buffer.put((byte)0).put((byte)1).put((byte)1).put((byte)0).put((byte)0).put((byte)0).put((byte)1).put((byte)0);
            buffer.put((byte)0).put((byte)0).put((byte)1).put((byte)0).put((byte)0).put((byte)0).put((byte)1).put((byte)0);
            buffer.put((byte)1).put((byte)0).put((byte)1).put((byte)0).put((byte)0).put((byte)0).put((byte)1).put((byte)0);
            buffer.put((byte)1).put((byte)1).put((byte)1).put((byte)0).put((byte)0).put((byte)0).put((byte)1).put((byte)0);
            buffer.put((byte)0).put((byte)1).put((byte)0).put((byte)0).put((byte)-1).put((byte)0).put((byte)0).put((byte)0);
            buffer.put((byte)0).put((byte)0).put((byte)0).put((byte)0).put((byte)-1).put((byte)0).put((byte)0).put((byte)0);
            buffer.put((byte)0).put((byte)0).put((byte)1).put((byte)0).put((byte)-1).put((byte)0).put((byte)0).put((byte)0);
            buffer.put((byte)0).put((byte)1).put((byte)1).put((byte)0).put((byte)-1).put((byte)0).put((byte)0).put((byte)0);
            buffer.put((byte)1).put((byte)1).put((byte)1).put((byte)0).put((byte)1).put((byte)0).put((byte)0).put((byte)0);
            buffer.put((byte)1).put((byte)0).put((byte)1).put((byte)0).put((byte)1).put((byte)0).put((byte)0).put((byte)0);
            buffer.put((byte)1).put((byte)0).put((byte)0).put((byte)0).put((byte)1).put((byte)0).put((byte)0).put((byte)0);
            buffer.put((byte)1).put((byte)1).put((byte)0).put((byte)0).put((byte)1).put((byte)0).put((byte)0).put((byte)0);
            buffer.flip();
            ByteBuffer indices = stack.bytes(new byte[]{0, 1, 2, 2, 3, 0});
            VertexArray.upload((int)vbo, (ByteBuffer)buffer, (VertexArray.DrawUsage)VertexArray.DrawUsage.STATIC);
            this.vertexArray.uploadIndexBuffer(indices, VertexArray.IndexType.BYTE);
        }
        this.vertexArray.editFormat().defineVertexBuffer(0, vbo, 0, 8, 0).setVertexAttribute(0, 0, 3, VertexArrayBuilder.DataType.BYTE, false, 0).setVertexAttribute(1, 0, 3, VertexArrayBuilder.DataType.BYTE, false, 4).setVertexIAttribute(2, 1, 2, VertexArrayBuilder.DataType.UNSIGNED_INT, 0);
    }

    public void onResourceManagerReload(ResourceManager resourceManager) {
        this.freePrograms();
    }

    @Override
    public SubLevelRenderData resize(ClientSubLevel subLevel, SubLevelRenderData renderData) {
        ((FancySubLevelRenderData)renderData).resize();
        return renderData;
    }

    @Override
    public SubLevelRenderData createRenderData(ClientSubLevel subLevel) {
        return new FancySubLevelRenderData(subLevel, this.sectionCompiler);
    }

    @Nullable
    private ShaderProgram getDynamicProgram(ShaderInstance vanillaProgram) {
        String name = VanillaShaderCompiler.getActiveDynamicBuffers((ShaderInstance)vanillaProgram) + "/" + vanillaProgram.getName();
        CompletableFuture<ShaderProgram> future = this.dynamicPrograms.get(name);
        if (future != null) {
            return future.getNow(null);
        }
        try (MemoryStack stack = MemoryStack.stackPush();){
            int size = GL20C.glGetProgrami((int)vanillaProgram.getId(), (int)35717);
            Int2ObjectArrayMap sources = new Int2ObjectArrayMap(size);
            IntBuffer shaders = stack.mallocInt(size);
            GL20C.glGetAttachedShaders((int)vanillaProgram.getId(), null, (IntBuffer)shaders);
            for (int i = 0; i < shaders.limit(); ++i) {
                int shader2 = shaders.get(i);
                int type = GL20C.glGetShaderi((int)shader2, (int)35663);
                sources.put(type, (Object)GL20C.glGetShaderSource((int)shader2));
            }
            this.dynamicPrograms.put(name, (CompletableFuture<ShaderProgram>)VeilRenderSystem.renderer().getShaderManager().createDynamicProgram(Sable.sablePath("dynamic_sublevel/" + name), (Int2ObjectMap)sources).thenApplyAsync(shader -> {
                ShaderUniform sableEnableSkyLightShadows;
                ShaderUniform sableEnableNormalLighting = shader.getUniform((CharSequence)"SableEnableNormalLighting");
                if (sableEnableNormalLighting != null) {
                    sableEnableNormalLighting.setFloat(1.0f);
                }
                if ((sableEnableSkyLightShadows = shader.getUniform((CharSequence)"SableShadowsEnabled")) != null) {
                    sableEnableSkyLightShadows.setFloat(SableSkyLightShadows.isEnabled() ? 1.0f : 0.0f);
                }
                return shader;
            }, (Executor)Minecraft.getInstance()));
        }
        return null;
    }

    @Override
    public void rebuild(Iterable<ClientSubLevel> sublevels) {
        this.sectionCompiler.getBuffer().clear();
        SubLevelRenderDispatcher.super.rebuild(sublevels);
    }

    @Override
    public void updateCulling(Iterable<ClientSubLevel> sublevels, double cameraX, double cameraY, double cameraZ, CullFrustum cullFrustum, boolean isSpectator) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (ClientSubLevel subLevel : sublevels) {
            FancySubLevelRenderData renderData = (FancySubLevelRenderData)subLevel.getRenderData();
            Pose3dc renderPose = subLevel.renderPose();
            Vector3d plotPos = renderPose.transformPositionInverse(new Vector3d(cameraX, cameraY, cameraZ));
            Vector3ic chunkOrigin = renderData.getChunkOrigin();
            pos.set(plotPos.x, plotPos.y, plotPos.z);
            ClientLevel level = subLevel.getLevel();
            boolean smartCull = Minecraft.getInstance().smartCull;
            if (isSpectator && level.getBlockState((BlockPos)pos).isSolidRender((BlockGetter)level, (BlockPos)pos)) {
                smartCull = false;
            }
            renderData.getOcclusionData().update((pos.getX() >> 4) - chunkOrigin.x(), (pos.getY() >> 4) - chunkOrigin.y(), (pos.getZ() >> 4) - chunkOrigin.z(), smartCull, cullFrustum);
        }
    }

    @Override
    public void renderSectionLayer(Iterable<ClientSubLevel> sublevels, RenderType renderType, ShaderInstance shader, double cameraX, double cameraY, double cameraZ, Matrix4f modelView, Matrix4f projection, float partialTicks) {
        ShaderProgram program = this.getDynamicProgram(shader);
        if (program == null) {
            return;
        }
        if (!program.isValid()) {
            return;
        }
        boolean setup = false;
        SubLevelTextureCache textureCache = this.sectionCompiler.getTextureCache();
        ShaderUniform sableSkyLightScale = program.getUniform((CharSequence)"SableSkyLightScale");
        ShaderUniform sableTransform = program.getUniform((CharSequence)"SableTransform");
        this.stagingBuffer.updateFencedAreas();
        for (ClientSubLevel subLevel : sublevels) {
            FancySubLevelRenderData renderData = (FancySubLevelRenderData)subLevel.getRenderData();
            FancySubLevelOcclusionData occlusionData = renderData.getOcclusionData();
            if (!occlusionData.hasLayer(renderType)) continue;
            if (!setup) {
                program.bind();
                program.setDefaultUniforms(VertexFormat.Mode.TRIANGLES, (Matrix4fc)modelView, (Matrix4fc)projection);
                program.bindSamplers(0);
                textureCache.bind();
                this.vertexArray.bind();
                this.sectionCompiler.getBuffer().bind(this.vertexArray);
                this.commandBuilder.setup();
                setup = true;
            }
            if (sableSkyLightScale != null) {
                int skyLight = subLevel.getLatestSkyLightScale();
                sableSkyLightScale.setFloat((float)skyLight / 15.0f);
            }
            Pose3dc renderPose = subLevel.renderPose();
            Vector3dc renderPos = renderPose.position();
            Quaterniondc renderRot = renderPose.orientation();
            Vector3d renderCOR = renderRot.transform(new Vector3d(renderPose.rotationPoint()).sub(renderData.getOrigin()));
            if (sableTransform != null) {
                Matrix4f transform = TRANSFORM.identity();
                transform.translate((float)(renderPos.x() - renderCOR.x - cameraX), (float)(renderPos.y() - renderCOR.y - cameraY), (float)(renderPos.z() - renderCOR.z - cameraZ));
                transform.rotate((Quaternionfc)new Quaternionf(renderRot));
                sableTransform.setMatrix((Matrix4fc)transform);
            }
            Vector3d plotPos = renderPose.transformPositionInverse(new Vector3d(VeilRenderSystem.getCullingFrustum().getPosition()));
            this.commandBuilder.draw(renderData, renderType, Mth.floor((double)plotPos.x) >> 4, Mth.floor((double)plotPos.y) >> 4, Mth.floor((double)plotPos.z) >> 4);
        }
        this.stagingBuffer.updateFencedAreas();
        if (setup) {
            this.commandBuilder.clear();
        }
    }

    @Override
    public void renderAfterSections(Iterable<ClientSubLevel> sublevels, double cameraX, double cameraY, double cameraZ, Matrix4f modelView, Matrix4f projection, float partialTicks) {
    }

    @Override
    public void renderBlockEntities(Iterable<ClientSubLevel> sublevels, SubLevelRenderDispatcher.BlockEntityRenderer blockEntityRenderer, double cameraX, double cameraY, double cameraZ, float partialTick) {
    }

    @Override
    public void addDebugInfo(Consumer<String> consumer) {
        consumer.accept("Staging Buffer: Used %.1f / %d MiB".formatted((double)(this.stagingBuffer.getUsedSize() / 1024L) / 1024.0, this.stagingBuffer.getSize() / 1024L / 1024L));
    }

    private void freePrograms() {
        for (CompletableFuture<ShaderProgram> future : this.dynamicPrograms.values()) {
            future.thenAcceptAsync(NativeResource::free, (Executor)Minecraft.getInstance());
        }
        this.dynamicPrograms.clear();
    }

    public void free() {
        this.commandBuilder.free();
        this.sectionCompiler.free();
        this.stagingBuffer.free();
        this.vertexArray.free();
        this.freePrograms();
    }
}
