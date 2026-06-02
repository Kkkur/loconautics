/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.fml.CrashReportCallables
 *  net.neoforged.fml.ModContainer
 *  net.neoforged.fml.common.Mod
 *  net.neoforged.fml.config.IConfigSpec
 *  net.neoforged.fml.config.ModConfig$Type
 *  net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
 *  net.neoforged.neoforge.common.NeoForge
 *  net.neoforged.neoforge.event.AddReloadListenerEvent
 *  net.neoforged.neoforge.event.OnDatapackSyncEvent
 *  net.neoforged.neoforge.event.RegisterCommandsEvent
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package dev.ryanhcode.sable.neoforge;

import com.mojang.brigadier.CommandDispatcher;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.SableCommonEvents;
import dev.ryanhcode.sable.SableConfig;
import dev.ryanhcode.sable.command.SableCommand;
import dev.ryanhcode.sable.command.argument.SubLevelSelectorModifiers;
import dev.ryanhcode.sable.index.SableAttributes;
import dev.ryanhcode.sable.physics.config.FloatingBlockMaterialDataHandler;
import dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertiesDefinitionLoader;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.CrashReportCallables;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(value="sable")
public final class SableNeoForge {
    public SableNeoForge(ModContainer modContainer, IEventBus modBus) {
        Sable.init();
        IEventBus neoBus = NeoForge.EVENT_BUS;
        neoBus.addListener(this::registerCommand);
        neoBus.addListener(this::registerReloadListeners);
        modBus.addListener(this::serverSetup);
        neoBus.addListener(this::syncDataPack);
        SubLevelSelectorModifiers.registerModifiers();
        DeferredRegister attributes = DeferredRegister.create((Registry)BuiltInRegistries.ATTRIBUTE, (String)"sable");
        SableAttributes.PUNCH_STRENGTH = attributes.register("player.sub_level_punch_strength", () -> SableAttributes.PUNCH_STRENGTH_ATTRIBUTE);
        SableAttributes.PUNCH_COOLDOWN = attributes.register("player.sub_level_punch_cooldown", () -> SableAttributes.PUNCH_COOLDOWN_ATTRIBUTE);
        attributes.register(modBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, (IConfigSpec)SableConfig.SPEC);
        CrashReportCallables.registerHeader(Sable::getCrashHeader);
    }

    public void registerReloadListeners(AddReloadListenerEvent event) {
        event.addListener((PreparableReloadListener)PhysicsBlockPropertiesDefinitionLoader.INSTANCE);
        event.addListener((PreparableReloadListener)DimensionPhysicsData.ReloadListener.INSTANCE);
        event.addListener((PreparableReloadListener)FloatingBlockMaterialDataHandler.ReloadListener.INSTANCE);
    }

    private void serverSetup(FMLCommonSetupEvent event) {
        SableAttributes.register();
    }

    private void registerCommand(RegisterCommandsEvent event) {
        SableCommand.register((CommandDispatcher<CommandSourceStack>)event.getDispatcher(), event.getBuildContext());
    }

    private void syncDataPack(OnDatapackSyncEvent event) {
        SableCommonEvents.syncDataPacket(packet -> event.getRelevantPlayers().forEach(player -> player.connection.send(packet)));
    }
}
