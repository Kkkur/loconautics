/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.MutableComponent
 */
package com.simibubi.create.content.redstone.thresholdSwitch;

import net.minecraft.network.chat.MutableComponent;

public interface ThresholdSwitchObservable {
    public int getMaxValue();

    public int getMinValue();

    public int getCurrentValue();

    public MutableComponent format(int var1);
}
