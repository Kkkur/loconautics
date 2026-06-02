/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  net.createmod.catnip.data.WorldAttached
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.packs.PackType
 *  net.minecraft.server.packs.repository.Pack$Position
 *  net.minecraft.server.packs.repository.RepositorySource
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.saveddata.maps.MapDecorationType
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  net.neoforged.neoforge.client.gui.map.IMapDecorationRenderer
 *  net.neoforged.neoforge.client.gui.map.RegisterMapDecorationRenderersEvent
 *  net.neoforged.neoforge.event.AddPackFindersEvent
 *  net.neoforged.neoforge.event.AddReloadListenerEvent
 *  net.neoforged.neoforge.event.RegisterCommandsEvent
 *  net.neoforged.neoforge.event.entity.EntityEvent$EnteringSection
 *  net.neoforged.neoforge.event.entity.EntityJoinLevelEvent
 *  net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent
 *  net.neoforged.neoforge.event.entity.player.AttackEntityEvent
 *  net.neoforged.neoforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.neoforged.neoforge.event.entity.player.PlayerEvent$PlayerLoggedOutEvent
 *  net.neoforged.neoforge.event.entity.player.PlayerEvent$StartTracking
 *  net.neoforged.neoforge.event.level.ChunkEvent$Unload
 *  net.neoforged.neoforge.event.level.LevelEvent$Load
 *  net.neoforged.neoforge.event.level.LevelEvent$Unload
 *  net.neoforged.neoforge.event.server.ServerStoppingEvent
 *  net.neoforged.neoforge.event.tick.EntityTickEvent
 *  net.neoforged.neoforge.event.tick.EntityTickEvent$Pre
 *  net.neoforged.neoforge.event.tick.LevelTickEvent$Post
 *  net.neoforged.neoforge.event.tick.ServerTickEvent
 *  net.neoforged.neoforge.event.tick.ServerTickEvent$Post
 */
package com.simibubi.create.foundation.events;

