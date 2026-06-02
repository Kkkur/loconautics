/*
 * Decompiled with CFR 0.152.
 */
package dev.ryanhcode.sable.mixinterface.toast;

import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.storage.holding.GlobalSavedSubLevelPointer;
import dev.ryanhcode.sable.sublevel.storage.serialization.SubLevelData;

public interface SableToastableServer {
    public void sable$reportSubLevelLoadFailure(GlobalSavedSubLevelPointer var1);

    public void sable$reportSubLevelSaveFailure(SubLevelData var1);

    public void sable$reportSubLevelPhysicsFailure(ServerSubLevel var1);
}
