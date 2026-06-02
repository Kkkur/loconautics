/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  it.unimi.dsi.fastutil.Function
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 */
package dev.ryanhcode.sable.mixin.clip_overwrite;

import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

class ClientLevelMixin.1
extends ObjectArrayList<Function<SubLevel, Pose3dc>> {
    ClientLevelMixin.1() {
        this.add(subLevel -> ((SubLevel)subLevel).logicalPose());
    }
}
