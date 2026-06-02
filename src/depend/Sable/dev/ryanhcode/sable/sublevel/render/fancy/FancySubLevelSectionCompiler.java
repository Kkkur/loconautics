/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.blaze3d.vertex.MeshData$SortState
 *  foundry.veil.api.client.render.VeilRenderSystem
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.block.BlockRenderDispatcher
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher
 *  net.minecraft.client.renderer.chunk.VisibilitySet
 *  net.minecraft.core.Direction
 *  net.minecraft.core.SectionPos
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3ic
 *  org.lwjgl.system.NativeResource
 */
package dev.ryanhcode.sable.sublevel.render.fancy;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.MeshData;
import dev.ryanhcode.sable.sublevel.render.dispatcher.SubLevelTextureCache;
import dev.ryanhcode.sable.sublevel.render.fancy.BucketRenderBuffer;
import dev.ryanhcode.sable.sublevel.render.fancy.SubLevelMeshBuilder;
import dev.ryanhcode.sable.sublevel.render.fancy.task.FancySubLevelTaskScheduler;
import dev.ryanhcode.sable.sublevel.render.fancy.task.SubLevelTask;
import dev.ryanhcode.sable.sublevel.render.staging.StagingBuffer;
import foundry.veil.api.client.render.VeilRenderSystem;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.chunk.VisibilitySet;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3ic;
import org.lwjgl.system.NativeResource;

