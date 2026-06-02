/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  dev.ryanhcode.sable.companion.math.BoundingBox3ic
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer
 *  net.caffeinemc.mods.sodium.client.render.chunk.RenderSection
 *  net.minecraft.client.Camera
 *  net.minecraft.client.PrioritizeChunkUpdates
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.chunk.RenderRegionCache
 *  net.minecraft.core.SectionPos
 *  net.minecraft.world.level.lighting.LevelLightEngine
 *  org.joml.Vector3d
 *  org.joml.Vector3i
 *  org.joml.Vector3ic
 */
package dev.ryanhcode.sable.sublevel.render.sodium;

import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.mixinterface.sublevel_render.sodium.RenderSectionManagerExtension;
import dev.ryanhcode.sable.mixinterface.sublevel_render.sodium.SodiumWorldRendererExtension;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.render.SubLevelRenderData;
import dev.ryanhcode.sable.sublevel.render.sodium.SubLevelRenderSectionManager;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.minecraft.client.Camera;
import net.minecraft.client.PrioritizeChunkUpdates;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.joml.Vector3ic;

public class SodiumSubLevelRenderData
implements SubLevelRenderData {
    public final Vector3d origin = new Vector3d();
    public final Vector3i chunkOrigin = new Vector3i();
    private final ClientSubLevel subLevel;
    private final Vector3i size = new Vector3i();
    private boolean initialized = false;
    private final ObjectSet<SectionPos> newSections = new ObjectOpenHashSet();
    private final ObjectSet<SectionPos> visibleSections = new ObjectOpenHashSet();

    public SodiumSubLevelRenderData(ClientSubLevel subLevel) {
        this.subLevel = subLevel;
        this.resize();
    }

    public void resize() {
        SodiumWorldRenderer worldRenderer = SodiumWorldRenderer.instance();
        SubLevelRenderSectionManager renderSectionManager = ((SodiumWorldRendererExtension)worldRenderer).sable$getSubLevelRenderSectionManager(this.subLevel);
        if (renderSectionManager == null) {
            return;
        }
        this.initialized = true;
        BoundingBox3ic bounds = this.subLevel.getPlot().getBoundingBox();
        if (bounds != null && !bounds.equals((Object)BoundingBox3i.EMPTY) && (double)bounds.volume() > 0.0) {
            Vector3i minChunkPos = new Vector3i(bounds.minX() >> 4, bounds.minY() >> 4, bounds.minZ() >> 4);
            Vector3i maxChunkPos = new Vector3i(bounds.maxX() >> 4, bounds.maxY() >> 4, bounds.maxZ() >> 4);
            Vector3i oldSize = new Vector3i((Vector3ic)this.size);
            Vector3i oldOrigin = new Vector3i((Vector3ic)this.chunkOrigin);
            this.size.set(maxChunkPos.x() - minChunkPos.x() + 1, maxChunkPos.y() - minChunkPos.y() + 1, maxChunkPos.z() - minChunkPos.z() + 1);
            this.chunkOrigin.set((Vector3ic)minChunkPos);
            this.origin.set((double)(minChunkPos.x() << 4), (double)(minChunkPos.y() << 4), (double)(minChunkPos.z() << 4));
            RenderSectionManagerExtension renderSectionManagerExtension = (RenderSectionManagerExtension)((Object)renderSectionManager);
            for (int x = minChunkPos.x(); x <= maxChunkPos.x(); ++x) {
                for (int y = minChunkPos.y(); y <= maxChunkPos.y(); ++y) {
                    for (int z = minChunkPos.z(); z <= maxChunkPos.z(); ++z) {
                        if (this.visibleSections.contains((Object)SectionPos.of((int)x, (int)y, (int)z))) continue;
                        this.newSections.add((Object)SectionPos.of((int)x, (int)y, (int)z));
                    }
                }
            }
        }
    }

    @Override
    public void rebuild() {
    }

    @Override
    public boolean isSectionCompiled(int x, int y, int z) {
        return false;
    }

    @Override
    public void setDirty(int x, int y, int z, boolean playerChanged) {
    }

    @Override
    public void compileSections(PrioritizeChunkUpdates chunkUpdates, RenderRegionCache renderRegionCache, Camera camera) {
    }

    @Override
    public ClientSubLevel getSubLevel() {
        return this.subLevel;
    }

    public void renderAdditional() {
        if (!this.initialized) {
            this.resize();
        }
    }

    public void updateChunks(boolean updateChunksImmediately) {
        if (this.newSections.isEmpty()) {
            return;
        }
        ClientLevel level = this.subLevel.getLevel();
        LevelLightEngine lightEngine = level.getLightEngine();
        ObjectIterator iterator = this.newSections.iterator();
        SubLevelRenderSectionManager renderSectionManager = ((SodiumWorldRendererExtension)SodiumWorldRenderer.instance()).sable$getSubLevelRenderSectionManager(this.subLevel);
        RenderSectionManagerExtension renderSectionManagerExtension = (RenderSectionManagerExtension)((Object)renderSectionManager);
        for (int count = 0; iterator.hasNext() && count < 1000; ++count) {
            SectionPos newSection = (SectionPos)iterator.next();
            if (lightEngine.lightOnInSection(newSection) && !this.add(renderSectionManagerExtension, newSection, updateChunksImmediately)) continue;
        }
    }

    private boolean add(RenderSectionManagerExtension manager, SectionPos section, boolean updateChunksImmediately) {
        RenderSection renderChunk = manager.sable$getRenderSection(section.x(), section.y(), section.z());
        if (renderChunk == null) {
            manager.sable$setRenderSectionDirty(section.x(), section.y(), section.z(), false);
            return false;
        }
        if (renderChunk.getOriginX() != section.origin().getX() || renderChunk.getOriginY() != section.origin().getY() || renderChunk.getOriginZ() != section.origin().getZ()) {
            manager.sable$setRenderSectionDirty(section.x(), section.y(), section.z(), false);
            renderChunk = manager.sable$getRenderSection(section.x(), section.y(), section.z());
            if (renderChunk == null || renderChunk.getOriginX() != section.origin().getX() || renderChunk.getOriginY() != section.origin().getY() || renderChunk.getOriginZ() != section.origin().getZ()) {
                return false;
            }
        }
        manager.sable$setRenderSectionDirty(section.x(), section.y(), section.z(), true);
        return true;
    }

    @Override
    public void close() {
        ((SodiumWorldRendererExtension)SodiumWorldRenderer.instance()).sable$freeRenderSectionManager(this.subLevel);
    }
}
