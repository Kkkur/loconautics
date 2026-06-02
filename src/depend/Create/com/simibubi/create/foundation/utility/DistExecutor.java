/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.fml.loading.FMLLoader
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package com.simibubi.create.foundation.utility;

import java.util.concurrent.Callable;
import java.util.function.Supplier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import org.jetbrains.annotations.ApiStatus;

@Deprecated(forRemoval=true, since="1.21")
@ApiStatus.Internal
public class DistExecutor {
    @Deprecated(forRemoval=true, since="1.21")
    @ApiStatus.Internal
    public static <T> T unsafeCallWhenOn(Dist dist, Supplier<Callable<T>> toRun) {
        if (FMLLoader.getDist() == dist) {
            try {
                return toRun.get().call();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