import com.mojang.brigadier.CommandDispatcher;
import com.simibubi.create.AllMapDecorationTypes;
import com.simibubi.create.Create;
import com.simibubi.create.compat.trainmap.TrainMapSync;
import com.simibubi.create.content.contraptions.ContraptionHandler;
import com.simibubi.create.content.contraptions.actors.psi.PortableFluidInterfaceBlockEntity;
import com.simibubi.create.content.contraptions.actors.psi.PortableItemInterfaceBlockEntity;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsServerHandler;
import com.simibubi.create.content.contraptions.chassis.StickerBlockEntity;
import com.simibubi.create.content.contraptions.minecart.CouplingPhysics;
import com.simibubi.create.content.contraptions.minecart.capability.CapabilityMinecartController;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.equipment.toolbox.ToolboxHandler;
import com.simibubi.create.content.equipment.wrench.WrenchItem;
import com.simibubi.create.content.equipment.zapper.ZapperInteractionHandler;
import com.simibubi.create.content.equipment.zapper.ZapperItem;
import com.simibubi.create.content.fluids.drain.ItemDrainBlockEntity;
import com.simibubi.create.content.fluids.hosePulley.HosePulleyBlockEntity;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.content.fluids.tank.CreativeFluidTankBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.chainConveyor.ServerChainConveyorHandler;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlockEntity;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.kinetics.drill.CobbleGenOptimisation;
import com.simibubi.create.content.kinetics.gauge.SpeedGaugeBlockEntity;
import com.simibubi.create.content.kinetics.gauge.StressGaugeBlockEntity;
import com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity;
import com.simibubi.create.content.kinetics.motor.CreativeMotorBlockEntity;
import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import com.simibubi.create.content.kinetics.speedController.SpeedControllerBlockEntity;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlockEntity;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.content.logistics.chute.SmartChuteBlockEntity;
import com.simibubi.create.content.logistics.crate.CreativeCrateBlockEntity;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.content.logistics.depot.EjectorBlockEntity;
import com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlockEntity;
import com.simibubi.create.content.logistics.packagePort.postbox.PostboxBlockEntity;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packager.repackager.RepackagerBlockEntity;
import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterBlockEntity;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.content.logistics.tableCloth.TableClothBlockEntity;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelBlockEntity;
import com.simibubi.create.content.logistics.tunnel.BrassTunnelBlockEntity;
import com.simibubi.create.content.logistics.vault.ItemVaultBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerServerHandler;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.simibubi.create.content.trains.entity.CarriageEntityHandler;
import com.simibubi.create.content.trains.observer.TrackObserverBlockEntity;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.foundation.data.RuntimeDataGenerator;
import com.simibubi.create.foundation.map.StationMapDecorationRenderer;
import com.simibubi.create.foundation.pack.DynamicPack;
import com.simibubi.create.foundation.pack.DynamicPackSource;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.recipe.trie.RecipeTrieFinder;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import com.simibubi.create.foundation.utility.TickBasedCache;
import com.simibubi.create.infrastructure.command.AllCommands;
import net.createmod.catnip.data.WorldAttached;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.gui.map.IMapDecorationRenderer;
import net.neoforged.neoforge.client.gui.map.RegisterMapDecorationRenderersEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber
public class CommonEvents {
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        Create.SCHEMATIC_RECEIVER.tick();
        Create.LAGGER.tick();
        ServerSpeedProvider.serverTick();
        Create.RAILWAYS.sync.serverTick();
        TrainMapSync.serverTick((ServerTickEvent)event);
        ServerChainConveyorHandler.tick();
        TickBasedCache.tick();
    }

    @SubscribeEvent
    public static void onChunkUnloaded(ChunkEvent.Unload event) {
        CapabilityMinecartController.onChunkUnloaded(event);
    }

    @SubscribeEvent
    public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        ToolboxHandler.playerLogin(player);
        Create.RAILWAYS.playerLogin(player);
    }

    @SubscribeEvent
    public static void playerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        Create.RAILWAYS.playerLogout(player);
    }

    @SubscribeEvent
    public static void onServerWorldTick(LevelTickEvent.Post event) {
        Level world = event.getLevel();
        if (world.isClientSide()) {
            return;
        }
        ContraptionHandler.tick(world);
        CapabilityMinecartController.tick(world);
        CouplingPhysics.tick(world);
        LinkedControllerServerHandler.tick((LevelAccessor)world);
        ControlsServerHandler.tick((LevelAccessor)world);
        Create.RAILWAYS.tick(world);
        Create.LOGISTICS.tick(world);
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Pre event) {
        CapabilityMinecartController.entityTick((EntityTickEvent)event);
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            Level level = livingEntity.level();
            ContraptionHandler.entitiesWhoJustDismountedGetSentToTheRightLocation(livingEntity, level);
            ToolboxHandler.entityTick((Entity)livingEntity, level);
        }
    }

    @SubscribeEvent
    public static void onEntityAdded(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        Level world = event.getLevel();
        ContraptionHandler.addSpawnedContraptionsToCollisionList(entity, world);
    }

    @SubscribeEvent
    public static void onEntityAttackedByPlayer(AttackEntityEvent event) {
        WrenchItem.wrenchInstaKillsMinecarts(event);
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        AllCommands.register((CommandDispatcher<CommandSourceStack>)event.getDispatcher());
    }

    @SubscribeEvent
    public static void onEntityEnterSection(EntityEvent.EnteringSection event) {
        CarriageEntityHandler.onEntityEnterSection(event);
    }

    @SubscribeEvent
    public static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener((PreparableReloadListener)RecipeFinder.LISTENER);
        event.addListener((PreparableReloadListener)RecipeTrieFinder.LISTENER);
        event.addListener((PreparableReloadListener)BeltHelper.LISTENER);
    }

    @SubscribeEvent
    public static void serverStopping(ServerStoppingEvent event) {
        Create.SCHEMATIC_RECEIVER.shutdown();
    }

    @SubscribeEvent
    public static void onLoadWorld(LevelEvent.Load event) {
        LevelAccessor world = event.getLevel();
        Create.REDSTONE_LINK_NETWORK_HANDLER.onLoadWorld(world);
        Create.TORQUE_PROPAGATOR.onLoadWorld(world);
        Create.RAILWAYS.levelLoaded(world);
        Create.LOGISTICS.levelLoaded(world);
    }

    @SubscribeEvent
    public static void onUnloadWorld(LevelEvent.Unload event) {
        LevelAccessor world = event.getLevel();
        Create.REDSTONE_LINK_NETWORK_HANDLER.onUnloadWorld(world);
        Create.TORQUE_PROPAGATOR.onUnloadWorld(world);
        WorldAttached.invalidateWorld((LevelAccessor)world);
        CobbleGenOptimisation.invalidateWorld(world);
    }

    @SubscribeEvent
    public static void attachData(EntityJoinLevelEvent event) {
        CapabilityMinecartController.attach(event);
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (!event.getEntity().isAlive()) {
            CapabilityMinecartController.onEntityDeath(event);
        }
    }

    @SubscribeEvent
    public static void startTracking(PlayerEvent.StartTracking event) {
        CapabilityMinecartController.startTracking(event);
    }

    public static void leftClickEmpty(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof ZapperItem) {
            ZapperInteractionHandler.trySelect(stack, (Player)player);
        }
    }

    @EventBusSubscriber
    public static class ModBusEvents {
        @SubscribeEvent
        public static void addPackFinders(AddPackFindersEvent event) {
            if (event.getPackType() == PackType.SERVER_DATA) {
                DynamicPack dynamicPack = new DynamicPack("create:dynamic_data", PackType.SERVER_DATA);
                RuntimeDataGenerator.insertIntoPack(dynamicPack);
                event.addRepositorySource((RepositorySource)new DynamicPackSource("create:dynamic_data", PackType.SERVER_DATA, Pack.Position.BOTTOM, dynamicPack));
            }
        }

        @SubscribeEvent
        public static void onRegisterMapDecorationRenderers(RegisterMapDecorationRenderersEvent event) {
            event.register((MapDecorationType)AllMapDecorationTypes.STATION_MAP_DECORATION.value(), (IMapDecorationRenderer)new StationMapDecorationRenderer());
        }

        @SubscribeEvent
        public static void registerCapabilities(RegisterCapabilitiesEvent event) {
            ChuteBlockEntity.registerCapabilities(event);
            SmartChuteBlockEntity.registerCapabilities(event);
            BeltBlockEntity.registerCapabilities(event);
            BasinBlockEntity.registerCapabilities(event);
            BeltTunnelBlockEntity.registerCapabilities(event);
            BrassTunnelBlockEntity.registerCapabilities(event);
            CreativeCrateBlockEntity.registerCapabilities(event);
            CrushingWheelControllerBlockEntity.registerCapabilities(event);
            ToolboxBlockEntity.registerCapabilities(event);
            DeployerBlockEntity.registerCapabilities(event);
            DepotBlockEntity.registerCapabilities(event);
            PortableFluidInterfaceBlockEntity.registerCapabilities(event);
            SpoutBlockEntity.registerCapabilities(event);
            PortableItemInterfaceBlockEntity.registerCapabilities(event);
            SawBlockEntity.registerCapabilities(event);
            EjectorBlockEntity.registerCapabilities(event);
            FluidTankBlockEntity.registerCapabilities(event);
            CreativeFluidTankBlockEntity.registerCapabilities(event);
            HosePulleyBlockEntity.registerCapabilities(event);
            ItemDrainBlockEntity.registerCapabilities(event);
            ItemVaultBlockEntity.registerCapabilities(event);
            MechanicalCrafterBlockEntity.registerCapabilities(event);
            MillstoneBlockEntity.registerCapabilities(event);
            StressGaugeBlockEntity.registerCapabilities(event);
            SpeedGaugeBlockEntity.registerCapabilities(event);
            StationBlockEntity.registerCapabilities(event);
            SpeedControllerBlockEntity.registerCapabilities(event);
            SequencedGearshiftBlockEntity.registerCapabilities(event);
            DisplayLinkBlockEntity.registerCapabilities(event);
            StockTickerBlockEntity.registerCapabilities(event);
            PackagerBlockEntity.registerCapabilities(event);
            RepackagerBlockEntity.registerCapabilities(event);
            PostboxBlockEntity.registerCapabilities(event);
            FrogportBlockEntity.registerCapabilities(event);
            RedstoneRequesterBlockEntity.registerCapabilities(event);
            TableClothBlockEntity.registerCapabilities(event);
            SignalBlockEntity.registerCapabilities(event);
            CreativeMotorBlockEntity.registerCapabilities(event);
            TrackObserverBlockEntity.registerCapabilities(event);
            NixieTubeBlockEntity.registerCapabilities(event);
            StickerBlockEntity.registerCapabilities(event);
        }
    }
}
