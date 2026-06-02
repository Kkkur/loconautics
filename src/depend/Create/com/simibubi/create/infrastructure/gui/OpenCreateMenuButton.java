/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.gui.ScreenOpener
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.PauseScreen
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.TitleScreen
 *  net.minecraft.client.resources.language.I18n
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.client.event.ScreenEvent$Init$Post
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package com.simibubi.create.infrastructure.gui;

import com.simibubi.create.AllItems;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.gui.CreateMainMenuScreen;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.apache.commons.lang3.mutable.MutableObject;

public class OpenCreateMenuButton
extends Button {
    public OpenCreateMenuButton(int x, int y) {
        super(x, y, 20, 20, CommonComponents.EMPTY, OpenCreateMenuButton::click, DEFAULT_NARRATION);
    }

    public void renderString(GuiGraphics graphics, Font pFont, int pColor) {
        ItemStack icon = AllItems.GOGGLES.asStack();
        BakedModel bakedmodel = Minecraft.getInstance().getItemRenderer().getModel(icon, (Level)Minecraft.getInstance().level, (LivingEntity)Minecraft.getInstance().player, 0);
        if (bakedmodel == null) {
            return;
        }
        graphics.renderItem(icon, this.getX() + 2, this.getY() + 2);
    }

    public static void click(Button b) {
        ScreenOpener.open((Screen)new CreateMainMenuScreen(Minecraft.getInstance().screen));
    }

    @EventBusSubscriber(value={Dist.CLIENT})
    public static class OpenConfigButtonHandler {
        @SubscribeEvent
        public static void onGuiInit(ScreenEvent.Init.Post event) {
            int offsetX;
            int rowIdx;
            MenuRows menu;
            Screen screen = event.getScreen();
            if (screen instanceof TitleScreen) {
                menu = MenuRows.MAIN_MENU;
                rowIdx = (Integer)AllConfigs.client().mainMenuConfigButtonRow.get();
                offsetX = (Integer)AllConfigs.client().mainMenuConfigButtonOffsetX.get();
            } else if (screen instanceof PauseScreen) {
                menu = MenuRows.INGAME_MENU;
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

    public static class MenuRows {
        public static final MenuRows MAIN_MENU = new MenuRows(Arrays.asList(new SingleMenuRow("menu.singleplayer"), new SingleMenuRow("menu.multiplayer"), new SingleMenuRow("fml.menu.mods", "menu.online"), new SingleMenuRow("narrator.button.language", "narrator.button.accessibility")));
        public static final MenuRows INGAME_MENU = new MenuRows(Arrays.asList(new SingleMenuRow("menu.returnToGame"), new SingleMenuRow("gui.advancements", "gui.stats"), new SingleMenuRow("menu.sendFeedback", "menu.reportBugs"), new SingleMenuRow("menu.options", "menu.shareToLan"), new SingleMenuRow("menu.returnToMenu")));
        protected final List<String> leftTextKeys;
        protected final List<String> rightTextKeys;

        public MenuRows(List<SingleMenuRow> rows) {
            this.leftTextKeys = rows.stream().map(SingleMenuRow::leftTextKey).collect(Collectors.toList());
            this.rightTextKeys = rows.stream().map(SingleMenuRow::rightTextKey).collect(Collectors.toList());
        }
    }

    public record SingleMenuRow(String leftTextKey, String rightTextKey) {
        public SingleMenuRow(String centerTextKey) {
            this(centerTextKey, centerTextKey);
        }
    }
}
