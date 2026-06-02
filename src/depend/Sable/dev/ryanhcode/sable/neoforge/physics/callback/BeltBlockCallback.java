/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.belt.BeltBlock
 *  com.simibubi.create.content.kinetics.belt.BeltBlockEntity
 *  com.simibubi.create.content.kinetics.belt.BeltSlope
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.neoforge.physics.callback;

import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import dev.ryanhcode.sable.api.physics.callback.BlockSubLevelCollisionCallback;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class BeltBlockCallback
implements BlockSubLevelCollisionCallback {
    public static BeltBlockCallback INSTANCE = new BeltBlockCallback();

    private BeltBlockCallback() {
    }

    @Override
    public BlockSubLevelCollisionCallback.CollisionResult sable$onCollision(BlockPos pos, Vector3d pos1, double impactVelocity) {
        SubLevelPhysicsSystem system = SubLevelPhysicsSystem.getCurrentlySteppingSystem();
        ServerLevel level = system.getLevel();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof BeltBlockEntity)) {
            return BlockSubLevelCollisionCallback.CollisionResult.NONE;
        }
        BeltBlockEntity belt = (BeltBlockEntity)blockEntity;
        BlockState state = belt.getBlockState();
        Direction facing = (Direction)state.getValue(BeltBlock.HORIZONTAL_FACING);
        BeltSlope slope = (BeltSlope)state.getValue(BeltBlock.SLOPE);
        if (slope == BeltSlope.SIDEWAYS) {
            return BlockSubLevelCollisionCallback.CollisionResult.NONE;
        }
        Vec3i normal = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)facing.getAxis()).getNormal();
        float speed = belt.getBeltMovementSpeed() * 20.0f;
        if (facing.getAxis() == Direction.Axis.X) {
            speed *= -1.0f;
        }
        Vector3d velocity = new Vector3d((double)((float)normal.getX() * speed), (double)((float)normal.getY() * speed), (double)((float)normal.getZ() * speed));
        if (slope == BeltSlope.HORIZONTAL && pos1.y - (double)belt.getBlockPos().getY() < 0.5) {
            velocity.negate();
        }
        return new BlockSubLevelCollisionCallback.CollisionResult((Vector3dc)velocity, false);
    }
}
