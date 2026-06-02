/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.command.argument.modifier_type;

import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import org.joml.Vector3dc;

@FunctionalInterface
public static interface SubLevelDoubleRangeFilter.DoubleGetter {
    public double fromSublevel(ServerSubLevel var1, Vector3dc var2);
}
