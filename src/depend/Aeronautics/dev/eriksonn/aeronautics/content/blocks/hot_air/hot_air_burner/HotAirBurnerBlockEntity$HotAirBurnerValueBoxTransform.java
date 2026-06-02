/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform$Sided
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.hot_air_burner;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

private static class HotAirBurnerBlockEntity.HotAirBurnerValueBoxTransform
extends ValueBoxTransform.Sided {
    private HotAirBurnerBlockEntity.HotAirBurnerValueBoxTransform() {
    }

    protected Vec3 getSouthLocation() {
        return VecHelper.voxelSpace((double)8.0, (double)8.0, (double)14.0);
    }

    public float getScale() {
        return 0.45f;
    }

    protected boolean isSideActive(BlockState state, Direction direction) {
        return direction.getAxis() != Direction.Axis.Y || direction.equals((Object)Direction.DOWN);
    }

    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        if (this.getSide() == Direction.DOWN) {
            return VecHelper.voxelSpace((double)8.0, (double)0.0, (double)8.0);
        }
        Vec3 location = this.getSouthLocation();
        location = location.add(VecHelper.voxelSpace((double)0.0, (double)-3.0, (double)1.75));
        location = VecHelper.rotateCentered((Vec3)location, (double)AngleHelper.horizontalAngle((Direction)this.getSide()), (Direction.Axis)Direction.Axis.Y);
        return location;
    }
}
