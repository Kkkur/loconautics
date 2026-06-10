/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.Mod
 *  net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
 *  net.neoforged.neoforge.event.server.ServerStartingEvent
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 *  org.slf4j.Logger
 */
package com.bearing.linearbearing;

import com.bearing.linearbearing.LinearBearingClient;
import com.bearing.linearbearing.ModBlocks;
import com.bearing.linearbearing.ModComponents;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(value="linearbearing")
public class LinearBearing {
    public static final String MODID = "linearbearing";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create((ResourceKey)Registries.CREATIVE_MODE_TAB, (String)"linearbearing");
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MOD_TAB = CREATIVE_MODE_TABS.register("linear_bearing_tab", () -> CreativeModeTab.builder().title((Component)Component.translatable((String)"itemGroup.linearbearing.tab")).icon(() -> new ItemStack((ItemLike)ModBlocks.LINEAR_BEARING.get().asItem())).displayItems((parameters, output) -> {
        output.accept((ItemLike)ModBlocks.LINEAR_BEARING.get().asItem());
        output.accept((ItemLike)ModBlocks.LINEAR_CASING.get().asItem());
        output.accept((ItemLike)ModBlocks.TORSIONAL_ANCHOR.get().asItem());
        output.accept((ItemLike)ModBlocks.MAGNETIC_PORT.get().asItem());
        output.accept((ItemLike)ModBlocks.ANDESITE_LAMP.get().asItem());
        output.accept((ItemLike)ModBlocks.REDSTONE_CONVERTER.get().asItem());
    }).build());

    public LinearBearing(IEventBus modEventBus) {
        ModBlocks.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ModComponents.register(modEventBus);
        modEventBus.addListener(this::addCreative);
        LinearBearingClient.registerClient(modEventBus);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Linear Bearing mod starting...");
    }
}
