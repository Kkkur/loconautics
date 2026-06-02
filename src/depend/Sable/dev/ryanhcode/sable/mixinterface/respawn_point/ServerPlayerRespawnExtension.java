/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.Pair
 *  net.minecraft.server.level.ServerPlayer
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3d
 */
package dev.ryanhcode.sable.mixinterface.respawn_point;

import it.unimi.dsi.fastutil.Pair;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public interface ServerPlayerRespawnExtension {
    @Nullable
    public UUID sable$getRespawnPoint();

    public void sable$takeQueuedFreezeFrom(ServerPlayer var1);

    @Nullable
    public Pair<UUID, Vector3d> sable$getQueuedFreeze();
}
