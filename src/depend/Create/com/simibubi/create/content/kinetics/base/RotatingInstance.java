/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.instance.InstanceHandle
 *  dev.engine_room.flywheel.api.instance.InstanceType
 *  dev.engine_room.flywheel.lib.instance.ColoredLitOverlayInstance
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.state.BlockState
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package com.simibubi.create.content.kinetics.base;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import dev.engine_room.flywheel.api.instance.InstanceHandle;
import dev.engine_room.flywheel.api.instance.InstanceType;
import dev.engine_room.flywheel.lib.instance.ColoredLitOverlayInstance;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RotatingInstance
extends ColoredLitOverlayInstance {
    public static final float SPEED_MULTIPLIER = 6.0f;
    public byte rotationAxisX;
    public byte rotationAxisY;
    public byte rotationAxisZ;
    public float x;
    public float y;
    public float z;
    public float rotationalSpeed;
    public float rotationOffset;
    public final Quaternionf rotation = new Quaternionf();

    public RotatingInstance(InstanceType<? extends RotatingInstance> type, InstanceHandle handle) {
        super(type, handle);
    }

    public static int colorFromBE(KineticBlockEntity be) {
        if (be.hasNetwork()) {
            return Color.generateFromLong((long)be.network).getRGB();
        }
        return 0xFFFFFF;
    }

    public RotatingInstance setup(KineticBlockEntity blockEntity) {
        BlockState blockState = blockEntity.getBlockState();
        Direction.Axis axis = KineticBlockEntityVisual.rotationAxis(blockState);
        return this.setup(blockEntity, axis, blockEntity.getSpeed());
    }

    public RotatingInstance setup(KineticBlockEntity blockEntity, Direction.Axis axis) {
        return this.setup(blockEntity, axis, blockEntity.getSpeed());
    }

    public RotatingInstance setup(KineticBlockEntity blockEntity, float speed) {
        BlockState blockState = blockEntity.getBlockState();
        Direction.Axis axis = KineticBlockEntityVisual.rotationAxis(blockState);
        return this.setup(blockEntity, axis, speed);
    }

    public RotatingInstance setup(KineticBlockEntity blockEntity, Direction.Axis axis, float speed) {
        BlockState blockState = blockEntity.getBlockState();
        BlockPos pos = blockEntity.getBlockPos();
        return this.setRotationAxis(axis).setRotationalSpeed(speed * 6.0f).setRotationOffset(KineticBlockEntityVisual.rotationOffset(blockState, axis, (Vec3i)pos) + (float)blockEntity.getRotationAngleOffset(axis));
    }

    public RotatingInstance rotateToFace(Direction.Axis axis) {
        Direction orientation = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis);
        return this.rotateToFace(orientation);
    }

    public RotatingInstance rotateToFace(Direction from, Direction.Axis axis) {
        Direction orientation = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis);
        return this.rotateToFace(from, orientation);
    }

    public RotatingInstance rotateToFace(Direction orientation) {
        return this.rotateToFace(orientation.getStepX(), orientation.getStepY(), orientation.getStepZ());
    }

    public RotatingInstance rotateToFace(Direction from, Direction orientation) {
        return this.rotateTo(from.getStepX(), from.getStepY(), from.getStepZ(), orientation.getStepX(), orientation.getStepY(), orientation.getStepZ());
    }

    public RotatingInstance rotateToFace(float stepX, float stepY, float stepZ) {
        return this.rotateTo(0.0f, 1.0f, 0.0f, stepX, stepY, stepZ);
    }

    public RotatingInstance rotateTo(float fromX, float fromY, float fromZ, float toX, float toY, float toZ) {
        this.rotation.rotateTo(fromX, fromY, fromZ, toX, toY, toZ);
        return this;
    }

    public RotatingInstance setRotationAxis(Direction.Axis axis) {
        Direction orientation = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis);
        return this.setRotationAxis(orientation.step());
    }

    public RotatingInstance setRotationAxis(Vector3f axis) {
        return this.setRotationAxis(axis.x(), axis.y(), axis.z());
    }

    public RotatingInstance setRotationAxis(float rotationAxisX, float rotationAxisY, float rotationAxisZ) {
        this.rotationAxisX = (byte)(rotationAxisX * 127.0f);
        this.rotationAxisY = (byte)(rotationAxisY * 127.0f);
        this.rotationAxisZ = (byte)(rotationAxisZ * 127.0f);
        return this;
    }

    public RotatingInstance setPosition(Vec3i pos) {
        return this.setPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    public RotatingInstance setPosition(Vector3f pos) {
        return this.setPosition(pos.x(), pos.y(), pos.z());
    }

    public RotatingInstance setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public RotatingInstance nudge(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public RotatingInstance setColor(KineticBlockEntity blockEntity) {
        this.colorRgb(RotatingInstance.colorFromBE(blockEntity));
        return this;
    }

    public RotatingInstance setColor(Color c) {
        this.color(c.getRed(), c.getGreen(), c.getBlue());
        return this;
    }

    public RotatingInstance setRotationalSpeed(float rotationalSpeed) {
        this.rotationalSpeed = rotationalSpeed;
        return this;
    }

    public RotatingInstance setRotationOffset(float rotationOffset) {
        this.rotationOffset = rotationOffset;
        return this;
    }
}
