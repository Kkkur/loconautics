/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.config.ConfigBase
 *  net.createmod.catnip.config.ConfigBase$ConfigBool
 *  net.createmod.catnip.config.ConfigBase$ConfigEnum
 *  net.createmod.catnip.config.ConfigBase$ConfigFloat
 *  net.createmod.catnip.config.ConfigBase$ConfigGroup
 *  net.createmod.catnip.config.ConfigBase$ConfigInt
 *  net.createmod.catnip.config.ui.ConfigAnnotations$IntDisplay
 */
package com.simibubi.create.infrastructure.config;

import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestScreen;
import net.createmod.catnip.config.ConfigBase;
import net.createmod.catnip.config.ui.ConfigAnnotations;

public class CClient
extends ConfigBase {
    public final ConfigBase.ConfigGroup client = this.group(0, "client", new String[]{Comments.client});
    public final ConfigBase.ConfigBool tooltips = this.b(true, "enableTooltips", new String[]{Comments.tooltips});
    public final ConfigBase.ConfigBool enableOverstressedTooltip = this.b(true, "enableOverstressedTooltip", new String[]{Comments.enableOverstressedTooltip});
    public final ConfigBase.ConfigBool explainRenderErrors = this.b(false, "explainRenderErrors", new String[]{Comments.explainRenderErrors});
    public final ConfigBase.ConfigFloat fanParticleDensity = this.f(0.5f, 0.0f, 1.0f, "fanParticleDensity", new String[]{Comments.fanParticleDensity});
    public final ConfigBase.ConfigFloat filterItemRenderDistance = this.f(10.0f, 1.0f, "filterItemRenderDistance", Comments.filterItemRenderDistance);
    public final ConfigBase.ConfigInt mainMenuConfigButtonRow = this.i(2, 0, 4, "mainMenuConfigButtonRow", Comments.mainMenuConfigButtonRow);
    public final ConfigBase.ConfigInt mainMenuConfigButtonOffsetX = this.i(-4, Integer.MIN_VALUE, Integer.MAX_VALUE, "mainMenuConfigButtonOffsetX", Comments.mainMenuConfigButtonOffsetX);
    public final ConfigBase.ConfigInt ingameMenuConfigButtonRow = this.i(3, 0, 5, "ingameMenuConfigButtonRow", Comments.ingameMenuConfigButtonRow);
    public final ConfigBase.ConfigInt ingameMenuConfigButtonOffsetX = this.i(-4, Integer.MIN_VALUE, Integer.MAX_VALUE, "ingameMenuConfigButtonOffsetX", Comments.ingameMenuConfigButtonOffsetX);
    public final ConfigBase.ConfigBool ignoreFabulousWarning = this.b(false, "ignoreFabulousWarning", new String[]{Comments.ignoreFabulousWarning});
    public final ConfigBase.ConfigBool rotateWhenSeated = this.b(true, "rotateWhenSeated", new String[]{Comments.rotatewhenSeated});
    public final ConfigBase.ConfigGroup fluidFogSettings = this.group(1, "fluidFogSettings", new String[]{Comments.fluidFogSettings});
    public final ConfigBase.ConfigFloat honeyTransparencyMultiplier = this.f(1.0f, 0.125f, 256.0f, "honey", new String[]{Comments.honeyTransparencyMultiplier});
    public final ConfigBase.ConfigFloat chocolateTransparencyMultiplier = this.f(1.0f, 0.125f, 256.0f, "chocolate", new String[]{Comments.chocolateTransparencyMultiplier});
    public final ConfigBase.ConfigGroup overlay = this.group(1, "goggleOverlay", new String[]{Comments.overlay});
    public final ConfigBase.ConfigInt overlayOffsetX = this.i(20, Integer.MIN_VALUE, Integer.MAX_VALUE, "overlayOffsetX", new String[]{Comments.overlayOffset});
    public final ConfigBase.ConfigInt overlayOffsetY = this.i(0, Integer.MIN_VALUE, Integer.MAX_VALUE, "overlayOffsetY", new String[]{Comments.overlayOffset});
    public final ConfigBase.ConfigBool overlayCustomColor = this.b(false, "customColorsOverlay", new String[]{Comments.overlayCustomColor});
    public final ConfigBase.ConfigInt overlayBackgroundColor = this.i(-267386864, Integer.MIN_VALUE, Integer.MAX_VALUE, "customBackgroundOverlay", Comments.overlayBackgroundColor);
    public final ConfigBase.ConfigInt overlayBorderColorTop = this.i(0x505000FF, Integer.MIN_VALUE, Integer.MAX_VALUE, "customBorderTopOverlay", Comments.overlayBorderColorTop);
    public final ConfigBase.ConfigInt overlayBorderColorBot = this.i(1344798847, Integer.MIN_VALUE, Integer.MAX_VALUE, "customBorderBotOverlay", Comments.overlayBorderColorBot);
    public final ConfigBase.ConfigGroup sound = this.group(1, "sound", new String[]{Comments.sound});
    public final ConfigBase.ConfigBool enableAmbientSounds = this.b(true, "enableAmbientSounds", new String[]{Comments.enableAmbientSounds});
    public final ConfigBase.ConfigFloat ambientVolumeCap = this.f(0.1f, 0.0f, 1.0f, "ambientVolumeCap", new String[]{Comments.ambientVolumeCap});
    public final ConfigBase.ConfigGroup integration = this.group(1, "recipeViewerIntegration", new String[]{Comments.integration});
    public final ConfigBase.ConfigEnum<StockKeeperRequestScreen.SearchSyncMode> syncRecipeViewerSearch = this.e(StockKeeperRequestScreen.SearchSyncMode.SYNC_BOTH, "syncRecipeViewerSearch", new String[]{Comments.syncRecipeViewerSearch});
    public final ConfigBase.ConfigGroup trains = this.group(1, "trains", new String[]{Comments.trains});
    public final ConfigBase.ConfigFloat mountedZoomMultiplier = this.f(3.0f, 0.0f, "mountedZoomMultiplier", new String[]{Comments.mountedZoomMultiplier});
    public final ConfigBase.ConfigBool showTrackGraphOnF3 = this.b(false, "showTrackGraphOnF3", new String[]{Comments.showTrackGraphOnF3});
    public final ConfigBase.ConfigBool showExtendedTrackGraphOnF3 = this.b(false, "showExtendedTrackGraphOnF3", new String[]{Comments.showExtendedTrackGraphOnF3});
    public final ConfigBase.ConfigBool showTrainMapOverlay = this.b(true, "showTrainMapOverlay", new String[]{Comments.showTrainMapOverlay});
    public final ConfigBase.ConfigEnum<TrainMapTheme> trainMapColorTheme = this.e(TrainMapTheme.RED, "trainMapColorTheme", new String[]{Comments.trainMapColorTheme});

    public String getName() {
        return "client";
    }

    private static class Comments {
        static String client = "Client-only settings - If you're looking for general settings, look inside your worlds serverconfig folder!";
        static String tooltips = "Show item descriptions on Shift and controls on Ctrl.";
        static String enableOverstressedTooltip = "Display a tooltip when looking at overstressed components.";
        static String explainRenderErrors = "Log a stack-trace when rendering issues happen within a moving contraption.";
        static String fanParticleDensity = "Higher density means more spawned particles.";
        static String[] filterItemRenderDistance = new String[]{"[in Blocks]", "Maximum Distance to the player at which items in Blocks' filter slots will be displayed"};
        static String[] mainMenuConfigButtonRow = new String[]{"Choose the menu row that the Create config button appears on in the main menu", "Set to 0 to disable the button altogether"};
        static String[] mainMenuConfigButtonOffsetX = new String[]{"Offset the Create config button in the main menu by this many pixels on the X axis", "The sign (-/+) of this value determines what side of the row the button appears on (left/right)"};
        static String[] ingameMenuConfigButtonRow = new String[]{"Choose the menu row that the Create config button appears on in the in-game menu", "Set to 0 to disable the button altogether"};
        static String[] ingameMenuConfigButtonOffsetX = new String[]{"Offset the Create config button in the in-game menu by this many pixels on the X axis", "The sign (-/+) of this value determines what side of the row the button appears on (left/right)"};
        static String ignoreFabulousWarning = "Setting this to true will prevent Create from sending you a warning when playing with Fabulous graphics enabled";
        static String rotatewhenSeated = "Disable to prevent being rotated while seated on a Moving Contraption";
        static String overlay = "Settings for the Goggle Overlay";
        static String overlayOffset = "Offset the overlay from goggle- and hover- information by this many pixels on the respective axis; Use /create overlay";
        static String overlayCustomColor = "Enable this to use your custom colors for the Goggle- and Hover- Overlay";
        static String[] overlayBackgroundColor = new String[]{"The custom background color to use for the Goggle- and Hover- Overlays, if enabled", "[in Hex: #AaRrGgBb]", ConfigAnnotations.IntDisplay.HEX.asComment()};
        static String[] overlayBorderColorTop = new String[]{"The custom top color of the border gradient to use for the Goggle- and Hover- Overlays, if enabled", "[in Hex: #AaRrGgBb]", ConfigAnnotations.IntDisplay.HEX.asComment()};
        static String[] overlayBorderColorBot = new String[]{"The custom bot color of the border gradient to use for the Goggle- and Hover- Overlays, if enabled", "[in Hex: #AaRrGgBb]", ConfigAnnotations.IntDisplay.HEX.asComment()};
        static String sound = "Sound settings";
        static String enableAmbientSounds = "Make cogs rumble and machines clatter.";
        static String ambientVolumeCap = "Maximum volume modifier of Ambient noise";
        static String trains = "Railway related settings";
        static String showTrainMapOverlay = "Display Track Networks and Trains on supported map mods";
        static String trainMapColorTheme = "Track Network Color on maps";
        static String mountedZoomMultiplier = "How far away the Camera should zoom when seated on a train";
        static String showTrackGraphOnF3 = "Display nodes and edges of a Railway Network while f3 debug mode is active";
        static String showExtendedTrackGraphOnF3 = "Additionally display materials of a Rail Network while f3 debug mode is active";
        static String fluidFogSettings = "Configure your vision range when submerged in Create's custom fluids";
        static String honeyTransparencyMultiplier = "The vision range through honey will be multiplied by this factor";
        static String chocolateTransparencyMultiplier = "The vision range though chocolate will be multiplied by this factor";
        static String integration = "Mod Integration and Recipe Viewer";
        static String syncRecipeViewerSearch = "How Recipe Viewer search should interact with Stock Keepers";

        private Comments() {
        }
    }

    public static enum TrainMapTheme {
        RED,
        GREY,
        WHITE;

    }

    public static enum PlacementIndicatorSetting {
        TEXTURE,
        TRIANGLE,
        NONE;

    }
}
