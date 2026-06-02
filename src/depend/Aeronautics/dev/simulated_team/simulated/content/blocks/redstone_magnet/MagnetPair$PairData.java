/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.blocks.redstone_magnet;

import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.simulated_team.simulated.content.blocks.redstone_magnet.SimMagnet;
import org.joml.Vector3dc;

protected record MagnetPair.PairData(Vector3dc relativePosition, Vector3dc moment1, Vector3dc moment2, SimMagnet magnet1, SimMagnet magnet2, ServerSubLevel body1, ServerSubLevel body2, Vector3dc magnet1Pos, Vector3dc magnet2Pos, double distance, double forceScale, double torqueScale) {
}
