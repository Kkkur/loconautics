/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 */
package com.simibubi.create.foundation.blockEntity.behaviour;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;
import net.minecraft.network.chat.Component;

public static class ValueSettingsFormatter.ScrollOptionSettingsFormatter
extends ValueSettingsFormatter {
    private final INamedIconOptions[] options;

    public ValueSettingsFormatter.ScrollOptionSettingsFormatter(INamedIconOptions[] options) {
        super(v -> Component.translatable((String)options[v.value()].getTranslationKey()));
        this.options = options;
    }

    public AllIcons getIcon(ValueSettingsBehaviour.ValueSettings valueSettings) {
        return this.options[valueSettings.value()].getIcon();
    }
}
