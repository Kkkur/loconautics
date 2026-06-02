/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.TitleScreen
 *  net.neoforged.neoforge.client.event.ScreenEvent$Init$Post
 *  net.neoforged.neoforge.common.NeoForge
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.simibubi.create.compat.pojav;

import com.simibubi.create.compat.pojav.PojavWarningScreen;
import java.util.regex.Pattern;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PojavChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(PojavChecker.class);
    private static final Pattern KNOWN_ANDROID_PATH = Pattern.compile("/data/user/[0-9]+/net\\.kdt\\.pojavlaunch");
    public static final boolean IS_PRESENT = (Boolean)Util.make(() -> {
        String workingDirectory;
        if (System.getenv("POJAV_RENDERER") != null) {
            LOGGER.warn("[Create]: Detected presence of environment variable POJAV_LAUNCHER, which seems to indicate we are running on Android");
            return true;
        }
        String librarySearchPaths = System.getProperty("java.library.path", null);
        if (librarySearchPaths != null) {
            for (String path : librarySearchPaths.split(":")) {
                if (!PojavChecker.isKnownAndroidPathFragment(path)) continue;
                LOGGER.warn("[Create]: Found a library search path which seems to be hosted in an Android filesystem: {}", (Object)path);
                return true;
            }
        }
        if ((workingDirectory = System.getProperty("user.home", null)) != null && PojavChecker.isKnownAndroidPathFragment(workingDirectory)) {
            LOGGER.warn("[Create]: Working directory seems to be hosted in an Android filesystem: {}", (Object)workingDirectory);
            return true;
        }
        return false;
    });
    private static boolean screenShown = false;

    public static void init() {
        if (!IS_PRESENT) {
            return;
        }
        NeoForge.EVENT_BUS.addListener(PojavChecker::onScreenInit);
    }

    public static void onScreenInit(ScreenEvent.Init.Post event) {
        Screen screen;
        if (!screenShown && (screen = event.getScreen()) instanceof TitleScreen) {
            TitleScreen titleScreen = (TitleScreen)screen;
            Minecraft.getInstance().setScreen((Screen)new PojavWarningScreen(titleScreen));
            screenShown = true;
        }
    }

    private static boolean isKnownAndroidPathFragment(String path) {
        return KNOWN_ANDROID_PATH.matcher(path).matches();
    }
}
