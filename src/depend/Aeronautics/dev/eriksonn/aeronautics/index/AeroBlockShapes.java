/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VoxelShaper
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.phys.shapes.BooleanOp
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package dev.eriksonn.aeronautics.index;

import java.util.function.BiFunction;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AeroBlockShapes {
    public static final VoxelShaper STEAM_VENT = AeroBlockShapes.shape(1.0, 0.0, 1.0, 15.0, 3.0, 15.0).add(3.0, 2.0, 3.0, 13.0, 16.0, 13.0).forAxis();
    public static final VoxelShaper PROPELLER_BEARING = AeroBlockShapes.shape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0).erase(0.0, 0.0, 0.0, 5.0, 12.0, 5.0).erase(11.0, 0.0, 0.0, 16.0, 12.0, 5.0).erase(0.0, 0.0, 11.0, 5.0, 12.0, 16.0).erase(11.0, 0.0, 11.0, 16.0, 12.0, 16.0).forDirectional();
    public static final VoxelShaper PROPELLER = AeroBlockShapes.shape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0).add(4.0, 5.0, 4.0, 12.0, 12.0, 12.0).forDirectional();
    public static final VoxelShaper SMART_PROPELLER = AeroBlockShapes.shape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0).add(2.0, 4.0, 2.0, 14.0, 10.0, 14.0).forHorizontal(Direction.NORTH);
    public static final VoxelShaper SMART_PROPELLER_CEILING = AeroBlockShapes.shape(0.0, 12.0, 0.0, 16.0, 16.0, 16.0).add(2.0, 6.0, 2.0, 14.0, 12.0, 14.0).forHorizontal(Direction.NORTH);
    public static final VoxelShape HOT_AIR_BURNER = AeroBlockShapes.shape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0).add(3.0, 8.0, 3.0, 13.0, 16.0, 13.0).build();
    public static final VoxelShape HOT_AIR_BURNER_PLAYER_COLLISION = AeroBlockShapes.shape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0).add(3.0, 8.0, 3.0, 13.0, 15.99, 13.0).build();
    public static final VoxelShape HOT_AIR_BURNER_SMOKE_CLIP = AeroBlockShapes.shape(0.0, 0.0, 0.0, 16.0, 7.0, 16.0).build();
    public static final VoxelShape MOUNTED_POTATO_CANNON = AeroBlockShapes.shape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0).add(1.0, 8.0, 1.0, 15.0, 12.0, 15.0).add(4.0, 12.0, 4.0, 12.0, 28.0, 12.0).erase(5.0, 18.0, 5.0, 11.0, 28.0, 11.0).build();
    public static final VoxelShape MOUNTED_POTATO_CANNON_BLOCKED = AeroBlockShapes.shape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0).add(1.0, 8.0, 1.0, 15.0, 12.0, 15.0).build();

    private static Builder shape(VoxelShape shape) {
        return new Builder(shape);
    }

    private static Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
        return AeroBlockShapes.shape(AeroBlockShapes.cuboid(x1, y1, z1, x2, y2, z2));
    }

    private static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Block.box((double)x1, (double)y1, (double)z1, (double)x2, (double)y2, (double)z2);
    }

    public static class Builder {
        private VoxelShape shape;

        public Builder(VoxelShape shape) {
            this.shape = shape;
        }

        public Builder add(VoxelShape shape) {
            this.shape = Shapes.or((VoxelShape)this.shape, (VoxelShape)shape);
            return this;
        }

        public Builder add(double x1, double y1, double z1, double x2, double y2, double z2) {
            return this.add(AeroBlockShapes.cuboid(x1, y1, z1, x2, y2, z2));
        }

        public Builder erase(double x1, double y1, double z1, double x2, double y2, double z2) {
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
}
