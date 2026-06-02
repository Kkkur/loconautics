/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.client.render.VeilRenderSystem
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.opengl.GLCapabilities
 *  org.lwjgl.system.NativeResource
 */
package dev.ryanhcode.sable.sublevel.render.staging;

import dev.ryanhcode.sable.sublevel.render.staging.DSAStagingBuffer;
import foundry.veil.api.client.render.VeilRenderSystem;
import java.util.function.LongFunction;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.NativeResource;

public abstract class StagingBuffer
implements NativeResource {
    private static StagingBufferType stagingBufferType;

    public static StagingBuffer create() {
        return StagingBuffer.create(0x1000000L);
    }

    public static StagingBuffer create(long size) {
        if (stagingBufferType == null) {
            GLCapabilities caps = GL.getCapabilities();
            stagingBufferType = caps.OpenGL44 || caps.GL_ARB_buffer_storage ? (VeilRenderSystem.directStateAccessSupported() ? StagingBufferType.DSA : StagingBufferType.ARB) : StagingBufferType.LEGACY;
        }
        return StagingBuffer.stagingBufferType.factory.apply(size);
    }

    public abstract void updateFencedAreas();

    public abstract long reserve(long var1);

    public abstract void copy(int var1, long var2);

    public abstract long getSize();

    public abstract long getUsedSize();

    private static enum StagingBufferType {
        LEGACY(DSAStagingBuffer::new),
        ARB(DSAStagingBuffer::new),
        DSA(DSAStagingBuffer::new);

        private final LongFunction<StagingBuffer> factory;

        private StagingBufferType(LongFunction<StagingBuffer> factory) {
            this.factory = factory;
        }
    }
}
