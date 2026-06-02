/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.config.ui.ConfigAnnotations$IntDisplay
 */
package com.simibubi.create.infrastructure.config;

import net.createmod.catnip.config.ui.ConfigAnnotations;

private static class CClient.Comments {
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

    private CClient.Comments() {
    }
}
