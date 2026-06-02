/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.content.contraptions.DirectionalExtenderScrollOptionSlot
 *  com.simibubi.create.content.kinetics.base.IRotate
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform$Sided
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.steam_vent;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.DirectionalExtenderScrollOptionSlot;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public static class SteamVentBlockEntity.SteamVentValueBoxTransform
extends ValueBoxTransform.Sided {
    BlockEntity be;

    public ValueBoxTransform.Sided fromSide(Direction direction) {
        this.direction = direction;
        if (direction == Direction.UP) {
            Minecraft mc = Minecraft.getInstance();
            HitResult target = mc.hitResult;
            if (target instanceof BlockHitResult) {
                Vec3 hit = target.getLocation();
                Vec3 localHit = hit.subtract(Vec3.atCenterOf((Vec3i)this.be.getBlockPos()));
                if (localHit.y < 0.4) {
                    this.direction = Direction.getNearest((double)localHit.x, (double)0.0, (double)localHit.z);
                }
            }
        }
        return this;
    }

    protected Vec3 getSouthLocation() {
        return VecHelper.voxelSpace((double)8.0, (double)8.0, (double)12.0);
    }

    public float getScale() {
        return 0.45f;
    }

    protected ValueBoxTransform getMovementModeSlot() {
        return new DirectionalExtenderScrollOptionSlot((state, d) -> {
            Direction.Axis axis = d.getAxis();
            Direction.Axis shaftAxis = ((IRotate)state.getBlock()).getRotationAxis(state);
            return shaftAxis != axis;
        });
    }

    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        float yRot = AngleHelper.horizontalAngle((Direction)this.getSide()) + 180.0f;
        float xRot = this.getSide() == Direction.UP ? 90.0f : (this.getSide() == Direction.DOWN ? 270.0f : 0.0f);
        ((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateYDegrees(yRot)).rotateXDegrees(xRot += 22.5f);
    }

    protected boolean isSideActive(BlockState state, Direction direction) {
        if (direction == Direction.UP) {
            Minecraft mc = Minecraft.getInstance();
            HitResult target = mc.hitResult;
            if (target instanceof BlockHitResult) {
                Vec3 hit = target.getLocation();
                Vec3 localHit = hit.subtract(Vec3.atCenterOf((Vec3i)this.be.getBlockPos()));
                return localHit.y < 0.4;
            }
        }
        return true;
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
