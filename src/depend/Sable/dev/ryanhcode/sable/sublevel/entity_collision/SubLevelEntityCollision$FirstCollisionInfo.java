/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.state.BlockState
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.sublevel.entity_collision;

import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3dc;

public record SubLevelEntityCollision.FirstCollisionInfo(Vector3dc localLocation, Vector3dc globalDirection, boolean horizontal, boolean bouncy, BlockState block) {
}
