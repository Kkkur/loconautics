/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.system.NativeResource
 */
package dev.ryanhcode.sable.sublevel.render.fancy;

import dev.ryanhcode.sable.sublevel.render.fancy.BucketRenderBuffer;
import java.nio.IntBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;

public static class BucketRenderBuffer.Slice
implements NativeResource {
    private final BucketRenderBuffer renderBuffer;
    private final int offset;
    private final int length;
    private boolean closed;

    private BucketRenderBuffer.Slice(BucketRenderBuffer renderBuffer, int offset, int length) {
        this.renderBuffer = renderBuffer;
        this.offset = offset;
        this.length = length;
    }

    public long write() {
        return this.renderBuffer.stagingBuffer.reserve((long)this.length * 8L);
    }

    public IntBuffer writeInt() {
        long pointer = this.write();
        if ((pointer & 3L) == 0L) {
            return MemoryUtil.memIntBuffer((long)pointer, (int)(this.length * 8 / 4));
        }
        return MemoryUtil.memByteBuffer((long)pointer, (int)(this.length * 8)).asIntBuffer();
    }

    public void flush() {
        this.renderBuffer.stagingBuffer.copy(this.renderBuffer.buffer, (long)this.offset * 8L);
    }

    public int offset() {
        return this.offset;
    }

    public int length() {
        return this.length;
    }

    public void free() {
        this.renderBuffer.free(this);
    }
}
