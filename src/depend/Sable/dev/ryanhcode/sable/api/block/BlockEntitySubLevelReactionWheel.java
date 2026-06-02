/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.state.BlockState
 *  org.joml.Vector3d
 */
package dev.ryanhcode.sable.api.block;

import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;

public interface BlockEntitySubLevelReactionWheel {
    public void sable$getAngularVelocity(Vector3d var1);

    public BlockState getBlockState();
}
