/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.ryanhcode.sable.api.block;

import dev.ryanhcode.sable.api.physics.callback.BlockSubLevelCollisionCallback;
import dev.ryanhcode.sable.mixinterface.block_properties.BlockStateExtension;
import dev.ryanhcode.sable.physics.callback.FragileBlockCallback;
import dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertyTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockWithSubLevelCollisionCallback {
    public static BlockSubLevelCollisionCallback sable$getCallback(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof BlockWithSubLevelCollisionCallback) {
            BlockWithSubLevelCollisionCallback blockCollisionCallback = (BlockWithSubLevelCollisionCallback)block;
            return blockCollisionCallback.sable$getCallback();
        }
        if (((Boolean)((BlockStateExtension)state).sable$getProperty((PhysicsBlockPropertyTypes.PhysicsBlockPropertyType)PhysicsBlockPropertyTypes.FRAGILE.get())).booleanValue()) {
            return FragileBlockCallback.INSTANCE;
        }
        return null;
    }

    public static boolean hasCallback(BlockState state) {
        return BlockWithSubLevelCollisionCallback.sable$getCallback(state) != null;
    }

    public BlockSubLevelCollisionCallback sable$getCallback();
}
