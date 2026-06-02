/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.MeshData
 *  com.mojang.blaze3d.vertex.MeshData$SortState
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.chunk.VisibilitySet
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.system.NativeResource
 */
package dev.ryanhcode.sable.sublevel.render.fancy;

import com.mojang.blaze3d.vertex.MeshData;
import dev.ryanhcode.sable.sublevel.render.fancy.SubLevelMeshBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.VisibilitySet;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NativeResource;

public static final class SubLevelMeshBuilder.Results
implements NativeResource {
    public final List<BlockEntity> globalBlockEntities = new ArrayList<BlockEntity>();
    public final List<BlockEntity> blockEntities = new ArrayList<BlockEntity>();
    public final Map<RenderType, SubLevelMeshBuilder.QuadMesh> renderedQuadLayers = new Reference2ObjectArrayMap();
    public final Map<RenderType, MeshData> renderedModelLayers = new Reference2ObjectArrayMap();
    public VisibilitySet visibilitySet = new VisibilitySet();
    @Nullable
    public MeshData.SortState transparencyState;

    public void free() {
        this.renderedModelLayers.values().forEach(MeshData::close);
    }
}
