/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DestFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SourceFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.createmod.catnip.config.ui.BaseConfigScreen
 *  net.createmod.catnip.config.ui.ConfigScreen
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBufferCache
 *  net.createmod.ponder.api.registration.PonderPlugin
 *  net.createmod.ponder.foundation.PonderIndex
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.GraphicsStatus
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.network.chat.ClickEvent
 *  net.minecraft.network.chat.ClickEvent$Action
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.ComponentUtils
 *  net.minecraft.network.chat.HoverEvent
 *  net.minecraft.network.chat.HoverEvent$Action
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.fml.common.Mod
 *  net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
 *  net.neoforged.neoforge.common.NeoForge
 */
package com.simibubi.create;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.ftb.FTBIntegration;
import com.simibubi.create.compat.pojav.PojavChecker;
import com.simibubi.create.compat.sodium.SodiumCompat;
import com.simibubi.create.content.contraptions.glue.SuperGlueSelectionHandler;
import com.simibubi.create.content.contraptions.render.ContraptionEntityRenderer;
import com.simibubi.create.content.decoration.encasing.CasingConnectivity;
import com.simibubi.create.content.equipment.bell.SoulPulseEffectHandler;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonRenderHandler;
import com.simibubi.create.content.equipment.zapper.ZapperRenderHandler;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.kinetics.waterwheel.WaterWheelRenderer;
import com.simibubi.create.content.schematics.client.ClientSchematicLoader;
import com.simibubi.create.content.schematics.client.SchematicAndQuillHandler;
import com.simibubi.create.content.schematics.client.SchematicHandler;
import com.simibubi.create.content.trains.GlobalRailwayManager;
import com.simibubi.create.foundation.ClientResourceReloadListener;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsClient;
import com.simibubi.create.foundation.model.ModelSwapper;
import com.simibubi.create.foundation.ponder.CreatePonderPlugin;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.gui.CreateMainMenuScreen;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.createmod.catnip.config.ui.ConfigScreen;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBufferCache;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value="create", dist={Dist.CLIENT})
public class CreateClient {
    public static final ModelSwapper MODEL_SWAPPER = new ModelSwapper();
    public static final CasingConnectivity CASING_CONNECTIVITY = new CasingConnectivity();
    public static final ClientSchematicLoader SCHEMATIC_SENDER = new ClientSchematicLoader();
    public static final SchematicHandler SCHEMATIC_HANDLER = new SchematicHandler();
    public static final SchematicAndQuillHandler SCHEMATIC_AND_QUILL_HANDLER = new SchematicAndQuillHandler();
    public static final SuperGlueSelectionHandler GLUE_HANDLER = new SuperGlueSelectionHandler();
    public static final ZapperRenderHandler ZAPPER_RENDER_HANDLER = new ZapperRenderHandler();
    public static final PotatoCannonRenderHandler POTATO_CANNON_RENDER_HANDLER = new PotatoCannonRenderHandler();
    public static final SoulPulseEffectHandler SOUL_PULSE_EFFECT_HANDLER = new SoulPulseEffectHandler();
    public static final GlobalRailwayManager RAILWAYS = new GlobalRailwayManager();
    public static final ValueSettingsClient VALUE_SETTINGS_HANDLER = new ValueSettingsClient();
    public static final ClientResourceReloadListener RESOURCE_RELOAD_LISTENER = new ClientResourceReloadListener();

    public CreateClient(IEventBus modEventBus) {
        CreateClient.onCtorClient(modEventBus);
    }

    public static void onCtorClient(IEventBus modEventBus) {
        IEventBus neoEventBus = NeoForge.EVENT_BUS;
        modEventBus.addListener(CreateClient::clientInit);
        modEventBus.addListener(AllParticleTypes::registerFactories);
        AllInstanceTypes.init();
        MODEL_SWAPPER.registerListeners(modEventBus);
        ZAPPER_RENDER_HANDLER.registerListeners(neoEventBus);
        POTATO_CANNON_RENDER_HANDLER.registerListeners(neoEventBus);
        Mods.FTBLIBRARY.executeIfInstalled(() -> () -> FTBIntegration.init(modEventBus, neoEventBus));
        Mods.SODIUM.executeIfInstalled(() -> () -> SodiumCompat.init(modEventBus, neoEventBus));
        PojavChecker.init();
    }

    public static void clientInit(FMLClientSetupEvent event) {
        SuperByteBufferCache.getInstance().registerCompartment(CachedBuffers.PARTIAL);
        SuperByteBufferCache.getInstance().registerCompartment(CachedBuffers.DIRECTIONAL_PARTIAL);
        SuperByteBufferCache.getInstance().registerCompartment(KineticBlockEntityRenderer.KINETIC_BLOCK);
        SuperByteBufferCache.getInstance().registerCompartment(WaterWheelRenderer.WATER_WHEEL);
        SuperByteBufferCache.getInstance().registerCompartment(ContraptionEntityRenderer.CONTRAPTION, 20L);
        AllPartialModels.init();
        PonderIndex.addPlugin((PonderPlugin)new CreatePonderPlugin());
        CreateClient.setupConfigUIBackground();
    }

    private static void setupConfigUIBackground() {
        ConfigScreen.backgrounds.put("create", (screen, graphics, partialTicks) -> {
            CreateMainMenuScreen.PANORAMA.render(graphics, screen.width, screen.height, 1.0f, partialTicks.floatValue());
            RenderSystem.enableBlend();
            RenderSystem.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            graphics.blit(CreateMainMenuScreen.PANORAMA_OVERLAY_TEXTURES, 0, 0, screen.width, screen.height, 0.0f, 0.0f, 16, 128, 16, 128);
            graphics.fill(0, 0, screen.width, screen.height, -1876415436);
        });
        ConfigScreen.shadowState = (BlockState)AllBlocks.LARGE_COGWHEEL.getDefaultState().setValue((Property)CogWheelBlock.AXIS, (Comparable)Direction.Axis.Y);
        BaseConfigScreen.setDefaultActionFor((String)"create", base -> base.withButtonLabels("Client Settings", "World Generation Settings", "Gameplay Settings").withSpecs(AllConfigs.client().specification, AllConfigs.common().specification, AllConfigs.server().specification));
    }

    public static void invalidateRenderers() {
        SCHEMATIC_HANDLER.updateRenderers();
    }

    public static void checkGraphicsFanciness() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        if (mc.options.graphicsMode().get() != GraphicsStatus.FABULOUS) {
            return;
        }
        if (((Boolean)AllConfigs.client().ignoreFabulousWarning.get()).booleanValue()) {
            return;
        }
        MutableComponent text = ComponentUtils.wrapInSquareBrackets((Component)Component.literal((String)"WARN")).withStyle(ChatFormatting.GOLD).append((Component)Component.literal((String)" Some of Create's visual features will not be available while Fabulous graphics are enabled!")).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/create dismissFabulousWarning")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (Object)Component.literal((String)"Click here to disable this warning"))));
        mc.player.displayClientMessage((Component)text, false);
    }
}
