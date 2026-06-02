/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.blaze3d.vertex.MeshData$SortState
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.chunk.VisibilitySet
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.system.NativeResource
 */
package dev.ryanhcode.sable.sublevel.render.fancy;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.MeshData;
import dev.ryanhcode.sable.sublevel.render.fancy.BucketRenderBuffer;
import dev.ryanhcode.sable.sublevel.render.fancy.SubLevelMeshBuilder;
import dev.ryanhcode.sable.sublevel.render.fancy.task.SubLevelTask;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.VisibilitySet;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NativeResource;

public static class FancySubLevelSectionCompiler.CompiledSection
implements NativeResource {
    public static final FancySubLevelSectionCompiler.CompiledSection UNCOMPILED = new FancySubLevelSectionCompiler.CompiledSection(){

        @Override
        public boolean facesCanSeeEachother(Direction face, Direction otherFace) {
            return false;
        }
    };
    public static final FancySubLevelSectionCompiler.CompiledSection EMPTY = new FancySubLevelSectionCompiler.CompiledSection(){

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

    public static FancySubLevelSectionCompiler.CompiledSection create(SubLevelMeshBuilder.Results results, SubLevelTask.MeshUploader meshUploader) {
        try (SubLevelMeshBuilder.Results results2 = results;){
            FancySubLevelSectionCompiler.CompiledSection compiledSection = new FancySubLevelSectionCompiler.CompiledSection();
            compiledSection.visibilitySet = results.visibilitySet;
            compiledSection.renderableBlockEntities.addAll(results.blockEntities);
            compiledSection.transparencyState = results.transparencyState;
            ArrayList<CompletionStage> futures = new ArrayList<CompletionStage>(results.renderedQuadLayers.size());
            for (Map.Entry<RenderType, SubLevelMeshBuilder.QuadMesh> entry : results.renderedQuadLayers.entrySet()) {
                futures.add(meshUploader.upload(entry.getValue()).thenAcceptAsync(slice -> compiledSection.quadLayers.put((RenderType)entry.getKey(), (BucketRenderBuffer.Slice[])slice), (Executor)Minecraft.getInstance()));
            }
            CompletableFuture.allOf((CompletableFuture[])futures.toArray(CompletableFuture[]::new)).join();
            FancySubLevelSectionCompiler.CompiledSection compiledSection2 = compiledSection;
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
