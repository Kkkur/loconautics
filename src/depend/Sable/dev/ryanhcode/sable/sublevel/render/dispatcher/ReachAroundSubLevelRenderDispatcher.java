/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.Util
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.RenderBuffers
 *  net.minecraft.client.renderer.SectionBufferBuilderPool
 *  net.minecraft.client.renderer.chunk.SectionRenderDispatcher
 */
package dev.ryanhcode.sable.sublevel.render.dispatcher;

import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.render.SubLevelRenderData;
import dev.ryanhcode.sable.sublevel.render.dispatcher.VanillaSubLevelRenderDispatcher;
import dev.ryanhcode.sable.sublevel.render.vanilla.VanillaChunkedSubLevelRenderData;
import dev.ryanhcode.sable.sublevel.render.vanilla.VanillaSingleSubLevelRenderData;
import java.util.concurrent.Executor;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.SectionBufferBuilderPool;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;

public class ReachAroundSubLevelRenderDispatcher
extends VanillaSubLevelRenderDispatcher {
    private SectionBufferBuilderPool sectionBufferPool;
    private SectionRenderDispatcher sectionRenderDispatcher;

    public ReachAroundSubLevelRenderDispatcher() {
        int processors = Runtime.getRuntime().availableProcessors();
        int num = Math.max(1, processors / 4);
        this.sectionBufferPool = SectionBufferBuilderPool.allocate((int)num);
    }

    private SectionRenderDispatcher getSectionRenderDispatcher(LevelRenderer levelRenderer, ClientLevel level) {
        if (this.sectionRenderDispatcher == null) {
            Minecraft minecraft = Minecraft.getInstance();
            RenderBuffers renderBuffers = minecraft.renderBuffers();
            this.sectionRenderDispatcher = new SectionRenderDispatcher(level, levelRenderer, (Executor)Util.backgroundExecutor(), renderBuffers, minecraft.getBlockRenderer(), minecraft.getBlockEntityRenderDispatcher());
            this.sectionRenderDispatcher.bufferPool = this.sectionBufferPool;
        }
        this.sectionRenderDispatcher.setLevel(level);
        return this.sectionRenderDispatcher;
    }

    @Override
    public void rebuild(Iterable<ClientSubLevel> sublevels) {
        if (this.sectionRenderDispatcher != null) {
            this.sectionRenderDispatcher.blockUntilClear();
        }
        super.rebuild(sublevels);
    }

    @Override
    public SubLevelRenderData createRenderData(ClientSubLevel subLevel) {
        if (ReachAroundSubLevelRenderDispatcher.isSingleBlock(subLevel)) {
            return new VanillaSingleSubLevelRenderData(subLevel);
        }
        Minecraft minecraft = Minecraft.getInstance();
        LevelRenderer levelRenderer = minecraft.levelRenderer;
        SectionRenderDispatcher sectionRenderDispatcher = this.getSectionRenderDispatcher(levelRenderer, subLevel.getLevel());
        return new VanillaChunkedSubLevelRenderData(subLevel, sectionRenderDispatcher);
    }

    @Override
    public void preRenderChunks(Camera camera) {
        if (this.sectionRenderDispatcher != null) {
            Minecraft minecraft = Minecraft.getInstance();
            this.sectionRenderDispatcher.setCamera(camera.getPosition());
            minecraft.getProfiler().push("sub_level_upload");
            this.sectionRenderDispatcher.uploadAllPendingUploads();
            minecraft.getProfiler().pop();
        }
    }

    @Override
    public void free() {
        if (this.sectionRenderDispatcher != null) {
            this.sectionRenderDispatcher.dispose();
            this.sectionRenderDispatcher = null;
            this.sectionBufferPool = null;
        }
        super.free();
    }
}
