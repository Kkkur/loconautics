/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.packs.PackType
 *  net.minecraft.server.packs.repository.Pack$Position
 *  net.minecraft.server.packs.repository.RepositorySource
 *  net.minecraft.world.level.saveddata.maps.MapDecorationType
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  net.neoforged.neoforge.client.gui.map.IMapDecorationRenderer
 *  net.neoforged.neoforge.client.gui.map.RegisterMapDecorationRenderersEvent
 *  net.neoforged.neoforge.event.AddPackFindersEvent
 */
package com.simibubi.create.foundation.events;

import com.simibubi.create.AllMapDecorationTypes;
import com.simibubi.create.content.contraptions.actors.psi.PortableFluidInterfaceBlockEntity;
import com.simibubi.create.content.contraptions.actors.psi.PortableItemInterfaceBlockEntity;
import com.simibubi.create.content.contraptions.chassis.StickerBlockEntity;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.fluids.drain.ItemDrainBlockEntity;
import com.simibubi.create.content.fluids.hosePulley.HosePulleyBlockEntity;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.content.fluids.tank.CreativeFluidTankBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlockEntity;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
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
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.simibubi.create.content.trains.observer.TrackObserverBlockEntity;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.foundation.data.RuntimeDataGenerator;
import com.simibubi.create.foundation.map.StationMapDecorationRenderer;
import com.simibubi.create.foundation.pack.DynamicPack;
import com.simibubi.create.foundation.pack.DynamicPackSource;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.gui.map.IMapDecorationRenderer;
import net.neoforged.neoforge.client.gui.map.RegisterMapDecorationRenderersEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@EventBusSubscriber
public static class CommonEvents.ModBusEvents {
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
