/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.lifting_gas;

import dev.eriksonn.aeronautics.config.AeroConfig;
import dev.eriksonn.aeronautics.content.blocks.hot_air.lifting_gas.LiftingGasType;
import dev.eriksonn.aeronautics.data.AeroLang;
import net.minecraft.network.chat.Component;

public class SteamLiftingGas
implements LiftingGasType {
    @Override
    public Component getName() {
        return AeroLang.translate("lifting_gas.steam", new Object[0]).component();
    }

    @Override
    public double getFillingTime() {
        return 180.0;
    }

    @Override
    public double getEmptyingTime() {
        return 180.0;
    }

    @Override
    public double getLiftStrength() {
        return (Double)AeroConfig.server().physics.steamStrength.get();
    }

    @Override
    public double getResponsivenessAdjustmentFactor() {
        return 5.0;
    }

    @Override
    public double getResponsivenessAdjustmentRange() {
        return 0.05;
    }
}
