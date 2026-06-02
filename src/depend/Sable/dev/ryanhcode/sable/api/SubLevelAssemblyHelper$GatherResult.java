/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  net.minecraft.core.BlockPos
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.api;

import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import java.util.Set;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public record SubLevelAssemblyHelper.GatherResult(@Nullable Set<BlockPos> blocks, int checkedBlocks, @Nullable BoundingBox3i boundingBox, State assemblyState) {

    public static enum State {
        SUCCESS("commands.sable.sub_level.assemble.connected.success"),
        TOO_MANY_BLOCKS("commands.sable.sub_level.assemble.connected.too_many_blocks"),
        NO_BLOCKS("commands.sable.sub_level.assemble.no_blocks");

        public final String errorKey;

        private State(String errorKey) {
            this.errorKey = errorKey;
        }
    }
}
