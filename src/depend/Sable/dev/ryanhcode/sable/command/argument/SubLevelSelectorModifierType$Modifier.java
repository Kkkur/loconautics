/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3d
 */
package dev.ryanhcode.sable.command.argument;

import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public static interface SubLevelSelectorModifierType.Modifier {
    public int getMaxResults();

    @Nullable
    public List<ServerSubLevel> apply(List<ServerSubLevel> var1, Vector3d var2);
}
