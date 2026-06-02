/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 */
package dev.ryanhcode.sable.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.Function;

public class SableCodecUtil {
    public static <N extends Number> Function<N, DataResult<N>> checkPositive(boolean includeZero) {
        return value -> {
            if (includeZero) {
                if (value.doubleValue() < 0.0) {
                    return DataResult.error(() -> "Value " + String.valueOf(value) + " is not positive or 0");
                }
            } else if (value.doubleValue() <= 0.0) {
                return DataResult.error(() -> "Value " + String.valueOf(value) + " is not positive");
            }
            return DataResult.success((Object)value);
        };
    }

    public static Codec<Double> positiveDouble(boolean includeZero) {
        return Codec.DOUBLE.flatXmap(SableCodecUtil.checkPositive(includeZero), SableCodecUtil.checkPositive(includeZero));
    }
}
