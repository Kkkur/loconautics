/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.infrastructure.gui;

import com.simibubi.create.infrastructure.gui.OpenCreateMenuButton;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public static class OpenCreateMenuButton.MenuRows {
    public static final OpenCreateMenuButton.MenuRows MAIN_MENU = new OpenCreateMenuButton.MenuRows(Arrays.asList(new OpenCreateMenuButton.SingleMenuRow("menu.singleplayer"), new OpenCreateMenuButton.SingleMenuRow("menu.multiplayer"), new OpenCreateMenuButton.SingleMenuRow("fml.menu.mods", "menu.online"), new OpenCreateMenuButton.SingleMenuRow("narrator.button.language", "narrator.button.accessibility")));
    public static final OpenCreateMenuButton.MenuRows INGAME_MENU = new OpenCreateMenuButton.MenuRows(Arrays.asList(new OpenCreateMenuButton.SingleMenuRow("menu.returnToGame"), new OpenCreateMenuButton.SingleMenuRow("gui.advancements", "gui.stats"), new OpenCreateMenuButton.SingleMenuRow("menu.sendFeedback", "menu.reportBugs"), new OpenCreateMenuButton.SingleMenuRow("menu.options", "menu.shareToLan"), new OpenCreateMenuButton.SingleMenuRow("menu.returnToMenu")));
    protected final List<String> leftTextKeys;
    protected final List<String> rightTextKeys;

    public OpenCreateMenuButton.MenuRows(List<OpenCreateMenuButton.SingleMenuRow> rows) {
        this.leftTextKeys = rows.stream().map(OpenCreateMenuButton.SingleMenuRow::leftTextKey).collect(Collectors.toList());
        this.rightTextKeys = rows.stream().map(OpenCreateMenuButton.SingleMenuRow::rightTextKey).collect(Collectors.toList());
    }
}
