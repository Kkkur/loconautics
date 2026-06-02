/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3d
 */
package dev.ryanhcode.sable.command.argument.modifier_type;

import dev.ryanhcode.sable.command.argument.SubLevelSelectorModifierType;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public class SubLevelLimitFilter
implements SubLevelSelectorModifierType.Modifier {
    private final int limit;

    public SubLevelLimitFilter(int limit) {
        this.limit = limit;
    }

    @Override
    public int getMaxResults() {
        return this.limit;
    }

    @Override
    @Nullable
    public List<ServerSubLevel> apply(List<ServerSubLevel> selected, Vector3d sourcePos) {
        if (selected.size() > this.limit) {
            return new ObjectArrayList(selected.subList(0, this.limit));
        }
        return selected;
    }
}
