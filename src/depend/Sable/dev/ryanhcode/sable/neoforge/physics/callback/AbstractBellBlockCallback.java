/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.equipment.bell.AbstractBellBlock
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BellAttachType
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 */
package dev.ryanhcode.sable.neoforge.physics.callback;

import com.simibubi.create.content.equipment.bell.AbstractBellBlock;
import dev.ryanhcode.sable.api.physics.callback.BlockSubLevelCollisionCallback;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.neoforge.mixin.compatibility.create.impact.AbstractBellBlockAccessor;
import dev.ryanhcode.sable.physics.callback.FragileBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class AbstractBellBlockCallback
extends FragileBlockCallback {
    public static final AbstractBellBlockCallback INSTANCE = new AbstractBellBlockCallback();

    @Override
    public boolean shouldTriggerFor(BlockState state) {
        return state.getBlock() instanceof AbstractBellBlock;
    }

    @Override
    public BlockSubLevelCollisionCallback.CollisionResult onHit(ServerLevel level, BlockPos pos, BlockState state, Vector3d hitPos) {
        Vec3 hitDir = pos.getCenter().subtract(hitPos.x, hitPos.y, hitPos.z);
        Direction facing = (Direction)state.getValue((Property)AbstractBellBlock.FACING);
        BellAttachType attachment = (BellAttachType)state.getValue((Property)AbstractBellBlock.ATTACHMENT);
        int xMul = Math.abs(facing.getStepX());
        int zMul = Math.abs(facing.getStepZ());
        if (attachment == BellAttachType.CEILING) {
            xMul = 1;
            zMul = 1;
        }
        Direction ringDir = Direction.getNearest((double)(hitDir.x * (double)xMul), (double)0.0, (double)(hitDir.z * (double)zMul)).getOpposite();
        AbstractBellBlock block = (AbstractBellBlock)state.getBlock();
        if (block.canRingFrom(state, ringDir, 0.0)) {
            ((AbstractBellBlockAccessor)block).invokeRing((Level)level, pos, ringDir, null);
        }
        return new BlockSubLevelCollisionCallback.CollisionResult(JOMLConversion.ZERO, false);
    }
}
