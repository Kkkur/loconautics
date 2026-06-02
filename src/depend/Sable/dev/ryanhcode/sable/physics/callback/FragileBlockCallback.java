/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.block.IceBlock
 *  net.minecraft.world.level.block.LeavesBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.joml.Vector3d
 */
package dev.ryanhcode.sable.physics.callback;

import dev.ryanhcode.sable.api.physics.callback.BlockSubLevelCollisionCallback;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.mixinterface.block_properties.BlockStateExtension;
import dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertyTypes;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Vector3d;

public class FragileBlockCallback
implements BlockSubLevelCollisionCallback {
    public static final FragileBlockCallback INSTANCE = new FragileBlockCallback();

    protected FragileBlockCallback() {
    }

    public double getTriggerVelocity() {
        return 4.0;
    }

    @Override
    public BlockSubLevelCollisionCallback.CollisionResult sable$onCollision(BlockPos pos, Vector3d pos1, double impactVelocity) {
        double triggerVelocity = this.getTriggerVelocity();
        if (impactVelocity * impactVelocity < triggerVelocity * triggerVelocity) {
            return BlockSubLevelCollisionCallback.CollisionResult.NONE;
        }
        SubLevelPhysicsSystem system = SubLevelPhysicsSystem.getCurrentlySteppingSystem();
        ServerLevel level = system.getLevel();
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof LeavesBlock && ((Boolean)state.getValue((Property)LeavesBlock.PERSISTENT)).booleanValue()) {
            return BlockSubLevelCollisionCallback.CollisionResult.NONE;
        }
        if (this.shouldTriggerFor(state)) {
            return this.onHit(level, pos, state, pos1);
        }
        return new BlockSubLevelCollisionCallback.CollisionResult(JOMLConversion.ZERO, true);
    }

    public boolean shouldTriggerFor(BlockState state) {
        return (Boolean)((BlockStateExtension)state).sable$getProperty((PhysicsBlockPropertyTypes.PhysicsBlockPropertyType)PhysicsBlockPropertyTypes.FRAGILE.get());
    }

    public BlockSubLevelCollisionCallback.CollisionResult onHit(ServerLevel level, BlockPos pos, BlockState state, Vector3d hitPos) {
        BlockState belowState;
        level.destroyBlock(pos, true);
        if (state.getBlock() instanceof IceBlock && ((belowState = level.getBlockState(pos.below())).blocksMotion() || belowState.liquid())) {
            level.setBlockAndUpdate(pos, IceBlock.meltsInto());
        }
        return new BlockSubLevelCollisionCallback.CollisionResult(JOMLConversion.ZERO, true);
    }
}
