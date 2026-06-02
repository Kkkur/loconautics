/*
 * Decompiled with CFR 0.152.
 */
package dev.ryanhcode.sable.api.sublevel;

import java.util.UUID;

public interface SubLevelTrackingPlugin {
    public Iterable<UUID> neededPlayers();

    public void sendTrackingData(int var1);
}
