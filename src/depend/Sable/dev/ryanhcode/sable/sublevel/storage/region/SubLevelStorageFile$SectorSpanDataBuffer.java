/*
 * Decompiled with CFR 0.152.
 */
package dev.ryanhcode.sable.sublevel.storage.region;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

class SubLevelStorageFile.SectorSpanDataBuffer
extends ByteArrayOutputStream {
    private final int subLevelIndex;

    public SubLevelStorageFile.SectorSpanDataBuffer(int subLevelIndex) {
        super(SubLevelStorageFile.this.sectorSize);
        super.write(0);
        super.write(0);
        super.write(0);
        super.write(0);
        super.write(0);
        this.subLevelIndex = subLevelIndex;
    }

    @Override
    public void close() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(this.buf, 0, this.count);
        int start = this.count - 4;
        byteBuffer.putInt(0, start);
        SubLevelStorageFile.this.write(this.subLevelIndex, byteBuffer);
    }
}
