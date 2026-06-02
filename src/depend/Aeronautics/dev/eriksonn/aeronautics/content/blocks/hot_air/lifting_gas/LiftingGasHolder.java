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
import dev.eriksonn.aeronautics.Aeronautics;
import dev.eriksonn.aeronautics.content.blocks.hot_air.lifting_gas.LiftingGasData;
import dev.eriksonn.aeronautics.content.blocks.hot_air.lifting_gas.LiftingGasType;
import dev.eriksonn.aeronautics.index.AeroRegistries;

public record LiftingGasHolder(LiftingGasType type, LiftingGasData data) {
    public static Codec<LiftingGasHolder> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Aeronautics.getRegistrate().byNameCodecExpanded(AeroRegistries.Keys.LIFTING_GAS_TYPE).fieldOf("type").forGetter(LiftingGasHolder::type), (App)LiftingGasData.CODEC.fieldOf("data").forGetter(LiftingGasHolder::data)).apply((Applicative)instance, LiftingGasHolder::new));

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LiftingGasHolder)) {
            return false;
        }
        return this.type == ((LiftingGasHolder)o).type;
    }
}
