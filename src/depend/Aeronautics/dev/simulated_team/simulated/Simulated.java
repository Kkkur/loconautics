/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.simibubi.create.foundation.item.ItemDescription$Modifier
 *  com.simibubi.create.foundation.item.KineticStats
 *  com.simibubi.create.foundation.item.TooltipHelper
 *  com.simibubi.create.foundation.item.TooltipModifier
 *  com.tterrag.registrate.providers.ProviderType
 *  com.tterrag.registrate.util.nullness.NonNullSupplier
 *  dev.ryanhcode.sable.platform.SableEventPlatform
 *  net.createmod.catnip.lang.FontHelper$Palette
 *  net.minecraft.ChatFormatting
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Rarity
 *  org.slf4j.Logger
 */
package dev.simulated_team.simulated;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.ryanhcode.sable.platform.SableEventPlatform;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.events.SimulatedCommonEvents;
import dev.simulated_team.simulated.index.SimBlockEntityTypes;
import dev.simulated_team.simulated.index.SimBlockMovementChecks;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.index.SimDataComponents;
import dev.simulated_team.simulated.index.SimEntityDataSerializers;
import dev.simulated_team.simulated.index.SimEntityTypes;
import dev.simulated_team.simulated.index.SimItemAttributeTypes;
import dev.simulated_team.simulated.index.SimItems;
import dev.simulated_team.simulated.index.SimMenuTypes;
import dev.simulated_team.simulated.index.SimNavigationTargets;
import dev.simulated_team.simulated.index.SimParticleTypes;
import dev.simulated_team.simulated.index.SimRegistries;
import dev.simulated_team.simulated.index.SimSoundEvents;
import dev.simulated_team.simulated.index.SimSpriteShifts;
import dev.simulated_team.simulated.index.SimTags;
import dev.simulated_team.simulated.network.SimPacketManager;
import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import dev.simulated_team.simulated.service.SimModCompatibilityService;
import dev.simulated_team.simulated.util.SimAssemblyHelper;
import dev.simulated_team.simulated.util.SimColors;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import org.slf4j.Logger;

public final class Simulated {
    public static final String MOD_ID = "simulated";
    public static final String MOD_NAME = "Create Simulated";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final NonNullSupplier<SimulatedRegistrate> REGISTRATE = NonNullSupplier.lazy(() -> (SimulatedRegistrate)new SimulatedRegistrate(Simulated.path(MOD_ID), MOD_ID).defaultCreativeTab(null));

    public static void init() {
        Simulated.setTooltips();
        SimEntityDataSerializers.register();
        Simulated.getRegistrate().addDataGenerator(ProviderType.LANG, SimLang::registrateLang);
        SimRegistries.register();
        SimTags.register();
        SimBlocks.register();
        SimItems.register();
        SimBlockEntityTypes.register();
        SimParticleTypes.register();
        SimSoundEvents.init();
        SimSpriteShifts.init();
        SimPacketManager.init();
        SimEntityTypes.register();
        SimMenuTypes.register();
        SimNavigationTargets.register();
        SimDataComponents.register();
        SimItemAttributeTypes.init();
        SimulatedCommonEvents.register();
        SimBlockMovementChecks.register();
        SimAssemblyHelper.register();
        SimModCompatibilityService.initLoaded();
        SableEventPlatform.INSTANCE.onPhysicsTick(SimulatedCommonEvents::onPhysicsTick);
        SableEventPlatform.INSTANCE.onPostPhysicsTick(SimulatedCommonEvents::onPostPhysicsTick);
    }

    public static void setTooltips() {
        Simulated.getRegistrate().setTooltipModifierFactory(item -> {
            Rarity rarity = item.getDefaultInstance().getRarity();
            FontHelper.Palette color = FontHelper.Palette.STANDARD_CREATE;
            if (rarity == Rarity.EPIC) {
                color = new FontHelper.Palette(TooltipHelper.styleFromColor((int)SimColors.EPIC_OURPLE), TooltipHelper.styleFromColor((ChatFormatting)rarity.color()));
            }
            return new ItemDescription.Modifier(item, color).andThen(TooltipModifier.mapNull((TooltipModifier)KineticStats.create((Item)item)));
        });
    }

    public static SimulatedRegistrate getRegistrate() {
        return (SimulatedRegistrate)((Object)REGISTRATE.get());
    }

    public static ResourceLocation path(String path) {
        return ResourceLocation.tryBuild((String)MOD_ID, (String)path);
    }
}
