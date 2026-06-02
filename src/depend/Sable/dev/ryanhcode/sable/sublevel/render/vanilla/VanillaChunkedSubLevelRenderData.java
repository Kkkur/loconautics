/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.shaders.Uniform
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.VertexBuffer
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  dev.ryanhcode.sable.companion.math.BoundingBox3ic
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  foundry.veil.api.compat.IrisCompat
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.PrioritizeChunkUpdates
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.client.renderer.chunk.RenderRegionCache
 *  net.minecraft.client.renderer.chunk.SectionRenderDispatcher
 *  net.minecraft.client.renderer.chunk.SectionRenderDispatcher$CompiledSection
 *  net.minecraft.client.renderer.chunk.SectionRenderDispatcher$RenderSection
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.world.level.Level
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaterniondc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3i
 *  org.joml.Vector3ic
 */
package dev.ryanhcode.sable.sublevel.render.vanilla;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.compatibility.SableIrisCompat;
import dev.ryanhcode.sable.mixin.sublevel_render.RenderSectionAccessor;
import dev.ryanhcode.sable.mixinterface.sublevel_render.vanilla.RenderSectionExtension;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.render.SubLevelRenderData;
import dev.ryanhcode.sable.sublevel.water_occlusion.WaterOcclusionContainer;
import dev.ryanhcode.sable.sublevel.water_occlusion.WaterOcclusionRegion;
import foundry.veil.api.compat.IrisCompat;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Objects;
import java.util.Set;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.PrioritizeChunkUpdates;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

