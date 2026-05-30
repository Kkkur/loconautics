package com.lycoris.loconautics.registry;

import com.lycoris.loconautics.core.LoconauticsConstants;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Holds all DeferredRegisters for the mod. Currently a stub: the addon has no blocks/items yet,
 * but keeping the registers here means the main mod class stays clean and adding content later
 * is a one-liner.
 */
public final class LoconauticsRegistries {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(LoconauticsConstants.MOD_ID);

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(LoconauticsConstants.MOD_ID);

    private LoconauticsRegistries() {
    }

    /** Registers every DeferredRegister onto the mod event bus. Call once from the mod constructor. */
    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
    }
}
