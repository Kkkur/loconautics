/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.compat.SodiumCompat
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 */
package dev.ryanhcode.sable.sublevel.render;

import dev.ryanhcode.sable.api.sublevel.ClientSubLevelContainer;
import dev.ryanhcode.sable.mixinterface.plot.SubLevelContainerHolder;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.render.dispatcher.ReachAroundSubLevelRenderDispatcher;
import dev.ryanhcode.sable.sublevel.render.dispatcher.SubLevelRenderDispatcher;
import dev.ryanhcode.sable.sublevel.render.dispatcher.VanillaSubLevelRenderDispatcher;
import foundry.veil.api.compat.SodiumCompat;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

public final class SubLevelRenderer {
    public static final SelectedRenderer DEFAULT;
    private static SubLevelRenderDispatcher dispatcher;
    private static SelectedRenderer selected;

    public static void setImpl(SelectedRenderer impl) {
        SelectedRenderer newImpl;
        SelectedRenderer selectedRenderer = newImpl = !impl.isSupported() ? DEFAULT : impl;
        if (selected.equals((Object)newImpl)) {
            return;
        }
        selected = newImpl;
        if (dispatcher != null) {
            dispatcher.free();
            dispatcher = null;
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                List<ClientSubLevel> sublevels = ((ClientSubLevelContainer)((SubLevelContainerHolder)level).sable$getPlotContainer()).getAllSubLevels();
                for (ClientSubLevel sublevel : sublevels) {
                    sublevel.updateRenderData();
                }
            }
        }
    }

    public static void free() {
        if (dispatcher != null) {
            dispatcher.free();
            dispatcher = null;
        }
    }

    public static SubLevelRenderDispatcher getDispatcher() {
        if (dispatcher == null) {
            dispatcher = selected.create();
        }
        return dispatcher;
    }

    static {
        SelectedRenderer impl = null;
        for (SelectedRenderer render : SelectedRenderer.values()) {
            if (!render.isSupported()) continue;
            impl = render;
            break;
        }
        if (impl == null) {
            throw new RuntimeException("Failed to find a supported sub-level renderer");
        }
        selected = DEFAULT = impl;
    }

    public static enum SelectedRenderer {
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
}
