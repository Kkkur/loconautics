/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.foundation.utility;

import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public class ResetableLazy<T>
implements Supplier<T> {
    private final Supplier<@NotNull T> supplier;
    private T value;

    public ResetableLazy(Supplier<@NotNull T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (this.value == null) {
            this.value = this.supplier.get();
        }
        return this.value;
    }

    public void reset() {
        this.value = null;
    }

    public static <T> ResetableLazy<T> of(Supplier<@NotNull T> supplier) {
        return new ResetableLazy<T>(supplier);
    }
}
