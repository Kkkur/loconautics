/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3ic
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix3d
 *  org.joml.Matrix3dc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.api.physics.mass;

import dev.ryanhcode.sable.api.block.BlockSubLevelCustomCenterOfMass;
import dev.ryanhcode.sable.api.physics.mass.MassData;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.physics.chunk.VoxelNeighborhoodState;
import dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertyHelper;
import dev.ryanhcode.sable.util.SableMathUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3d;
import org.joml.Matrix3dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class MassTracker
implements MassData {
    private static final AABB UNIT_BOUNDS = new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    public static BiFunction<BlockGetter, BlockState, Vector3dc> BLOCK_CENTER_OF_MASS = new BiFunction<BlockGetter, BlockState, Vector3dc>(){
        private final Int2ObjectOpenHashMap<Vector3dc> cache = new Int2ObjectOpenHashMap();

        @Override
        public Vector3dc apply(BlockGetter blockGetter, BlockState state) {
            return (Vector3dc)this.cache.computeIfAbsent(state.hashCode(), x -> {
                if (state.isAir()) {
                    return JOMLConversion.HALF;
                }
                Block patt0$temp = state.getBlock();
                if (patt0$temp instanceof BlockSubLevelCustomCenterOfMass) {
                    BlockSubLevelCustomCenterOfMass customCenterOfMass = (BlockSubLevelCustomCenterOfMass)patt0$temp;
                    return customCenterOfMass.getCenterOfMass(blockGetter, state);
                }
                VoxelShape shape = state.getCollisionShape(blockGetter, BlockPos.ZERO);
                if (shape.isEmpty()) {
                    return JOMLConversion.HALF;
                }
                if (state.isCollisionShapeFullBlock(blockGetter, BlockPos.ZERO)) {
                    return JOMLConversion.HALF;
                }
                AABB bounds = shape.bounds().intersect(UNIT_BOUNDS);
                return JOMLConversion.toJOML((Position)bounds.getCenter());
            });
        }
    };
    private static final Matrix3d BLOCK_INERTIA = new Matrix3d();
    private double mass = 0.0;
    private Matrix3d inertiaTensor = new Matrix3d().zero();
    private double inverseMass;
    private Matrix3d inverseInertiaTensor = new Matrix3d().zero();
    @Nullable
    private Vector3d centerOfMass = null;

    public static MassTracker build(BlockGetter blockGetter, BoundingBox3ic bounds) {
        double blockMass;
        BlockState state;
        int z;
        int y;
        int x;
        double mass = 0.0;
        Vector3d centerOfMass = new Vector3d();
        Matrix3d inertiaTensor = new Matrix3d().zero();
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        Vector3d blockCenter = new Vector3d();
        int blockCount = 0;
        for (x = bounds.minX(); x <= bounds.maxX(); ++x) {
            for (y = bounds.minY(); y <= bounds.maxY(); ++y) {
                for (z = bounds.minZ(); z <= bounds.maxZ(); ++z) {
                    state = blockGetter.getBlockState((BlockPos)blockPos.set(x, y, z));
                    if (!VoxelNeighborhoodState.isSolid(blockGetter, (BlockPos)blockPos, state)) continue;
                    blockMass = PhysicsBlockPropertyHelper.getMass(blockGetter, (BlockPos)blockPos, state);
                    blockCenter.set((double)x, (double)y, (double)z).add(BLOCK_CENTER_OF_MASS.apply(blockGetter, state));
                    mass += blockMass;
                    centerOfMass.fma(blockMass, (Vector3dc)blockCenter);
                    ++blockCount;
                }
            }
        }
        if (blockCount == 0) {
            MassTracker tracker = new MassTracker();
            tracker.mass = 0.0;
            tracker.centerOfMass = null;
            tracker.inertiaTensor = new Matrix3d().zero();
            tracker.inverseInertiaTensor = new Matrix3d().zero();
            return tracker;
        }
        centerOfMass.div(mass);
        for (x = bounds.minX(); x <= bounds.maxX(); ++x) {
            for (y = bounds.minY(); y <= bounds.maxY(); ++y) {
                for (z = bounds.minZ(); z <= bounds.maxZ(); ++z) {
                    state = blockGetter.getBlockState((BlockPos)blockPos.set(x, y, z));
                    if (!VoxelNeighborhoodState.isSolid(blockGetter, (BlockPos)blockPos, state)) continue;
                    blockCenter.set((double)x, (double)y, (double)z).add(BLOCK_CENTER_OF_MASS.apply(blockGetter, state));
                    blockMass = PhysicsBlockPropertyHelper.getMass(blockGetter, (BlockPos)blockPos, state);
                    Vec3 blockInertia = PhysicsBlockPropertyHelper.getInertia(blockGetter, (BlockPos)blockPos, state);
                    Vector3d r = blockCenter.sub((Vector3dc)centerOfMass);
                    MassTracker.addBlockInertia(r, blockMass, inertiaTensor, blockInertia);
                }
            }
        }
        Matrix3d inverseInertiaTensor = new Matrix3d((Matrix3dc)inertiaTensor).invert();
        double inverseMass = 1.0 / mass;
        MassTracker tracker = new MassTracker();
        tracker.centerOfMass = centerOfMass;
        tracker.mass = mass;
        tracker.inverseMass = inverseMass;
        tracker.inertiaTensor = inertiaTensor;
        tracker.inverseInertiaTensor = inverseInertiaTensor;
        return tracker;
    }

    private static Matrix3d addBlockInertia(Vector3d blockPos, double blockMass, Matrix3d dest, @Nullable Vec3 blockInertia) {
        if (blockInertia == null) {
            BLOCK_INERTIA.identity().scale(blockMass / 6.0);
        } else {
            BLOCK_INERTIA.identity();
            MassTracker.BLOCK_INERTIA.m00 = blockInertia.x * blockMass;
            MassTracker.BLOCK_INERTIA.m11 = blockInertia.y * blockMass;
            MassTracker.BLOCK_INERTIA.m22 = blockInertia.z * blockMass;
        }
        dest.add((Matrix3dc)BLOCK_INERTIA);
        SableMathUtils.fmaInertiaTensor((Vector3dc)blockPos, blockMass, dest);
        return dest;
    }

    public void addBlockMass(BlockGetter blockGetter, BlockState state, BlockPos blockPos, double blockMass, @Nullable Vec3 blockInertia) {
        double oldMass = this.mass;
        double newMass = oldMass + blockMass;
        Vector3d blockCenter = new Vector3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()).add(BLOCK_CENTER_OF_MASS.apply(blockGetter, state));
        if (this.centerOfMass == null) {
            this.centerOfMass = new Vector3d((Vector3dc)blockCenter);
        }
        Vector3d blockCenterFromCOM = new Vector3d((Vector3dc)blockCenter).sub((Vector3dc)this.centerOfMass);
        MassTracker.addBlockInertia(blockCenterFromCOM, blockMass, this.inertiaTensor, blockInertia);
        this.mass = newMass;
        this.inverseMass = 1.0 / newMass;
        this.moveCenterOfMass(new Vector3d((Vector3dc)this.centerOfMass).mul(oldMass).add((Vector3dc)blockCenter.mul(blockMass)).div(newMass));
    }

    public void moveCenterOfMass(Vector3d newCenterOfMass) {
        Vector3d diff = new Vector3d((Vector3dc)newCenterOfMass).sub((Vector3dc)this.centerOfMass);
        Matrix3d outerProduct = new Matrix3d(diff.x * diff.x, diff.y * diff.x, diff.z * diff.x, diff.x * diff.y, diff.y * diff.y, diff.z * diff.y, diff.x * diff.z, diff.y * diff.z, diff.z * diff.z);
        Matrix3d inertia = new Matrix3d().scale(diff.lengthSquared()).sub((Matrix3dc)outerProduct).scale(this.mass);
        this.inertiaTensor.sub((Matrix3dc)inertia);
        this.inverseInertiaTensor = new Matrix3d((Matrix3dc)this.inertiaTensor).invert();
        this.centerOfMass.set((Vector3dc)newCenterOfMass);
    }

    @Override
    public double getInverseMass() {
        return this.inverseMass;
    }

    @Override
    public Matrix3dc getInverseInertiaTensor() {
        return this.inverseInertiaTensor;
    }

    @Override
    public Matrix3dc getInertiaTensor() {
        return this.inertiaTensor;
    }

    @Override
    public double getMass() {
        return this.mass;
    }

    @Override
    public Vector3dc getCenterOfMass() {
        return this.centerOfMass;
    }
}
