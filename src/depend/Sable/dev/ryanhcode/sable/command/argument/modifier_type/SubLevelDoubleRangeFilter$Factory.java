/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.critereon.MinMaxBounds$Doubles
 */
package dev.ryanhcode.sable.command.argument.modifier_type;

import dev.ryanhcode.sable.command.argument.modifier_type.SubLevelDoubleRangeFilter;
import net.minecraft.advancements.critereon.MinMaxBounds;

public static class SubLevelDoubleRangeFilter.Factory {
    private final SubLevelDoubleRangeFilter.DoubleGetter doubleGetter;
    private final boolean squared;

    public SubLevelDoubleRangeFilter.Factory(SubLevelDoubleRangeFilter.DoubleGetter doubleGetter, boolean squared) {
        this.doubleGetter = doubleGetter;
        this.squared = squared;
    }

    public SubLevelDoubleRangeFilter create(MinMaxBounds.Doubles range) {
        return new SubLevelDoubleRangeFilter(range, this.doubleGetter, this.squared);
    }
}
