/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.chunk.LevelChunk
 */
package dev.ryanhcode.sable.platform;

import dev.ryanhcode.sable.platform.SablePlatformUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.LevelChunk;

public interface SablePlotPlatform {
    public static final SablePlotPlatform INSTANCE = SablePlatformUtil.load(SablePlotPlatform.class);

    public void readLightData(CompoundTag var1, RegistryAccess var2, LevelChunk var3);

    public void readChunkAttachments(CompoundTag var1, RegistryAccess var2, LevelChunk var3);

    public void postLoad(CompoundTag var1, LevelChunk var2);

    public void writeLightData(CompoundTag var1, RegistryAccess var2, LevelChunk var3);

    public void writeChunkAttachments(CompoundTag var1, RegistryAccess var2, LevelChunk var3);
}
