/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.command.argument.modifier_type;

import dev.ryanhcode.sable.command.argument.SubLevelSelectorModifierType;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class SubLevelDoubleFilter
implements SubLevelSelectorModifierType.Modifier {
    private final double value;
    private final DoublePredicate valuePredicate;

    private SubLevelDoubleFilter(double value, DoublePredicate valuePredicate) {
        this.value = value;
        this.valuePredicate = valuePredicate;
    }

    public static Factory factory(DoublePredicate valuePredicate) {
        return new Factory(valuePredicate);
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
            if (!this.valuePredicate.fromSublevel(subLevel, (Vector3dc)sourcePos, this.value)) continue;
            filtered.add(subLevel);
        }
        return filtered;
    }

    @FunctionalInterface
    public static interface DoublePredicate {
        public boolean fromSublevel(ServerSubLevel var1, Vector3dc var2, double var3);
    }

    public static class Factory {
        private final DoublePredicate doublePredicate;

        public Factory(DoublePredicate doublePredicate) {
            this.doublePredicate = doublePredicate;
        }

        public SubLevelDoubleFilter create(double value) {
            return new SubLevelDoubleFilter(value, this.doublePredicate);
        }
    }
}
