/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.block.render;

import java.util.Set;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface BlockDestructionProgressExtension {
    @Nullable
    public Set<BlockPos> create$getExtraPositions();

    public void create$setExtraPositions(@Nullable Set<BlockPos> var1);
}
