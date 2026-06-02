/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderType
 *  net.neoforged.neoforge.client.ChunkRenderTypeSet
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package dev.eriksonn.aeronautics.mixin.levitite;

import java.util.BitSet;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={ChunkRenderTypeSet.class})
public interface ChunkRenderTypeSetAccessor {
    @Mutable
    @Accessor(value="CHUNK_RENDER_TYPES_LIST")
    public static void setChunkRenderTypesList(List<RenderType> data) {
        throw new AssertionError((Object)"Something has gone terribly wrong.");
    }

    @Accessor(value="CHUNK_RENDER_TYPES")
    @Mutable
    public static void setChunkRenderTypes(RenderType[] data) {
        throw new AssertionError((Object)"Something has gone terribly wrong.");
    }

    @Accessor(value="bits")
    public BitSet getBits();
}
