/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.advancements.critereon.MinMaxBounds$Doubles
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.command.argument.modifier_type;

import dev.ryanhcode.sable.command.argument.SubLevelSelectorModifierType;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.advancements.critereon.MinMaxBounds;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class SubLevelDoubleRangeFilter
implements SubLevelSelectorModifierType.Modifier {
    private final MinMaxBounds.Doubles range;
    private final DoubleGetter valueGetter;
    private final boolean squared;

    private SubLevelDoubleRangeFilter(MinMaxBounds.Doubles range, DoubleGetter valueGetter, boolean squared) {
        this.range = range;
        this.valueGetter = valueGetter;
        this.squared = squared;
    }

    public static Factory linear(DoubleGetter valueGetter) {
        return new Factory(valueGetter, false);
    }

    public static Factory squared(DoubleGetter valueGetter) {
        return new Factory(valueGetter, true);
    }

    @Override
    public int getMaxResults() {
        return Integer.MAX_VALUE;
    }

    @Override
    @Nullable
    public List<ServerSubLevel> apply(List<ServerSubLevel> selected, Vector3d sourcePos) {
        ObjectArrayList filtered = new ObjectArrayList();
        for (ServerSubLevel subLevel : selected) {
            double value = this.valueGetter.fromSublevel(subLevel, (Vector3dc)sourcePos);
            if (this.squared) {
                if (!this.range.matchesSqr(value)) continue;
                filtered.add(subLevel);
                continue;
            }
            if (!this.range.matches(value)) continue;
            filtered.add(subLevel);
        }
        return filtered;
    }

    @FunctionalInterface
    public static interface DoubleGetter {
        public double fromSublevel(ServerSubLevel var1, Vector3dc var2);
    }

    public static class Factory {
        private final DoubleGetter doubleGetter;
        private final boolean squared;

        public Factory(DoubleGetter doubleGetter, boolean squared) {
            this.doubleGetter = doubleGetter;
            this.squared = squared;
        }

        public SubLevelDoubleRangeFilter create(MinMaxBounds.Doubles range) {
            return new SubLevelDoubleRangeFilter(range, this.doubleGetter, this.squared);
        }
    }
}
