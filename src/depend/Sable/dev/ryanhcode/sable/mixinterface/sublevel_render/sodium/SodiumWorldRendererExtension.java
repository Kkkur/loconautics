/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.mixinterface.sublevel_render.sodium;

import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.render.sodium.SubLevelRenderSectionManager;
import org.jetbrains.annotations.Nullable;

public interface SodiumWorldRendererExtension {
    @Nullable
    public SubLevelRenderSectionManager sable$getSubLevelRenderSectionManager(ClientSubLevel var1);

    public void sable$freeRenderSectionManager(ClientSubLevel var1);
}
