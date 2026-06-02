/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.PauseScreen
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.TitleScreen
 *  net.minecraft.client.resources.language.I18n
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.client.event.ScreenEvent$Init$Post
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package com.simibubi.create.infrastructure.gui;

import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.gui.OpenCreateMenuButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.resources.language.I18n;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.apache.commons.lang3.mutable.MutableObject;

@EventBusSubscriber(value={Dist.CLIENT})
public static class OpenCreateMenuButton.OpenConfigButtonHandler {
    @SubscribeEvent
    public static void onGuiInit(ScreenEvent.Init.Post event) {
        int offsetX;
        int rowIdx;
        OpenCreateMenuButton.MenuRows menu;
        Screen screen = event.getScreen();
        if (screen instanceof TitleScreen) {
            menu = OpenCreateMenuButton.MenuRows.MAIN_MENU;
            rowIdx = (Integer)AllConfigs.client().mainMenuConfigButtonRow.get();
            offsetX = (Integer)AllConfigs.client().mainMenuConfigButtonOffsetX.get();
        } else if (screen instanceof PauseScreen) {
            menu = OpenCreateMenuButton.MenuRows.INGAME_MENU;
            rowIdx = (Integer)AllConfigs.client().ingameMenuConfigButtonRow.get();
            offsetX = (Integer)AllConfigs.client().ingameMenuConfigButtonOffsetX.get();
        } else {
            return;
        }
        if (rowIdx == 0) {
            return;
        }
        boolean onLeft = offsetX < 0;
        String targetMessage = I18n.get((String)(onLeft ? menu.leftTextKeys : menu.rightTextKeys).get(rowIdx - 1), (Object[])new Object[0]);
        int offsetX_ = offsetX;
        MutableObject toAdd = new MutableObject(null);
        event.getListenersList().stream().filter(w -> w instanceof AbstractWidget).map(w -> (AbstractWidget)w).filter(w -> w.getMessage().getString().equals(targetMessage)).findFirst().ifPresent(w -> toAdd.setValue((Object)new OpenCreateMenuButton(w.getX() + offsetX_ + (onLeft ? -20 : w.getWidth()), w.getY())));
        if (toAdd.getValue() != null) {
            event.addListener((GuiEventListener)toAdd.getValue());
        }
    }
}
