/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  net.createmod.catnip.config.ConfigBase
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.core.Direction
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.data.DataGenerator
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.bus.api.EventPriority
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.fml.event.config.ModConfigEvent$Loading
 *  net.neoforged.fml.event.config.ModConfigEvent$Reloading
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  net.neoforged.neoforge.client.event.InputEvent$InteractionKeyMappingTriggered
 *  net.neoforged.neoforge.data.event.GatherDataEvent
 *  net.neoforged.neoforge.event.AddReloadListenerEvent
 *  net.neoforged.neoforge.event.ModifyDefaultComponentsEvent
 *  net.neoforged.neoforge.event.OnDatapackSyncEvent
 *  net.neoforged.neoforge.event.RegisterCommandsEvent
 *  net.neoforged.neoforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickItem
 *  net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent
 *  net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent$UsePhase
 *  net.neoforged.neoforge.event.level.ChunkEvent$Load
 *  net.neoforged.neoforge.event.server.ServerStoppedEvent
 *  net.neoforged.neoforge.event.tick.ServerTickEvent$Post
 *  net.neoforged.neoforge.registries.RegisterEvent
 */
package dev.simulated_team.simulated.neoforge.events;

import com.mojang.brigadier.CommandDispatcher;
import dev.simulated_team.simulated.command.SimCommand;
import dev.simulated_team.simulated.content.end_sea.EndSeaPhysicsData;
import dev.simulated_team.simulated.data.advancements.SimAdvancementTriggers;
import dev.simulated_team.simulated.data.advancements.SimAdvancements;
import dev.simulated_team.simulated.data.neoforge.SimProcessingRecipeGen;
import dev.simulated_team.simulated.events.SimulatedCommonClientEvents;
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
import dev.simulated_team.simulated.util.hold_interaction.HoldInteractionManager;
import java.util.concurrent.CompletableFuture;
import net.createmod.catnip.config.ConfigBase;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid="simulated")
public class SimNeoForgeCommonEvents {
    @SubscribeEvent
    public static void loadChunk(ChunkEvent.Load event) {
        SimulatedCommonEvents.onChunkLoad(event.getLevel(), event.getChunk(), event.isNewChunk());
    }

    @SubscribeEvent
    public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        SimulatedCommonEvents.onPlayerLoggedIn(player);
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        SimCommand.register((CommandDispatcher<CommandSourceStack>)event.getDispatcher(), event.getBuildContext());
    }

    @SubscribeEvent
    public static void serverStopped(ServerStoppedEvent event) {
        SimulatedCommonEvents.onServerStopped(event.getServer());
    }

    @SubscribeEvent
    public static void postServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        for (ServerLevel level : server.getAllLevels()) {
            SimulatedCommonEvents.onServerTickEnd(level);
        }
    }

    @SubscribeEvent
    public static void syncDataPack(OnDatapackSyncEvent event) {
        EndSeaPhysicsData.syncDataPacket(packet -> event.getRelevantPlayers().forEach(player -> player.connection.send(packet)));
    }

    @SubscribeEvent
    public static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener((PreparableReloadListener)EndSeaPhysicsData.ReloadListener.INSTANCE);
    }

    @SubscribeEvent
    public static void keyInput(InputEvent.InteractionKeyMappingTriggered event) {
        if (event.isUseItem() && SimulatedCommonClientEvents.useItemMappingTriggered()) {
            event.setCanceled(true);
            event.setSwingHand(false);
        }
    }

    @SubscribeEvent
    public static void useItemOnBlock(UseItemOnBlockEvent event) {
        if (event.getLevel().isClientSide()) {
            if (event.getPlayer() != null && event.getUsePhase() == UseItemOnBlockEvent.UsePhase.ITEM_AFTER_BLOCK && SimulatedCommonClientEvents.useItemOnBlockEvent(event.getLevel(), event.getPlayer(), event.getItemStack(), event.getHand())) {
                event.cancelWithResult(ItemInteractionResult.CONSUME);
            }
            SimNeoForgeCommonEvents.useItemOnBlockClient(event);
        }
    }

    @SubscribeEvent
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = SimulatedCommonEvents.rightClickBlock(event.getLevel(), event.getPos(), event.getEntity(), event.getItemStack());
        if (result != null) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingEntityUseItem(PlayerInteractEvent.RightClickItem event) {
        Player player;
        Player entity = event.getEntity();
        if (entity instanceof Player && (player = entity).isLocalPlayer()) {
            SimulatedCommonClientEvents.useItemOnAirEvent(entity.level(), player, event.getItemStack(), event.getHand());
        }
    }

    private static void useItemOnBlockClient(UseItemOnBlockEvent event) {
        if (event.getPlayer().isLocalPlayer() && HoldInteractionManager.isActive()) {
            event.setCanceled(true);
        }
    }

    @EventBusSubscriber(modid="simulated")
    public static class ModBusEvents {
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
}
