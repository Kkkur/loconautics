/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.mojang.logging.LogUtils
 *  net.createmod.catnip.lang.FontHelper$Palette
 *  net.createmod.catnip.lang.LangBuilder
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.neoforged.bus.api.EventPriority
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.fml.ModContainer
 *  net.neoforged.fml.ModLoadingContext
 *  net.neoforged.fml.common.Mod
 *  net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
 *  net.neoforged.neoforge.common.NeoForgeMod
 *  net.neoforged.neoforge.registries.RegisterEvent
 *  org.slf4j.Logger
 */
package com.simibubi.create;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.simibubi.create.AllAttachmentTypes;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlockSpoutingBehaviours;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.AllContraptionMovementSettings;
import com.simibubi.create.AllContraptionTypes;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllDisplaySources;
import com.simibubi.create.AllDisplayTargets;
import com.simibubi.create.AllEntityDataSerializers;
import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.AllFluids;
import com.simibubi.create.AllInteractionBehaviours;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllMapDecorationTypes;
import com.simibubi.create.AllMenuTypes;
import com.simibubi.create.AllMountedDispenseItemBehaviors;
import com.simibubi.create.AllMountedStorageTypes;
import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.AllOpenPipeEffectHandlers;
import com.simibubi.create.AllPackets;
import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.AllSchematicStateFilters;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.AllStructureProcessorTypes;
import com.simibubi.create.CreateBuildInfo;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.computercraft.ComputerCraftProxy;
import com.simibubi.create.compat.curios.Curios;
import com.simibubi.create.compat.inventorySorter.InventorySorterCompat;
import com.simibubi.create.content.decoration.palettes.AllPaletteBlocks;
import com.simibubi.create.content.equipment.armor.AllArmorMaterials;
import com.simibubi.create.content.equipment.potatoCannon.AllPotatoProjectileBlockHitActions;
import com.simibubi.create.content.equipment.potatoCannon.AllPotatoProjectileEntityHitActions;
import com.simibubi.create.content.equipment.potatoCannon.AllPotatoProjectileRenderModes;
import com.simibubi.create.content.fluids.tank.BoilerHeaters;
import com.simibubi.create.content.kinetics.TorquePropagator;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.logistics.item.filter.attribute.AllItemAttributeTypes;
import com.simibubi.create.content.logistics.packagePort.AllPackagePortTargetTypes;
import com.simibubi.create.content.logistics.packager.AllInventoryIdentifiers;
import com.simibubi.create.content.logistics.packager.AllUnpackingHandlers;
import com.simibubi.create.content.logistics.packagerLink.GlobalLogisticsManager;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import com.simibubi.create.content.schematics.ServerSchematicLoader;
import com.simibubi.create.content.trains.GlobalRailwayManager;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.track.AllPortalTracks;
import com.simibubi.create.foundation.CreateNBTProcessors;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.advancement.AllTriggers;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.foundation.recipe.AllIngredients;
import com.simibubi.create.infrastructure.command.ServerLagger;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.data.CreateDatagen;
import com.simibubi.create.infrastructure.worldgen.AllFeatures;
import com.simibubi.create.infrastructure.worldgen.AllPlacementModifiers;
import java.util.Random;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(value="create")
public class Create {
    public static final String ID = "create";
    public static final String NAME = "Create";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    @Deprecated
    public static final Random RANDOM = new Random();
    private static final CreateRegistrate REGISTRATE = ((CreateRegistrate)CreateRegistrate.create("create").defaultCreativeTab(null)).setTooltipModifierFactory(item -> new ItemDescription.Modifier((Item)item, FontHelper.Palette.STANDARD_CREATE).andThen(TooltipModifier.mapNull(KineticStats.create(item))));
    public static final ServerSchematicLoader SCHEMATIC_RECEIVER = new ServerSchematicLoader();
    public static final RedstoneLinkNetworkHandler REDSTONE_LINK_NETWORK_HANDLER = new RedstoneLinkNetworkHandler();
    public static final TorquePropagator TORQUE_PROPAGATOR = new TorquePropagator();
    public static final GlobalRailwayManager RAILWAYS = new GlobalRailwayManager();
    public static final GlobalLogisticsManager LOGISTICS = new GlobalLogisticsManager();
    public static final ServerLagger LAGGER = new ServerLagger();

