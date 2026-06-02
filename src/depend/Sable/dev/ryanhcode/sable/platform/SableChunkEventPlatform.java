/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.chunk.LevelChunk
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package dev.ryanhcode.sable.platform;

import dev.ryanhcode.sable.platform.SablePlatformUtil;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface SableChunkEventPlatform {
    public static final SableChunkEventPlatform INSTANCE = SablePlatformUtil.load(SableChunkEventPlatform.class);

    public void onChunkPacketReplaced(LevelChunk var1);

    public void onOldChunkInvalid(LevelChunk var1);
}
