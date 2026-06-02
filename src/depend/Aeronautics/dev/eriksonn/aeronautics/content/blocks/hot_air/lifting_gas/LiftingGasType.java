/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.lifting_gas;

import net.minecraft.network.chat.Component;

public interface LiftingGasType {
    public Component getName();

    public double getFillingTime();

    public double getEmptyingTime();

    public double getLiftStrength();

    public double getResponsivenessAdjustmentFactor();

    public double getResponsivenessAdjustmentRange();
}
