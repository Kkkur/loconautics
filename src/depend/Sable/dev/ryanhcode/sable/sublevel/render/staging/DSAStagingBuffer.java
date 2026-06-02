/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongList
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.lwjgl.opengl.GL45C
 *  org.lwjgl.system.MemoryStack
 */
package dev.ryanhcode.sable.sublevel.render.staging;

import com.mojang.blaze3d.platform.GlStateManager;
import dev.ryanhcode.sable.sublevel.render.staging.StagingBuffer;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL45C;
import org.lwjgl.system.MemoryStack;

@ApiStatus.Internal
public class DSAStagingBuffer
extends StagingBuffer {
    private final long size;
    private final int buffer;
    private final long pointer;
    private long writePointer;
    private long writeRegionSize;
    private final LongList flushRegions;
    private final List<FencedArea> fences;

    DSAStagingBuffer(long size) {
        this.size = size;
        this.buffer = GlStateManager._glGenBuffers();
        GL45C.glNamedBufferStorage((int)this.buffer, (long)size, (int)578);
        this.pointer = GL45C.nglMapNamedBufferRange((int)this.buffer, (long)0L, (long)size, (int)90);
        this.flushRegions = new LongArrayList();
        this.fences = new ObjectArrayList();
        this.writePointer = 0L;
        this.writeRegionSize = size;
    }

    @Override
    public void updateFencedAreas() {
        if (this.fences.isEmpty()) {
            return;
        }
        try (MemoryStack stack = MemoryStack.stackPush();){
            IntBuffer size = stack.mallocInt(1);
            Iterator<FencedArea> iterator = this.fences.iterator();
            while (iterator.hasNext()) {
                FencedArea area = iterator.next();
                long fence = area.fence;
                int status = GL45C.glGetSynci((long)fence, (int)37140, (IntBuffer)size);
                if (size.get(0) != 1) {
                    throw new IllegalStateException("Expected 1 value from fence");
                }
                if (status != 37145) continue;
                GL45C.glDeleteSync((long)fence);
                iterator.remove();
            }
        }
    }

    private long allocate(long size) {
        long pointer = this.pointer + this.writePointer;
        if (!this.flushRegions.isEmpty()) {
            long length;
            long offset = this.flushRegions.getLong(this.flushRegions.size() - 2);
            if (offset + (length = this.flushRegions.getLong(this.flushRegions.size() - 1)) == this.writePointer) {
                this.flushRegions.set(this.flushRegions.size() - 1, length + size);
            } else {
                this.flushRegions.add(this.writePointer);
                this.flushRegions.add(size);
            }
        } else {
            this.flushRegions.add(this.writePointer);
            this.flushRegions.add(size);
        }
        this.writePointer += size;
        this.writeRegionSize -= size;
        return pointer;
    }

    @Override
    public long reserve(long size) {
        if (this.writePointer + size >= this.writeRegionSize) {
            this.updateFencedAreas();
            if (this.fences.isEmpty()) {
                this.writePointer = 0L;
                this.writeRegionSize = this.size;
                return this.allocate(size);
            }
            FencedArea fence = this.fences.getLast();
            if (fence.offset + fence.length + size < this.size) {
                long end = fence.offset + fence.length;
                for (int i = 0; i < this.flushRegions.size(); i += 2) {
                    long length;
                    long offset = this.flushRegions.getLong(i);
                    if (offset + (length = this.flushRegions.getLong(this.flushRegions.size() - 1)) + size >= this.size) {
                        this.writePointer = 0L;
                        this.writeRegionSize = this.fences.getFirst().offset;
                        return this.allocate(size);
                    }
                    if (offset + length <= end) continue;
                    end = offset + length;
                }
                this.writePointer = end;
                this.writeRegionSize = this.size - this.writePointer;
                return this.allocate(size);
            }
            this.writePointer = 0L;
            this.writeRegionSize = this.fences.getFirst().offset;
        }
        return this.allocate(size);
    }

    @Override
    public void copy(int buffer, long writeOffset) {
        if (this.flushRegions.isEmpty()) {
            return;
        }
        long writeRegionOffset = 0L;
        long offset = this.flushRegions.getLong(0);
        long length = this.flushRegions.getLong(1);
        for (int i = 2; i < this.flushRegions.size(); i += 2) {
            long regionOffset = this.flushRegions.getLong(i);
            long regionLength = this.flushRegions.getLong(i + 1);
            if (offset + length == regionOffset) {
                length += regionLength;
                continue;
            }
            GL45C.glFlushMappedNamedBufferRange((int)this.buffer, (long)offset, (long)length);
            GL45C.glCopyNamedBufferSubData((int)this.buffer, (int)buffer, (long)offset, (long)(writeRegionOffset + writeOffset), (long)length);
            this.fences.add(new FencedArea(GL45C.glFenceSync((int)37143, (int)0), offset, length));
            writeRegionOffset += length;
            offset = regionOffset;
            length = regionLength;
        }
        GL45C.glFlushMappedNamedBufferRange((int)this.buffer, (long)offset, (long)length);
        GL45C.glCopyNamedBufferSubData((int)this.buffer, (int)buffer, (long)offset, (long)(writeRegionOffset + writeOffset), (long)length);
        this.fences.add(new FencedArea(GL45C.glFenceSync((int)37143, (int)0), offset, length));
        this.flushRegions.clear();
        Collections.sort(this.fences);
    }

    @Override
    public long getSize() {
        return this.size;
    }

    @Override
    public long getUsedSize() {
        return this.writePointer;
    }

    public void free() {
        GL45C.glUnmapNamedBuffer((int)this.buffer);
        GL45C.glDeleteBuffers((int)this.buffer);
    }

    private record FencedArea(long fence, long offset, long length) implements Comparable<FencedArea>
    {
        @Override
        public int compareTo(@NotNull FencedArea o) {
            return Long.compare(this.offset, o.offset);
        }
    }
}
