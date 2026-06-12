package com.lycoris.loconautics.core;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;

/**
 * Central place for mod-wide constants. Keeps literals out of the rest of the codebase.
 */
public final class LoconauticsConstants {

    private LoconauticsConstants() {
    }

    /** The mod id. Must match the id declared in neoforge.mods.toml. */
    public static final String MOD_ID = "loconautics";

    /** Shared slf4j logger for the whole mod. */
    public static final Logger LOGGER = LogUtils.getLogger();

    /** Networking channel/payload version. Bump when the packet wire format changes. */
    public static final int NETWORK_VERSION = 3;

    /** Builds a ResourceLocation in this mod's namespace. */
    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
