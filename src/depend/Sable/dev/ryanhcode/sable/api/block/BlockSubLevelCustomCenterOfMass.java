/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.api.block;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3dc;

public interface BlockSubLevelCustomCenterOfMass {
    public Vector3dc getCenterOfMass(BlockGetter var1, BlockState var2);
}