    public Create(IEventBus eventBus, ModContainer modContainer) {
        Create.onCtor(eventBus, modContainer);
    }

    public static void onCtor(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("{} {} initializing! Commit hash: {}", new Object[]{NAME, CreateBuildInfo.VERSION, CreateBuildInfo.GIT_COMMIT});
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        REGISTRATE.registerEventListeners(modEventBus);
        AllSoundEvents.prepare();
        AllCreativeModeTabs.register(modEventBus);
        AllArmorMaterials.register(modEventBus);
        AllDisplaySources.register();
        AllDisplayTargets.register();
        AllBlocks.register();
        AllItems.register();
        AllFluids.register();
        AllPaletteBlocks.register();
        AllMenuTypes.register();
        AllEntityTypes.register();
        AllBlockEntityTypes.register();
        AllRecipeTypes.register(modEventBus);
        AllParticleTypes.register(modEventBus);
        AllStructureProcessorTypes.register(modEventBus);
        AllEntityDataSerializers.register(modEventBus);
        AllPackets.register();
        AllFeatures.register(modEventBus);
        AllPlacementModifiers.register(modEventBus);
        AllIngredients.register(modEventBus);
        AllAttachmentTypes.register(modEventBus);
        AllDataComponents.register(modEventBus);
        AllMapDecorationTypes.register(modEventBus);
        AllMountedStorageTypes.register();
        AllConfigs.register(modLoadingContext, modContainer);
        AllPackagePortTargetTypes.register(modEventBus);
        AllSchematicStateFilters.registerDefaults();
        BogeySizes.init();
        AllBogeyStyles.init();
        ComputerCraftProxy.register();
        NeoForgeMod.enableMilkFluid();
        modEventBus.addListener(Create::init);
        modEventBus.addListener(Create::onRegister);
        modEventBus.addListener(AllEntityTypes::registerEntityAttributes);
        modEventBus.addListener(EventPriority.HIGHEST, CreateDatagen::gatherDataHighPriority);
        modEventBus.addListener(EventPriority.LOWEST, CreateDatagen::gatherData);
        modEventBus.addListener(AllSoundEvents::register);
        Mods.CURIOS.executeIfInstalled(() -> () -> Curios.init(modEventBus));
        Mods.INVENTORYSORTER.executeIfInstalled(() -> () -> InventorySorterCompat.init(modEventBus));
    }

    public static void init(FMLCommonSetupEvent event) {
        AllFluids.registerFluidInteractions();
        CreateNBTProcessors.register();
        event.enqueueWork(() -> {
            BoilerHeaters.registerDefaults();
            AllPortalTracks.registerDefaults();
            AllBlockSpoutingBehaviours.registerDefaults();
            AllMovementBehaviours.registerDefaults();
            AllInteractionBehaviours.registerDefaults();
            AllContraptionMovementSettings.registerDefaults();
            AllOpenPipeEffectHandlers.registerDefaults();
            AllMountedDispenseItemBehaviors.registerDefaults();
            AllUnpackingHandlers.registerDefaults();
            AllInventoryIdentifiers.registerDefaults();
        });
    }

    public static void onRegister(RegisterEvent event) {
        AllArmInteractionPointTypes.init();
        AllFanProcessingTypes.init();
        AllItemAttributeTypes.init();
        AllContraptionTypes.init();
        AllPotatoProjectileRenderModes.init();
        AllPotatoProjectileEntityHitActions.init();
        AllPotatoProjectileBlockHitActions.init();
        if (event.getRegistry() == BuiltInRegistries.TRIGGER_TYPES) {
            AllAdvancements.register();
            AllTriggers.register();
        }
    }

    public static LangBuilder lang() {
        return new LangBuilder(ID);
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath((String)ID, (String)path);
    }

    public static CreateRegistrate registrate() {
        if (!STACK_WALKER.getCallerClass().getPackageName().startsWith("com.simibubi.create")) {
            throw new UnsupportedOperationException("Other mods are not permitted to use create's registrate instance.");
        }
        return REGISTRATE;
    }
}
