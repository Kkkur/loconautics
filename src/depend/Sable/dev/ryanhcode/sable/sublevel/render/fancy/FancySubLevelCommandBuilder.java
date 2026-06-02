/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.core.Direction
 *  net.minecraft.core.SectionPos
 *  org.lwjgl.opengl.GL15C
 *  org.lwjgl.opengl.GL43C
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.system.NativeResource
 */
package dev.ryanhcode.sable.sublevel.render.fancy;

import com.mojang.blaze3d.platform.GlStateManager;
import dev.ryanhcode.sable.sublevel.render.fancy.BucketRenderBuffer;
import dev.ryanhcode.sable.sublevel.render.fancy.FancySubLevelRenderData;
import dev.ryanhcode.sable.sublevel.render.fancy.FancySubLevelSectionCompiler;
import dev.ryanhcode.sable.sublevel.render.staging.StagingBuffer;
import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import org.lwjgl.opengl.GL15C;
import org.lwjgl.opengl.GL43C;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;

public class FancySubLevelCommandBuilder
implements NativeResource {
    private static final int MAX_SECTIONS = 4096;
    private static final int INDIRECT_COMMAND_SIZE = 20;
    private static final Direction[] DIRECTIONS = Direction.values();
    private final StagingBuffer stagingBuffer;
    private final int commandBuffer;
    private final Deque<FancySubLevelSectionCompiler.RenderSection> sectionQueue;
    private int drawCount;

    public FancySubLevelCommandBuilder(StagingBuffer stagingBuffer) {
        this.stagingBuffer = stagingBuffer;
        this.commandBuffer = GlStateManager._glGenBuffers();
        GL15C.glBindBuffer((int)36671, (int)this.commandBuffer);
        GL15C.glBufferData((int)36671, (long)81920L, (int)35040);
        GL15C.glBindBuffer((int)36671, (int)0);
        this.sectionQueue = new LinkedBlockingDeque<FancySubLevelSectionCompiler.RenderSection>();
    }

    private void flush() {
        if (this.drawCount > 0) {
            this.stagingBuffer.copy(this.commandBuffer, 0L);
            GL43C.glMultiDrawElementsIndirect((int)4, (int)5121, (long)0L, (int)this.drawCount, (int)0);
        }
        this.drawCount = 0;
    }

    public void setup() {
        GL15C.glBindBuffer((int)36671, (int)this.commandBuffer);
    }

    public void clear() {
        this.flush();
        GL15C.glBindBuffer((int)36671, (int)0);
    }

    public void free() {
        GlStateManager._glDeleteBuffers((int)this.commandBuffer);
    }

    public void draw(FancySubLevelRenderData data, RenderType renderType, int sectionX, int sectionY, int sectionZ) {
        for (FancySubLevelSectionCompiler.RenderSection section : data.getOcclusionData().getVisibleSections()) {
            SectionPos pos = section.getPos();
            int dx = pos.getX() - sectionX;
            int dy = pos.getY() - sectionY;
            int dz = pos.getZ() - sectionZ;
            FancySubLevelSectionCompiler.CompiledSection compiledSection = section.getCompiledSection();
            for (Direction direction : DIRECTIONS) {
                int dot;
                BucketRenderBuffer.Slice slice = compiledSection.get(renderType, direction);
                if (slice == null || (dot = direction.getStepX() * dx + direction.getStepY() * dy + direction.getStepZ() * dz) > 0) continue;
                long pointer = this.stagingBuffer.reserve(20L);
                MemoryUtil.memPutInt((long)pointer, (int)6);
                MemoryUtil.memPutInt((long)(pointer + 4L), (int)slice.length());
                MemoryUtil.memPutInt((long)(pointer + 8L), (int)0);
                MemoryUtil.memPutInt((long)(pointer + 12L), (int)(direction.get3DDataValue() * 4));
                MemoryUtil.memPutInt((long)(pointer + 16L), (int)slice.offset());
                ++this.drawCount;
                if (this.drawCount < 4096) continue;
                this.flush();
            }
        }
        this.flush();
        this.sectionQueue.clear();
    }
}
