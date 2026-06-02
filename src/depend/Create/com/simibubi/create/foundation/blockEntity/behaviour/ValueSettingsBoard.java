/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 */
package com.simibubi.create.foundation.blockEntity.behaviour;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import java.util.List;
import net.minecraft.network.chat.Component;

public record ValueSettingsBoard(Component title, int maxValue, int milestoneInterval, List<Component> rows, ValueSettingsFormatter formatter) {
}
