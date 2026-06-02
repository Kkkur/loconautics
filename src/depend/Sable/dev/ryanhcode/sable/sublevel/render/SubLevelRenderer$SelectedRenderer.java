/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.compat.SodiumCompat
 */
package dev.ryanhcode.sable.sublevel.render;

import dev.ryanhcode.sable.sublevel.render.dispatcher.ReachAroundSubLevelRenderDispatcher;
import dev.ryanhcode.sable.sublevel.render.dispatcher.SubLevelRenderDispatcher;
import dev.ryanhcode.sable.sublevel.render.dispatcher.VanillaSubLevelRenderDispatcher;
import foundry.veil.api.compat.SodiumCompat;

public static enum SubLevelRenderer.SelectedRenderer {
    VANILLA{

        @Override
        public boolean isSupported() {
            return !SodiumCompat.isLoaded();
        }

        @Override
        public SubLevelRenderDispatcher create() {
            return new VanillaSubLevelRenderDispatcher();
        }
    }
    ,
    SODIUM_REACHAROUND{

        @Override
        public boolean isSupported() {
            return SodiumCompat.isLoaded();
        }

        @Override
        public SubLevelRenderDispatcher create() {
            return new ReachAroundSubLevelRenderDispatcher();
        }
    };


    public abstract boolean isSupported();

    public abstract SubLevelRenderDispatcher create();
}
