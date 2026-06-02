/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2LongMap
 *  it.unimi.dsi.fastutil.longs.Long2LongMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2LongMaps
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Vec3i
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.graph;

import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.graph.BalloonLayerData;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongMaps;
import java.util.Iterator;
import java.util.NoSuchElementException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

private final class BalloonLayerData.LayerBlockIterator
implements Iterator<BlockPos> {
    private final Iterator<Long2LongMap.Entry> chunkIter;
    private final boolean ignoreSolids;
    private Long2LongMap.Entry currentEntry;
    private boolean hasSolidsChunk;
    private long solidsChunkBits;
    private int bitIndex = -1;
    private boolean hasNext = false;
    private final BlockPos.MutableBlockPos nextPos = new BlockPos.MutableBlockPos();
    private final BlockPos.MutableBlockPos resultPos = new BlockPos.MutableBlockPos();

    private BalloonLayerData.LayerBlockIterator(boolean ignoreSolids) {
        this.chunkIter = Long2LongMaps.fastIterator((Long2LongMap)BalloonLayerData.this.chunks);
        this.ignoreSolids = ignoreSolids;
        this.advance();
    }

    private void advance() {
        this.hasNext = false;
        while (true) {
            boolean chunkFull;
            if (this.currentEntry == null && this.chunkIter.hasNext()) {
                this.currentEntry = this.chunkIter.next();
                if (this.ignoreSolids) {
                    this.solidsChunkBits = BalloonLayerData.this.solidChunks.get(this.currentEntry.getLongKey());
                    this.hasSolidsChunk = this.solidsChunkBits != 0L;
                }
                this.bitIndex = -1;
            } else if (this.currentEntry == null) {
                return;
            }
            long chunkBits = this.currentEntry.getLongValue();
            if (this.hasSolidsChunk) {
                chunkBits &= this.solidsChunkBits ^ 0xFFFFFFFFFFFFFFFFL;
            }
            boolean bl = chunkFull = chunkBits == -1L;
            while (++this.bitIndex < 64) {
                if (!chunkFull && (chunkBits >>> this.bitIndex & 1L) == 0L) continue;
                long key = this.currentEntry.getLongKey();
                int chunkX = BalloonLayerData.getChunkX(key);
                int chunkZ = BalloonLayerData.getChunkZ(key);
                int localX = this.bitIndex >> 3;
                int localZ = this.bitIndex & 7;
                int worldX = (chunkX << 3) + localX;
                int worldZ = (chunkZ << 3) + localZ;
                this.nextPos.set(worldX, BalloonLayerData.this.yLevel, worldZ);
                this.hasNext = true;
                return;
            }
            this.currentEntry = null;
        }
    }

    @Override
    public boolean hasNext() {
        return this.hasNext;
    }

    @Override
    public BlockPos next() {
        if (!this.hasNext) {
            throw new NoSuchElementException();
        }
        this.resultPos.set((Vec3i)this.nextPos);
        this.advance();
        return this.resultPos;
    }
}
