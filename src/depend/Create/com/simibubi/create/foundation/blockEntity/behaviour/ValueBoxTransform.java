/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.simibubi.create.foundation.blockEntity.behaviour;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.function.Function;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

public abstract class ValueBoxTransform {
    protected float scale = this.getScale();

    public abstract Vec3 getLocalOffset(LevelAccessor var1, BlockPos var2, BlockState var3);

    public abstract void rotate(LevelAccessor var1, BlockPos var2, BlockState var3, PoseStack var4);

    public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
        Vec3 offset = this.getLocalOffset(level, pos, state);
        if (offset == null) {
            return false;
        }
        return localHit.distanceTo(offset) < (double)(this.scale / 2.0f);
    }

    public void transform(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        Vec3 position = this.getLocalOffset(level, pos, state);
        if (position == null) {
            return;
        }
        ms.translate(position.x, position.y, position.z);
        this.rotate(level, pos, state, ms);
        ms.scale(this.scale, this.scale, this.scale);
    }

    public boolean shouldRender(LevelAccessor level, BlockPos pos, BlockState state) {
        return !state.isAir() && this.getLocalOffset(level, pos, state) != null;
    }

    public int getOverrideColor() {
        return -1;
    }

    protected Vec3 rotateHorizontally(BlockState state, Vec3 vec) {
        float yRot = 0.0f;
        if (state.hasProperty((Property)BlockStateProperties.FACING)) {
            yRot = AngleHelper.horizontalAngle((Direction)((Direction)state.getValue((Property)BlockStateProperties.FACING)));
        }
        if (state.hasProperty((Property)BlockStateProperties.HORIZONTAL_FACING)) {
            yRot = AngleHelper.horizontalAngle((Direction)((Direction)state.getValue((Property)BlockStateProperties.HORIZONTAL_FACING)));
        }
        return VecHelper.rotateCentered((Vec3)vec, (double)yRot, (Direction.Axis)Direction.Axis.Y);
    }

    public float getScale() {
        return 0.5f;
    }

    public float getFontScale() {
        return 0.015625f;
    }

    public static abstract class Sided
    extends ValueBoxTransform {
        protected Direction direction = Direction.UP;

        public Sided fromSide(Direction direction) {
            this.direction = direction;
            return this;
        }

        @Override
        public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
            Vec3 location = this.getSouthLocation();
            location = VecHelper.rotateCentered((Vec3)location, (double)AngleHelper.horizontalAngle((Direction)this.getSide()), (Direction.Axis)Direction.Axis.Y);
            location = VecHelper.rotateCentered((Vec3)location, (double)AngleHelper.verticalAngle((Direction)this.getSide()), (Direction.Axis)Direction.Axis.X);
            return location;
        }

        protected abstract Vec3 getSouthLocation();

        @Override
        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
            float yRot = AngleHelper.horizontalAngle((Direction)this.getSide()) + 180.0f;
            float xRot = this.getSide() == Direction.UP ? 90.0f : (this.getSide() == Direction.DOWN ? 270.0f : 0.0f);
            ((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateYDegrees(yRot)).rotateXDegrees(xRot);
        }

        @Override
        public boolean shouldRender(LevelAccessor level, BlockPos pos, BlockState state) {
            return super.shouldRender(level, pos, state) && this.isSideActive(state, this.getSide());
        }

        @Override
        public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
            return this.isSideActive(state, this.getSide()) && super.testHit(level, pos, state, localHit);
        }

        protected boolean isSideActive(BlockState state, Direction direction) {
            return true;
        }

        public Direction getSide() {
            return this.direction;
        }
    }

    public static abstract class Dual
    extends ValueBoxTransform {
        protected boolean first;

        public Dual(boolean first) {
            this.first = first;
        }

        public boolean isFirst() {
            return this.first;
        }

        public static Pair<ValueBoxTransform, ValueBoxTransform> makeSlots(Function<Boolean, ? extends Dual> factory) {
            return Pair.of((Object)factory.apply(true), (Object)factory.apply(false));
        }

        @Override
        public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
            Vec3 offset = this.getLocalOffset(level, pos, state);
            if (offset == null) {
                return false;
            }
            return localHit.distanceTo(offset) < (double)(this.scale / 3.5f);
        }
    }
}
