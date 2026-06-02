/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectIterable
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Quaterniondc
 */
package dev.ryanhcode.sable.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniondc;

public static enum SableMathUtils.GridQuats implements ObjectIterable<Quaterniondc>
{
    ALL(2047),
    X_AXIS(19),
    Y_AXIS(37),
    Z_AXIS(73),
    REAL(1137);

    private final ObjectList<Quaterniondc> currentQuats = new ObjectArrayList(ALL_QUATS.length);
    private final ObjectList<Quaterniondc> oppositeQuats = new ObjectArrayList(ALL_QUATS.length);

    private SableMathUtils.GridQuats(int bitPattern) {
        for (Quaterniondc q : ALL_QUATS) {
            ((bitPattern & 1) > 0 ? this.currentQuats : this.oppositeQuats).add((Object)q);
            bitPattern >>= 1;
        }
    }

    public ObjectIterable<Quaterniondc> opposite() {
        return () -> this.oppositeQuats.iterator();
    }

    @NotNull
    public ObjectIterator<Quaterniondc> iterator() {
        return this.currentQuats.iterator();
    }
}
