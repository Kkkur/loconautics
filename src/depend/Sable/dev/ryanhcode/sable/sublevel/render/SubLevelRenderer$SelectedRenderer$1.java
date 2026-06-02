/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.compat.SodiumCompat
 */
package dev.ryanhcode.sable.sublevel.render;

import dev.ryanhcode.sable.sublevel.render.SubLevelRenderer;
import dev.ryanhcode.sable.sublevel.render.dispatcher.SubLevelRenderDispatcher;
import dev.ryanhcode.sable.sublevel.render.dispatcher.VanillaSubLevelRenderDispatcher;
import foundry.veil.api.compat.SodiumCompat;

final class SubLevelRenderer.SelectedRenderer.1
extends SubLevelRenderer.SelectedRenderer {
    @Override
    public boolean isSupported() {
        return !SodiumCompat.isLoaded();
    }

    @Override
    public SubLevelRenderDispatcher create() {
        return new VanillaSubLevelRenderDispatcher();
    }
}
