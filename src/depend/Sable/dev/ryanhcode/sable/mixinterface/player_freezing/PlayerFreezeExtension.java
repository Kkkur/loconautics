/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.mixinterface.player_freezing;

import java.util.UUID;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3dc;

public interface PlayerFreezeExtension {
    @Nullable
    public UUID sable$getFrozenToSubLevel();

    @Nullable
    public Vector3dc sable$getFrozenToSubLevelAnchor();

    public void sable$tickStopFreezing();

    public void sable$freezeTo(UUID var1, Vector3dc var2);

    public void sable$teleport();
}
