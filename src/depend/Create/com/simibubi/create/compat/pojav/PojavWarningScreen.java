/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.layouts.Layout
 *  net.minecraft.client.gui.layouts.LayoutElement
 *  net.minecraft.client.gui.layouts.LinearLayout
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.TitleScreen
 *  net.minecraft.client.gui.screens.multiplayer.WarningScreen
 *  net.minecraft.network.chat.Component
 */
package com.simibubi.create.compat.pojav;

import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.WarningScreen;
import net.minecraft.network.chat.Component;

public class PojavWarningScreen
extends WarningScreen {
    public static final Component TITLE = CreateLang.translateDirect("gui.pojav.title", new Object[0]).withStyle(ChatFormatting.RED);
    public static final Component CONTENT = CreateLang.translateDirect("gui.pojav.content", new Object[0]);
    public static final Component NARRATION = TITLE.copy().append("\n").append(CONTENT);
    public static final Component CONTINUE = CreateLang.translateDirect("gui.pojav.continue", new Object[0]);
    public static final Component QUIT = Component.translatable((String)"menu.quit");
    private final TitleScreen titleScreen;

    public PojavWarningScreen(TitleScreen titleScreen) {
        super(TITLE, CONTENT, null, NARRATION);
        this.titleScreen = titleScreen;
    }

    protected Layout addFooterButtons() {
        LinearLayout layout = LinearLayout.horizontal().spacing(8);
        layout.addChild((LayoutElement)Button.builder((Component)CONTINUE, button -> this.minecraft.setScreen((Screen)this.titleScreen)).build());
        layout.addChild((LayoutElement)Button.builder((Component)QUIT, button -> this.minecraft.stop()).build());
        return layout;
    }
}
