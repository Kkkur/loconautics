/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VoxelShaper
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.phys.shapes.BooleanOp
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create;

import com.simibubi.create.AllShapes;
import java.util.function.BiFunction;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public static class AllShapes.Builder {
    private VoxelShape shape;

    public AllShapes.Builder(VoxelShape shape) {
        this.shape = shape;
    }

    public AllShapes.Builder add(VoxelShape shape) {
        this.shape = Shapes.or((VoxelShape)this.shape, (VoxelShape)shape);
        return this;
    }

    public AllShapes.Builder add(double x1, double y1, double z1, double x2, double y2, double z2) {
        return this.add(AllShapes.cuboid(x1, y1, z1, x2, y2, z2));
    }

    public AllShapes.Builder erase(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.shape = Shapes.join((VoxelShape)this.shape, (VoxelShape)AllShapes.cuboid(x1, y1, z1, x2, y2, z2), (BooleanOp)BooleanOp.ONLY_FIRST);
        return this;
    }

    public VoxelShape build() {
        return this.shape;
    }

    public VoxelShaper build(BiFunction<VoxelShape, Direction, VoxelShaper> factory, Direction direction) {
        return factory.apply(this.shape, direction);
    }

    public VoxelShaper build(BiFunction<VoxelShape, Direction.Axis, VoxelShaper> factory, Direction.Axis axis) {
        return factory.apply(this.shape, axis);
    }

    public VoxelShaper forDirectional(Direction direction) {
        return this.build(VoxelShaper::forDirectional, direction);
    }

    public VoxelShaper forAxis() {
        return this.build(VoxelShaper::forAxis, Direction.Axis.Y);
    }

    public VoxelShaper forHorizontalAxis() {
        return this.build(VoxelShaper::forHorizontalAxis, Direction.Axis.Z);
    }

    public VoxelShaper forHorizontal(Direction direction) {
        return this.build(VoxelShaper::forHorizontal, direction);
    }

    public VoxelShaper forDirectional() {
        return this.forDirectional(Direction.UP);
    }
}
