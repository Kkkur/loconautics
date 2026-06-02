/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.fml.ModContainer
 *  net.neoforged.fml.ModList
 *  net.neoforged.fml.ModLoadingContext
 *  net.neoforged.fml.common.Mod
 *  net.neoforged.neoforge.common.NeoForge
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package dev.simulated_team.simulated.neoforge;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.index.neoforge.SimNeoForgeRecipeTypes;
import dev.simulated_team.simulated.index.neoforge.SimParticleTypesImpl;
import dev.simulated_team.simulated.neoforge.events.SimNeoForgeCommonEvents;
import dev.simulated_team.simulated.neoforge.service.NeoForgeSimConfigService;
import dev.simulated_team.simulated.neoforge.service.NeoForgeSimEntityDataSerialization;
import dev.simulated_team.simulated.neoforge.service.compat.NeoForgeSimPeripheralService;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(value="simulated")
public final class SimulatedNeoForge {
    public static final CreativeModeTab TAB = CreativeModeTab.builder().title((Component)Component.translatable((String)"itemGroup.simulated.group")).icon(() -> new ItemStack((ItemLike)SimBlocks.PHYSICS_ASSEMBLER.get())).build();

    public SimulatedNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        DeferredRegister tabRegister = DeferredRegister.create((Registry)BuiltInRegistries.CREATIVE_MODE_TAB, (String)"simulated");
        tabRegister.register("main_tab", () -> TAB);
        tabRegister.register(modEventBus);
        NeoForge.EVENT_BUS.register(SimNeoForgeCommonEvents.class);
        modEventBus.register(SimNeoForgeCommonEvents.ModBusEvents.class);
        SimParticleTypesImpl.register(modEventBus);
        SimNeoForgeRecipeTypes.register(modEventBus);
        NeoForgeSimEntityDataSerialization.register(modEventBus);
        Simulated.getRegistrate().registerEventListeners(modEventBus);
        if (ModList.get().isLoaded("computercraft")) {
            modEventBus.register(NeoForgeSimPeripheralService.class);
        }
        Simulated.init();
        NeoForgeSimConfigService.register(ModLoadingContext.get(), modContainer);
    }
}
