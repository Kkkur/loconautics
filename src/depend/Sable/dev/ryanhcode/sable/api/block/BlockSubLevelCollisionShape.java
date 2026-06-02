/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package dev.ryanhcode.sable.api.block;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface BlockSubLevelCollisionShape {
    public VoxelShape getSubLevelCollisionShape(BlockGetter var1, BlockState var2);
}
