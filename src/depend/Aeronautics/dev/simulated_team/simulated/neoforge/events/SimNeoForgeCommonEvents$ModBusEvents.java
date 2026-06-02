/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.config.ConfigBase
 *  net.minecraft.core.Direction
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.data.DataGenerator
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.bus.api.EventPriority
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.fml.event.config.ModConfigEvent$Loading
 *  net.neoforged.fml.event.config.ModConfigEvent$Reloading
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  net.neoforged.neoforge.data.event.GatherDataEvent
 *  net.neoforged.neoforge.event.ModifyDefaultComponentsEvent
 *  net.neoforged.neoforge.registries.RegisterEvent
 */
package dev.simulated_team.simulated.neoforge.events;

import dev.simulated_team.simulated.data.advancements.SimAdvancementTriggers;
import dev.simulated_team.simulated.data.advancements.SimAdvancements;
import dev.simulated_team.simulated.data.neoforge.SimProcessingRecipeGen;
import dev.simulated_team.simulated.events.SimulatedCommonEvents;
import dev.simulated_team.simulated.index.SimArmInteractions;
import dev.simulated_team.simulated.index.SimSoundEvents;
import dev.simulated_team.simulated.index.SimTags;
import dev.simulated_team.simulated.multiloader.inventory.AbstractContainer;
import dev.simulated_team.simulated.multiloader.inventory.neoforge.ContainerWrapper;
import dev.simulated_team.simulated.multiloader.tanks.SingleTank;
import dev.simulated_team.simulated.multiloader.tanks.neoforge.SingleTankWrapper;
import dev.simulated_team.simulated.neoforge.service.NeoForgeSimConfigService;
import dev.simulated_team.simulated.neoforge.service.NeoForgeSimInventoryService;
import java.util.concurrent.CompletableFuture;
import net.createmod.catnip.config.ConfigBase;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid="simulated")
public static class SimNeoForgeCommonEvents.ModBusEvents {
    @SubscribeEvent
    public static void modifyDefaultComponents(ModifyDefaultComponentsEvent event) {
        SimulatedCommonEvents.modifyDefaultComponents((arg_0, arg_1) -> ((ModifyDefaultComponentsEvent)event).modify(arg_0, arg_1));
    }

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        SimArmInteractions.init();
        if (event.getRegistry() == BuiltInRegistries.TRIGGER_TYPES) {
            SimAdvancements.register();
            SimAdvancementTriggers.register();
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void gatherDataHighPriority(GatherDataEvent event) {
        if (event.getMods().contains("simulated")) {
            SimTags.addGenerators();
        }
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture lookupProvider = event.getLookupProvider();
        if (event.includeClient()) {
            event.addProvider((DataProvider)SimSoundEvents.REGISTRY.getProvider(output));
        }
        generator.addProvider(event.includeServer(), (DataProvider)new SimAdvancements(output, lookupProvider));
        generator.addProvider(event.includeServer(), SimProcessingRecipeGen.registerAll(output, lookupProvider));
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (NeoForgeSimInventoryService.InventoryGetterHolder<? extends BlockEntity> inventoryGetterHolder : NeoForgeSimInventoryService.inventoryGetters) {
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, inventoryGetterHolder.type(), (be, dir) -> {
                AbstractContainer container = getter.castBlockEntityAndGetInv((BlockEntity)be, (Direction)dir);
                if (container == null) {
                    return null;
                }
                return new ContainerWrapper<AbstractContainer>(container);
            });
        }
        for (NeoForgeSimInventoryService.TankGetterHolder tankGetterHolder : NeoForgeSimInventoryService.fluidTankGetters) {
            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, tankGetterHolder.type(), (be, dir) -> {
                SingleTank container = tankGetterHolder.castBlockEntityAndGetInv((BlockEntity)be, (Direction)dir);
                if (container == null) {
                    return null;
                }
                return new SingleTankWrapper(container);
            });
        }
    }

    @SubscribeEvent
    public static void loadConfig(ModConfigEvent.Loading event) {
        for (ConfigBase config : NeoForgeSimConfigService.CONFIGS.values()) {
            if (config.specification != event.getConfig().getSpec()) continue;
            config.onLoad();
        }
    }

    @SubscribeEvent
    public static void reloadConfig(ModConfigEvent.Reloading event) {
        for (ConfigBase config : NeoForgeSimConfigService.CONFIGS.values()) {
            if (config.specification != event.getConfig().getSpec()) continue;
            config.onReload();
        }
    }
}
