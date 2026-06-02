/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.behaviour.display.DisplaySource
 *  com.tterrag.registrate.util.entry.RegistryEntry
 */
package dev.eriksonn.aeronautics.index;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.eriksonn.aeronautics.Aeronautics;
import dev.eriksonn.aeronautics.content.display_sources.GasDisplaySource;
import java.util.function.Supplier;

public class AeroDisplaySources {
    public static final RegistryEntry<DisplaySource, GasDisplaySource> GAS_DISPLAY = AeroDisplaySources.simple("gas_display", GasDisplaySource::new);

    private static <T extends DisplaySource> RegistryEntry<DisplaySource, T> simple(String name, Supplier<T> supplier) {
        return Aeronautics.getRegistrate().displaySource(name, supplier).register();
    }
}
