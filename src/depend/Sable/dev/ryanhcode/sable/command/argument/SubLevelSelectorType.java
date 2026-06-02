/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 */
package dev.ryanhcode.sable.command.argument;

import net.minecraft.network.chat.Component;

public enum SubLevelSelectorType {
    ALL('e', (Component)Component.translatable((String)"argument.sable.body.selector.all"), false),
    NEAREST('n', (Component)Component.translatable((String)"argument.sable.body.selector.nearest"), true),
    RANDOM('r', (Component)Component.translatable((String)"argument.sable.body.selector.random"), true),
    VIEWED('v', (Component)Component.translatable((String)"argument.sable.body.selector.viewed"), true),
    LATEST('l', (Component)Component.translatable((String)"argument.sable.body.selector.latest"), true),
    TRACKING('t', (Component)Component.translatable((String)"argument.sable.body.selector.tracking"), true),
    INSIDE('i', (Component)Component.translatable((String)"argument.sable.body.selector.inside"), true);

    private final char selector;
    private final Component tooltip;
    private final boolean single;

    private SubLevelSelectorType(char selector, Component tooltip, boolean single) {
        this.selector = selector;
        this.tooltip = tooltip;
        this.single = single;
    }

    public static SubLevelSelectorType of(char c) {
        for (SubLevelSelectorType type : SubLevelSelectorType.values()) {
            if (type.selector != c) continue;
            return type;
        }
        return null;
    }

    public char getChar() {
        return this.selector;
    }

    public Component getTooltip() {
        return this.tooltip;
    }

    public boolean single() {
        return this.single;
    }
}
