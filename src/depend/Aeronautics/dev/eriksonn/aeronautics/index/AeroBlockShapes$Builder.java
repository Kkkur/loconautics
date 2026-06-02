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
package dev.eriksonn.aeronautics.index;

import dev.eriksonn.aeronautics.index.AeroBlockShapes;
import java.util.function.BiFunction;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public static class AeroBlockShapes.Builder {
    private VoxelShape shape;

    public AeroBlockShapes.Builder(VoxelShape shape) {
        this.shape = shape;
    }

    public AeroBlockShapes.Builder add(VoxelShape shape) {
        this.shape = Shapes.or((VoxelShape)this.shape, (VoxelShape)shape);
        return this;
    }

    public AeroBlockShapes.Builder add(double x1, double y1, double z1, double x2, double y2, double z2) {
        return this.add(AeroBlockShapes.cuboid(x1, y1, z1, x2, y2, z2));
    }

    public AeroBlockShapes.Builder erase(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.shape = Shapes.join((VoxelShape)this.shape, (VoxelShape)AeroBlockShapes.cuboid(x1, y1, z1, x2, y2, z2), (BooleanOp)BooleanOp.ONLY_FIRST);
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

    public VoxelShaper forAxis() {
        return this.build(VoxelShaper::forAxis, Direction.Axis.Y);
    }

    public VoxelShaper forHorizontalAxis() {
        return this.build(VoxelShaper::forHorizontalAxis, Direction.Axis.Z);
    }

    public VoxelShaper forHorizontal(Direction direction) {
        return this.build(VoxelShaper::forHorizontal, direction);
    }

    public VoxelShaper forDirectional(Direction direction) {
        return this.build(VoxelShaper::forDirectional, direction);
    }

    public VoxelShaper forDirectional() {
        return this.forDirectional(Direction.UP);
    }
}
