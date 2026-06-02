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

public class SubLevelNameFilter
implements SubLevelSelectorModifierType.Modifier {
    private final String name;

    public SubLevelNameFilter(String name) {
        this.name = name;
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
            if (subLevel.getName() == null || !subLevel.getName().equals(this.name)) continue;
            filtered.add(subLevel);
        }
        return filtered;
    }
}
