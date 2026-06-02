/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3d
 */
package dev.ryanhcode.sable.command.argument.modifier_type;

import dev.ryanhcode.sable.command.argument.SubLevelSelectorModifierType;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public class SubLevelSortModifier
implements SubLevelSelectorModifierType.Modifier {
    private final String filtering;

    public SubLevelSortModifier(String filtering) {
        this.filtering = filtering;
    }

    @Override
    public int getMaxResults() {
        return Integer.MAX_VALUE;
    }

    @Override
    @Nullable
    public List<ServerSubLevel> apply(List<ServerSubLevel> selected, Vector3d sourcePos) {
        Map<SubLevel, Double> distances = selected.stream().collect(Collectors.toMap(subLevel -> subLevel, subLevel -> subLevel.logicalPose().position().distanceSquared(sourcePos.x, sourcePos.y, sourcePos.z)));
        if (this.filtering.equals("nearest")) {
            selected.sort(Comparator.comparingDouble(distances::get));
        } else if (this.filtering.equals("furthest")) {
            selected.sort(Comparator.comparingDouble(subLevel -> -((Double)distances.get(subLevel)).doubleValue()));
        }
        return selected;
    }
}
