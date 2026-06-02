/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform$Sided
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.blocks.torsion_spring;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.simulated_team.simulated.content.blocks.torsion_spring.TorsionSpringBlock;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public static class TorsionSpringBlockEntity.TorsionSpringValueBox
extends ValueBoxTransform.Sided {
    protected Vec3 getSouthLocation() {
        return VecHelper.voxelSpace((double)8.0, (double)8.0, (double)15.5);
    }

    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        return super.getLocalOffset(level, pos, state).add(Vec3.atLowerCornerOf((Vec3i)((Direction)state.getValue((Property)TorsionSpringBlock.FACING)).getNormal()).scale(-0.3125));
    }

    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        if (!this.getSide().getAxis().isHorizontal()) {
            TransformStack.of((PoseStack)ms).rotateY((AngleHelper.horizontalAngle((Direction)((Direction)state.getValue((Property)TorsionSpringBlock.FACING))) + 180.0f) * (float)Math.PI / 180.0f);
        }
        super.rotate(level, pos, state, ms);
    }

    public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
        Vec3 offset = this.getLocalOffset(level, pos, state);
        if (offset == null) {
            return false;
        }
        return localHit.distanceTo(offset) < (double)(this.scale / 1.5f);
    }

    protected boolean isSideActive(BlockState state, Direction direction) {
        return direction.getAxis() != ((Direction)state.getValue((Property)TorsionSpringBlock.FACING)).getAxis();
    }
}
