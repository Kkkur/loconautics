/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.lifting_gas;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class LiftingGasData {
    public static Codec<LiftingGasData> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.DOUBLE.fieldOf("target").forGetter(LiftingGasData::getTarget), (App)Codec.DOUBLE.fieldOf("amount").forGetter(LiftingGasData::getAmount), (App)Codec.DOUBLE.fieldOf("nudge").forGetter(LiftingGasData::getNudge)).apply((Applicative)instance, LiftingGasData::new));
    public double target;
    public double amount;
    public double nudge;

    public LiftingGasData() {
        this.target = 0.0;
        this.amount = 0.0;
        this.nudge = 0.0;
    }

    public LiftingGasData(double target, double amount, double nudge) {
        this.target = target;
        this.amount = amount;
        this.nudge = nudge;
    }

    public double getTarget() {
        return this.target;
    }

    public double getAmount() {
        return this.amount;
    }

    public double getNudge() {
        return this.nudge;
    }
}
