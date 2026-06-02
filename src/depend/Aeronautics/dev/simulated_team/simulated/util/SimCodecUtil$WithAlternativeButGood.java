/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 */
package dev.simulated_team.simulated.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

private record SimCodecUtil.WithAlternativeButGood<T>(Codec<T> first, Codec<T> second) implements Codec<T>
{
    public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
        DataResult result = this.first.decode(ops, input);
        if (result.isSuccess()) {
            return result;
        }
        return this.second.decode(ops, input);
    }

    public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
        DataResult result = this.first.encode(input, ops, prefix);
        if (result.isSuccess()) {
            return result;
        }
        return this.second.encode(input, ops, prefix);
    }
}
