/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.MutableComponent
 */
package com.simibubi.create.foundation.blockEntity.behaviour;

import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.network.chat.MutableComponent;

public record ValueSettingsBehaviour.ValueSettings(int row, int value) {
    public MutableComponent format() {
        return CreateLang.number(this.value).component();
    }
}