public class VanillaChunkedSubLevelRenderData
implements SubLevelRenderData {
    private static final Matrix4f TRANSFORM = new Matrix4f();
    private static final Matrix4f MODEL_MATRIX = new Matrix4f();
    private final Vector3d origin = new Vector3d();
    private final Vector3i chunkOrigin = new Vector3i();
    private final ClientSubLevel subLevel;
    private final Vector3i size = new Vector3i();
    private final ObjectList<SectionRenderDispatcher.RenderSection> allRenderSections = new ObjectArrayList();
    private final ObjectList<SectionRenderDispatcher.RenderSection> dirtyRenderSections = new ObjectArrayList();
    private SectionRenderDispatcher.RenderSection[] renderSections = null;
    private final SectionRenderDispatcher sectionRenderDispatcher;

    public VanillaChunkedSubLevelRenderData(ClientSubLevel subLevel, SectionRenderDispatcher sectionRenderDispatcher) {
        this.subLevel = subLevel;
        this.sectionRenderDispatcher = sectionRenderDispatcher;
        this.resize();
    }

    private static SectionRenderDispatcher.RenderSection getSection(SectionRenderDispatcher.RenderSection[] sections, Vector3i size, Vector3i origin, int x, int y, int z) {
        int relX = x - origin.x();
        int relY = y - origin.y();
        int relZ = z - origin.z();
        if (relX < 0 || relY < 0 || relZ < 0) {
            return null;
        }
        if (relX >= size.x() || relY >= size.y() || relZ >= size.z()) {
            return null;
        }
        return sections[relX + relY * size.x() + relZ * size.x() * size.y()];
    }

    private int getIndex(int x, int y, int z) {
        return x - this.chunkOrigin.x() + (y - this.chunkOrigin.y()) * this.size.x() + (z - this.chunkOrigin.z()) * this.size.x() * this.size.y();
    }

    private boolean inBounds(int x, int y, int z) {
        int localX = x - this.chunkOrigin.x();
        int localY = y - this.chunkOrigin.y();
        int localZ = z - this.chunkOrigin.z();
        return localX >= 0 && localY >= 0 && localZ >= 0 && localX < this.size.x() && localY < this.size.y() && localZ < this.size.z();
    }

    public void resize() {
        SectionRenderDispatcher.RenderSection[] oldRenderSections = this.renderSections;
        ObjectArrayList oldRenderSectionsList = new ObjectArrayList(this.allRenderSections);
        this.renderSections = null;
        this.allRenderSections.clear();
        this.dirtyRenderSections.clear();
        BoundingBox3ic bounds = this.subLevel.getPlot().getBoundingBox();
        if (bounds != null && !bounds.equals((Object)BoundingBox3i.EMPTY) && (double)bounds.volume() > 0.0) {
            Vector3i minChunkPos = new Vector3i(bounds.minX() >> 4, bounds.minY() >> 4, bounds.minZ() >> 4);
            Vector3i maxChunkPos = new Vector3i(bounds.maxX() >> 4, bounds.maxY() >> 4, bounds.maxZ() >> 4);
            Vector3i oldSize = new Vector3i((Vector3ic)this.size);
            Vector3i oldOrigin = new Vector3i((Vector3ic)this.chunkOrigin);
            this.size.set(maxChunkPos.x() - minChunkPos.x() + 1, maxChunkPos.y() - minChunkPos.y() + 1, maxChunkPos.z() - minChunkPos.z() + 1);
            this.chunkOrigin.set((Vector3ic)minChunkPos);
            this.origin.set((double)(minChunkPos.x() << 4), (double)(minChunkPos.y() << 4), (double)(minChunkPos.z() << 4));
            this.renderSections = new SectionRenderDispatcher.RenderSection[this.size.x() * this.size.y() * this.size.z()];
            for (int x = minChunkPos.x(); x <= maxChunkPos.x(); ++x) {
                for (int y = minChunkPos.y(); y <= maxChunkPos.y(); ++y) {
                    for (int z = minChunkPos.z(); z <= maxChunkPos.z(); ++z) {
                        SectionRenderDispatcher.RenderSection newSection;
                        SectionRenderDispatcher.RenderSection oldSection = VanillaChunkedSubLevelRenderData.getSection(oldRenderSections, oldSize, oldOrigin, x, y, z);
                        if (oldRenderSections != null && oldSection != null) {
                            newSection = oldSection;
                        } else {
                            SectionRenderDispatcher sectionRenderDispatcher = this.sectionRenderDispatcher;
                            Objects.requireNonNull(sectionRenderDispatcher);
                            newSection = new SectionRenderDispatcher.RenderSection(sectionRenderDispatcher, -1, x << 4, y << 4, z << 4);
                            ((RenderSectionExtension)newSection).sable$addDirtyListener(arg_0 -> this.dirtyRenderSections.add(arg_0));
                        }
                        if (newSection.isDirty()) {
                            this.dirtyRenderSections.add((Object)newSection);
                        }
                        this.renderSections[this.getIndex((int)x, (int)y, (int)z)] = newSection;
                        this.allRenderSections.add((Object)newSection);
                    }
                }
            }
            if (oldRenderSections != null) {
                for (SectionRenderDispatcher.RenderSection oldSection : oldRenderSectionsList) {
                    SectionPos oldSectionPos = SectionPos.of((BlockPos)oldSection.getOrigin());
                    if (oldSectionPos.getX() >= minChunkPos.x() && oldSectionPos.getX() <= maxChunkPos.x() && oldSectionPos.getY() >= minChunkPos.y() && oldSectionPos.getY() <= maxChunkPos.y() && oldSectionPos.getZ() >= minChunkPos.z() && oldSectionPos.getZ() <= maxChunkPos.z()) continue;
                    oldSection.releaseBuffers();
                    oldSection.updateGlobalBlockEntities(Set.of());
                    oldSection.setCompiled(SectionRenderDispatcher.CompiledSection.EMPTY);
                }
            }
        }
    }

    @Override
    public void rebuild() {
        for (SectionRenderDispatcher.RenderSection renderSection : this.allRenderSections) {
            renderSection.setDirty(true);
            ((RenderSectionAccessor)renderSection).getGlobalBlockEntities().clear();
        }
    }

    @Override
    public void compileSections(PrioritizeChunkUpdates chunkUpdates, RenderRegionCache renderRegionCache, Camera camera) {
        if (this.dirtyRenderSections.isEmpty()) {
            return;
        }
        ProfilerFiller profiler = Minecraft.getInstance().getProfiler();
        Vector3d cameraPos = JOMLConversion.atCenterOf((Vec3i)camera.getBlockPosition()).sub(8.0, 8.0, 8.0);
        this.subLevel.logicalPose().transformPositionInverse(cameraPos);
        for (SectionRenderDispatcher.RenderSection renderSection : this.dirtyRenderSections) {
            ((RenderSectionExtension)renderSection).sable$setListening(false);
            boolean buildSync = false;
            if (chunkUpdates == PrioritizeChunkUpdates.NEARBY) {
                BlockPos origin = renderSection.getOrigin();
                buildSync = cameraPos.distanceSquared((double)origin.getX(), (double)origin.getY(), (double)origin.getZ()) < 768.0 || renderSection.isDirtyFromPlayer();
            } else if (chunkUpdates == PrioritizeChunkUpdates.PLAYER_AFFECTED) {
                buildSync = renderSection.isDirtyFromPlayer();
            }
            if (buildSync) {
                profiler.push("sublevel_build_near_sync");
                this.sectionRenderDispatcher.rebuildSectionSync(renderSection, renderRegionCache);
                profiler.pop();
            } else {
                profiler.push("sublevel_schedule_async_compile");
                renderSection.rebuildSectionAsync(this.sectionRenderDispatcher, renderRegionCache);
                profiler.pop();
            }
            renderSection.setNotDirty();
            ((RenderSectionExtension)renderSection).sable$setListening(true);
        }
        this.dirtyRenderSections.clear();
    }

    @Override
    public ClientSubLevel getSubLevel() {
        return this.subLevel;
    }

    @Override
    public boolean isSectionCompiled(int x, int y, int z) {
        if (this.renderSections == null) {
            return false;
        }
        if (!this.inBounds(x, y, z)) {
            return true;
        }
        int index = this.getIndex(x, y, z);
        return index >= 0 && index < this.renderSections.length && this.renderSections[index].compiled.get() != SectionRenderDispatcher.CompiledSection.UNCOMPILED;
    }

    @Override
    public void setDirty(int x, int y, int z, boolean playerChanged) {
        if (this.renderSections == null) {
            return;
        }
        if (!this.inBounds(x, y, z)) {
            return;
        }
        int index = this.getIndex(x, y, z);
        if (index >= 0 && index < this.renderSections.length) {
            this.renderSections[index].setDirty(playerChanged);
        }
    }

    public ObjectList<SectionRenderDispatcher.RenderSection> allRenderSections() {
        return this.allRenderSections;
    }

    public void renderChunkedSubLevel(RenderType layer, ShaderInstance shader, Matrix4f modelView, double camX, double camY, double camZ) {
        Uniform sableSkyLightScale;
        Camera camera;
        WaterOcclusionContainer<?> container;
        Object occludingRegion;
        Pose3dc renderPose = this.subLevel.renderPose();
        Vector3d renderPos = new Vector3d(renderPose.position());
        Quaterniondc renderRot = renderPose.orientation();
        Vector3d renderCOR = renderRot.transform(new Vector3d(renderPose.rotationPoint()).sub((Vector3dc)this.origin));
        float[] oldFogColor = null;
        if (shader.FOG_COLOR != null && (occludingRegion = (container = WaterOcclusionContainer.getContainer((Level)this.subLevel.getLevel())).getOccludingRegion((camera = Minecraft.getInstance().gameRenderer.getMainCamera()).getPosition())) != null && Sable.HELPER.getContaining((Level)this.subLevel.getLevel(), (Vec3i)((WaterOcclusionRegion)occludingRegion).getVolume().getMinBlockPos()) == this.subLevel) {
            oldFogColor = RenderSystem.getShaderFogColor();
            shader.FOG_COLOR.set(0.0f, 0.0f, 0.0f, 0.0f);
            shader.FOG_COLOR.upload();
        }
        if ((sableSkyLightScale = shader.getUniform("SableSkyLightScale")) != null) {
            int skyLight = this.subLevel.getLatestSkyLightScale();
            sableSkyLightScale.set((float)skyLight / 15.0f);
            sableSkyLightScale.upload();
        }
        renderPos.sub((Vector3dc)renderCOR);
        Matrix4f transform = TRANSFORM.identity();
        Vector3d fogOffset = new Vector3d(camX, camY, camZ).sub((Vector3dc)renderPos).mul(-1.0);
        transform.translate((float)(renderPos.x() - camX - fogOffset.x), (float)(renderPos.y() - camY - fogOffset.y), (float)(renderPos.z() - camZ - fogOffset.z));
        transform.rotate((Quaternionfc)new Quaternionf(renderRot));
        if (shader.MODEL_VIEW_MATRIX != null) {
            shader.MODEL_VIEW_MATRIX.set(modelView.mul((Matrix4fc)transform, MODEL_MATRIX));
            shader.MODEL_VIEW_MATRIX.upload();
            if (IrisCompat.isLoaded()) {
                SableIrisCompat.refreshModelMatrices(shader);
            }
        }
        Uniform chunkOffsetUniform = shader.CHUNK_OFFSET;
        for (SectionRenderDispatcher.RenderSection renderSection : this.allRenderSections) {
            if (renderSection.getCompiled().isEmpty(layer)) continue;
            if (chunkOffsetUniform != null) {
                BlockPos pos = renderSection.getOrigin();
                Vector3d fogOffsetRot = renderRot.transformInverse((Vector3dc)fogOffset, new Vector3d());
                chunkOffsetUniform.set((float)((double)pos.getX() - this.origin.x() + fogOffsetRot.x), (float)((double)pos.getY() - this.origin.y() + fogOffsetRot.y), (float)((double)pos.getZ() - this.origin.z() + fogOffsetRot.z));
                chunkOffsetUniform.upload();
            }
            VertexBuffer buffer = renderSection.getBuffer(layer);
            buffer.bind();
            buffer.draw();
        }
        if (chunkOffsetUniform != null) {
            chunkOffsetUniform.set(0.0f, 0.0f, 0.0f);
        }
        if (oldFogColor != null) {
            shader.FOG_COLOR.set(oldFogColor[0], oldFogColor[1], oldFogColor[2], oldFogColor[3]);
        }
    }

    @Override
    public void close() {
        for (SectionRenderDispatcher.RenderSection section : this.allRenderSections) {
            section.releaseBuffers();
            section.updateGlobalBlockEntities(Set.of());
            section.setCompiled(SectionRenderDispatcher.CompiledSection.EMPTY);
        }
        this.allRenderSections.clear();
        this.renderSections = null;
    }

    public SectionRenderDispatcher.RenderSection getRenderSection(SectionPos sectionPos) {
        if (this.renderSections == null) {
            return null;
        }
        int index = this.getIndex(sectionPos.getX(), sectionPos.getY(), sectionPos.getZ());
        if (index < 0 || index >= this.renderSections.length) {
            return null;
        }
        return this.renderSections[index];
    }
}
