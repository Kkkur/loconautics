/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  net.minecraft.world.level.ChunkPos
 */
package dev.ryanhcode.sable.api.physics.object;

import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.sublevel.storage.holding.SubLevelHoldingChunkMap;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.world.level.ChunkPos;

public interface ArbitraryPhysicsObject {
    public void getBoundingBox(BoundingBox3d var1);

    public void onUnloaded(SubLevelHoldingChunkMap var1, ChunkPos var2);

    public void onRemoved();

    public void onAddition(SubLevelPhysicsSystem var1);

    public void wakeUp();
}
