/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.world.level.ChunkPos
 */
package dev.ryanhcode.sable.sublevel.storage.holding;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ryanhcode.sable.sublevel.storage.holding.SavedSubLevelPointer;
import net.minecraft.world.level.ChunkPos;

public record GlobalSavedSubLevelPointer(ChunkPos chunkPos, short storageIndex, short subLevelIndex) {
    public static final Codec<GlobalSavedSubLevelPointer> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("chunk_x").forGetter(x -> x.chunkPos().x), (App)Codec.INT.fieldOf("chunk_z").forGetter(x -> x.chunkPos().z), (App)Codec.SHORT.fieldOf("storage_index").forGetter(GlobalSavedSubLevelPointer::storageIndex), (App)Codec.SHORT.fieldOf("sub_level_index").forGetter(GlobalSavedSubLevelPointer::subLevelIndex)).apply(Applicative.unbox((App)instance), (chunkX, chunkZ, storage, subLevel) -> new GlobalSavedSubLevelPointer(new ChunkPos(chunkX.intValue(), chunkZ.intValue()), (short)storage, (short)subLevel)));

    public SavedSubLevelPointer local() {
        return new SavedSubLevelPointer(this.storageIndex, this.subLevelIndex);
    }

    @Override
    public String toString() {
        return "global->[chunkPos=" + String.valueOf(this.chunkPos) + ", storageIndex=" + this.storageIndex + ", subLevelIndex=" + this.subLevelIndex + "]";
    }
}