public class FancySubLevelSectionCompiler
implements SubLevelTask.MeshUploader,
NativeResource {
    private final BucketRenderBuffer buffer;
    private final SubLevelTextureCache textureCache;
    private final SubLevelMeshBuilder meshBuilder;
    private final FancySubLevelTaskScheduler scheduler;

    public FancySubLevelSectionCompiler(StagingBuffer stagingBuffer, BlockRenderDispatcher blockRenderDispatcher, BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        this.buffer = new BucketRenderBuffer(stagingBuffer);
        this.textureCache = new SubLevelTextureCache();
        this.meshBuilder = new SubLevelMeshBuilder(blockRenderDispatcher, blockEntityRenderDispatcher, this.textureCache);
        this.scheduler = new FancySubLevelTaskScheduler(this, Runtime.getRuntime().availableProcessors());
        this.scheduler.start();
    }

    @Override
    public CompletableFuture<BucketRenderBuffer.Slice[]> upload(SubLevelMeshBuilder.QuadMesh mesh) {
        return CompletableFuture.supplyAsync(() -> {
            IntList[] faces = mesh.getFaces();
            BucketRenderBuffer.Slice[] slices = new BucketRenderBuffer.Slice[faces.length];
            for (int i = 0; i < faces.length; ++i) {
                IntList array = faces[i];
                if (array.isEmpty()) continue;
                BucketRenderBuffer.Slice slice = this.buffer.allocate(array.size() * 4 / 8);
                slice.writeInt().put(array.toIntArray());
                slice.flush();
                slices[i] = slice;
            }
            return slices;
        }, (Executor)Minecraft.getInstance());
    }

    public BucketRenderBuffer getBuffer() {
        return this.buffer;
    }

    public SubLevelTextureCache getTextureCache() {
        return this.textureCache;
    }

    @Override
    public SubLevelMeshBuilder getMeshBuilder() {
        return this.meshBuilder;
    }

    public FancySubLevelTaskScheduler getScheduler() {
        return this.scheduler;
    }

    public void free() {
        this.buffer.free();
        this.textureCache.free();
    }

    public static class CompiledSection
    implements NativeResource {
        public static final CompiledSection UNCOMPILED = new CompiledSection(){

            @Override
            public boolean facesCanSeeEachother(Direction face, Direction otherFace) {
                return false;
            }
        };
        public static final CompiledSection EMPTY = new CompiledSection(){

            @Override
            public boolean facesCanSeeEachother(Direction face, Direction otherFace) {
                return true;
            }
        };
        private final Map<RenderType, BucketRenderBuffer.Slice[]> quadLayers = new Reference2ObjectArrayMap(RenderType.chunkBufferLayers().size());
        private final List<BlockEntity> renderableBlockEntities = Lists.newArrayList();
        private VisibilitySet visibilitySet = new VisibilitySet();
        @Nullable
        private MeshData.SortState transparencyState;

        public static CompiledSection create(SubLevelMeshBuilder.Results results, SubLevelTask.MeshUploader meshUploader) {
            try (SubLevelMeshBuilder.Results results2 = results;){
                CompiledSection compiledSection = new CompiledSection();
                compiledSection.visibilitySet = results.visibilitySet;
                compiledSection.renderableBlockEntities.addAll(results.blockEntities);
                compiledSection.transparencyState = results.transparencyState;
                ArrayList<CompletionStage> futures = new ArrayList<CompletionStage>(results.renderedQuadLayers.size());
                for (Map.Entry<RenderType, SubLevelMeshBuilder.QuadMesh> entry : results.renderedQuadLayers.entrySet()) {
                    futures.add(meshUploader.upload(entry.getValue()).thenAcceptAsync(slice -> compiledSection.quadLayers.put((RenderType)entry.getKey(), (BucketRenderBuffer.Slice[])slice), (Executor)Minecraft.getInstance()));
                }
                CompletableFuture.allOf((CompletableFuture[])futures.toArray(CompletableFuture[]::new)).join();
                CompiledSection compiledSection2 = compiledSection;
                return compiledSection2;
            }
        }

        public Collection<RenderType> getLayers() {
            return this.quadLayers.keySet();
        }

        public boolean hasNoRenderableLayers() {
            return this.quadLayers.isEmpty();
        }

        @Nullable
        public BucketRenderBuffer.Slice get(RenderType renderType, Direction face) {
            BucketRenderBuffer.Slice[] slices = this.quadLayers.get(renderType);
            return slices != null ? slices[face.get3DDataValue()] : null;
        }

        public boolean isEmpty(RenderType renderType) {
            return !this.quadLayers.containsKey(renderType);
        }

        public List<BlockEntity> getRenderableBlockEntities() {
            return this.renderableBlockEntities;
        }

        public boolean facesCanSeeEachother(Direction face, Direction otherFace) {
            return this.visibilitySet.visibilityBetween(face, otherFace);
        }

        public void free() {
            for (BucketRenderBuffer.Slice[] value : this.quadLayers.values()) {
                for (BucketRenderBuffer.Slice slice : value) {
                    if (slice == null) continue;
                    slice.free();
                }
            }
            this.quadLayers.clear();
        }
    }

    public static class RenderSection
    implements NativeResource {
        private final SectionPos pos;
        private final Vector3ic origin;
        private final AtomicReference<CompiledSection> compiledSection;
        private boolean dirty;
        private boolean dirtyFromPlayer;

        public RenderSection(SectionPos pos, Vector3ic origin) {
            this.pos = pos;
            this.origin = origin;
            this.compiledSection = new AtomicReference<CompiledSection>(CompiledSection.UNCOMPILED);
            this.dirty = true;
            this.dirtyFromPlayer = false;
        }

        public void setCompiledSection(CompiledSection compiledSection) {
            CompiledSection oldSection = this.compiledSection.getAndSet(compiledSection);
            if (oldSection != null) {
                VeilRenderSystem.renderThreadExecutor().execute(oldSection::free);
            }
        }

        public void setDirty(boolean playerChanged) {
            this.dirty = true;
            this.dirtyFromPlayer |= playerChanged;
        }

        public void setNotDirty() {
            this.dirty = false;
            this.dirtyFromPlayer = false;
        }

        public SectionPos getPos() {
            return this.pos;
        }

        public Vector3ic getOrigin() {
            return this.origin;
        }

        public CompiledSection getCompiledSection() {
            return this.compiledSection.get();
        }

        public boolean isDirty() {
            return this.dirty;
        }

        public boolean isDirtyFromPlayer() {
            return this.dirtyFromPlayer;
        }

        public void free() {
            this.compiledSection.getAndSet(CompiledSection.UNCOMPILED).free();
        }
    }
}
