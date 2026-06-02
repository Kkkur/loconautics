/*
 * Decompiled with CFR 0.152.
 */
package dev.ryanhcode.sable.command.argument.modifier_type;

import dev.ryanhcode.sable.command.argument.modifier_type.SubLevelDoubleFilter;

public static class SubLevelDoubleFilter.Factory {
    private final SubLevelDoubleFilter.DoublePredicate doublePredicate;

    public SubLevelDoubleFilter.Factory(SubLevelDoubleFilter.DoublePredicate doublePredicate) {
        this.doublePredicate = doublePredicate;
    }

    public SubLevelDoubleFilter create(double value) {
        return new SubLevelDoubleFilter(value, this.doublePredicate);
    }
}
