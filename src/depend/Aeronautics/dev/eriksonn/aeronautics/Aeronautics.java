/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.item.ItemDescription$Modifier
 *  com.simibubi.create.foundation.item.KineticStats
 *  com.simibubi.create.foundation.item.TooltipHelper
 *  com.simibubi.create.foundation.item.TooltipModifier
 *  com.tterrag.registrate.providers.ProviderType
 *  com.tterrag.registrate.util.nullness.NonNullSupplier
 *  dev.ryanhcode.sable.platform.SableEventPlatform
 *  dev.simulated_team.simulated.util.SimColors
 *  net.createmod.catnip.lang.FontHelper$Palette
 *  net.minecraft.ChatFormatting
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Rarity
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package dev.eriksonn.aeronautics;

import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.eriksonn.aeronautics.data.AeroLang;
import dev.eriksonn.aeronautics.events.AeronauticsCommonEvents;
import dev.eriksonn.aeronautics.index.AeroArmorMaterials;
import dev.eriksonn.aeronautics.index.AeroBlockEntityTypes;
import dev.eriksonn.aeronautics.index.AeroBlockMovementChecks;
import dev.eriksonn.aeronautics.index.AeroBlocks;
import dev.eriksonn.aeronautics.index.AeroDataComponents;
import dev.eriksonn.aeronautics.index.AeroEntityTypes;
import dev.eriksonn.aeronautics.index.AeroItems;
import dev.eriksonn.aeronautics.index.AeroLevititeBlendPropagationContexts;
import dev.eriksonn.aeronautics.index.AeroLiftingGasTypes;
import dev.eriksonn.aeronautics.index.AeroRegistries;
import dev.eriksonn.aeronautics.index.AeroSoundEvents;
import dev.eriksonn.aeronautics.network.AeroPacketManager;
import dev.eriksonn.aeronautics.registry.AeroRegistrate;
import dev.ryanhcode.sable.platform.SableEventPlatform;
import dev.simulated_team.simulated.util.SimColors;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Aeronautics {
    public static final String MOD_ID = "aeronautics";
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"aeronautics");
    private static final NonNullSupplier<AeroRegistrate> REGISTRATE = NonNullSupplier.lazy(() -> (AeroRegistrate)new AeroRegistrate(Aeronautics.path(MOD_ID), MOD_ID).defaultCreativeTab(null));

    public static void init() {
        Aeronautics.setTooltips();
        Aeronautics.getRegistrate().addDataGenerator(ProviderType.LANG, AeroLang::registrateLang);
        AeroBlocks.init();
        AeroBlockEntityTypes.init();
        AeroItems.init();
        AeroEntityTypes.init();
        AeroArmorMaterials.init();
        AeroSoundEvents.init();
        AeroLiftingGasTypes.init();
        AeroBlockMovementChecks.init();
        AeroRegistries.init();
        AeroPacketManager.init();
        AeroLevititeBlendPropagationContexts.init();
        AeroDataComponents.init();
        Aeronautics.listenCommonEvents();
    }

    public static void setTooltips() {
        Aeronautics.getRegistrate().setTooltipModifierFactory(item -> {
            Rarity rarity = item.getDefaultInstance().getRarity();
            FontHelper.Palette color = FontHelper.Palette.STANDARD_CREATE;
            if (rarity == Rarity.EPIC) {
                color = new FontHelper.Palette(TooltipHelper.styleFromColor((int)SimColors.EPIC_OURPLE), TooltipHelper.styleFromColor((ChatFormatting)rarity.color()));
            }
            return new ItemDescription.Modifier(item, color).andThen(TooltipModifier.mapNull((TooltipModifier)KineticStats.create((Item)item)));
        });
    }

    private static void listenCommonEvents() {
        SableEventPlatform.INSTANCE.onPhysicsTick(AeronauticsCommonEvents::physicsTick);
        SableEventPlatform.INSTANCE.onSubLevelContainerReady(AeronauticsCommonEvents::onSubLevelContainerReady);
    }

    public static AeroRegistrate getRegistrate() {
        return (AeroRegistrate)((Object)REGISTRATE.get());
    }

    public static ResourceLocation path(String path) {
        return ResourceLocation.tryBuild((String)MOD_ID, (String)path);
    }
}
