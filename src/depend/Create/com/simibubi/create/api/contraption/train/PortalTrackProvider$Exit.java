/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.BlockFace
 *  net.minecraft.server.level.ServerLevel
 */
package com.simibubi.create.api.contraption.train;

import net.createmod.catnip.math.BlockFace;
import net.minecraft.server.level.ServerLevel;

public record PortalTrackProvider.Exit(ServerLevel level, BlockFace face) {
}
