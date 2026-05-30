/*
 * Decompiled with CFR 0.152.
 */
package dev.ryanhcode.sable.api.sublevel;

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;

public interface SubLevelObserver {
    default public void onSubLevelAdded(SubLevel subLevel) {
    }

    default public void onSubLevelRemoved(SubLevel subLevel, SubLevelRemovalReason reason) {
    }

    default public void tick(SubLevelContainer subLevels) {
    }
}
