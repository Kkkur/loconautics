/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 */
package com.simibubi.create.api.contraption.storage;

import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.BlockPos;

public interface SyncedMountedStorage {
    public boolean isDirty();

    public void markClean();

    public void afterSync(Contraption var1, BlockPos var2);
}
