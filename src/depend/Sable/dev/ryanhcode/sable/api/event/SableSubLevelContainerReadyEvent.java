/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.Level
 */
package dev.ryanhcode.sable.api.event;

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import net.minecraft.world.level.Level;

@FunctionalInterface
public interface SableSubLevelContainerReadyEvent {
    public void onSubLevelContainerReady(Level var1, SubLevelContainer var2);
}
