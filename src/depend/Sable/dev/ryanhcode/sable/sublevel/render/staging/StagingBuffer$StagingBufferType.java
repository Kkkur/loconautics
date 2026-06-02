/*
 * Decompiled with CFR 0.152.
 */
package dev.ryanhcode.sable.sublevel.render.staging;

import dev.ryanhcode.sable.sublevel.render.staging.DSAStagingBuffer;
import dev.ryanhcode.sable.sublevel.render.staging.StagingBuffer;
import java.util.function.LongFunction;

private static enum StagingBuffer.StagingBufferType {
    LEGACY(DSAStagingBuffer::new),
    ARB(DSAStagingBuffer::new),
    DSA(DSAStagingBuffer::new);

    private final LongFunction<StagingBuffer> factory;

    private StagingBuffer.StagingBufferType(LongFunction<StagingBuffer> factory) {
        this.factory = factory;
    }
}
