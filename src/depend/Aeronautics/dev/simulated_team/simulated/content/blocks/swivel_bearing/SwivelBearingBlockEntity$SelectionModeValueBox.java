/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.blocks.swivel_bearing;

import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import dev.simulated_team.simulated.content.blocks.swivel_bearing.SwivelBearingBlock;
import java.util.function.BiPredicate;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

private static class SwivelBearingBlockEntity.SelectionModeValueBox
extends CenteredSideValueBoxTransform {
    public SwivelBearingBlockEntity.SelectionModeValueBox(BiPredicate<BlockState, Direction> allowedDirections) {
        super(allowedDirections);
    }

    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        return super.getLocalOffset(level, pos, state).subtract(Vec3.atLowerCornerOf((Vec3i)((Direction)state.getValue((Property)SwivelBearingBlock.FACING)).getNormal()).scale(0.3125));
    }

    protected Vec3 getSouthLocation() {
        return VecHelper.voxelSpace((double)8.0, (double)8.0, (double)15.75);
    }

    public float getScale() {
        return 0.35f;
    }
}
