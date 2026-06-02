/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3ic
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 */
package dev.ryanhcode.sable.sublevel.plot.heat;

import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import java.util.Collection;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

@FunctionalInterface
public static interface SubLevelHeatMapManager.SplitListener {
    public void addBlocks(Level var1, BoundingBox3ic var2, Collection<BlockPos> var3);
}
