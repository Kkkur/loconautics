/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package dev.ryanhcode.sable.sublevel.render.staging;

import org.jetbrains.annotations.NotNull;

private record DSAStagingBuffer.FencedArea(long fence, long offset, long length) implements Comparable<DSAStagingBuffer.FencedArea>
{
    @Override
    public int compareTo(@NotNull DSAStagingBuffer.FencedArea o) {
        return Long.compare(this.offset, o.offset);
    }
}
