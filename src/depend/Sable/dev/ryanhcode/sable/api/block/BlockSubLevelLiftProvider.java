/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.api.block;

import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public interface BlockSubLevelLiftProvider {
    public static final Direction[] DIRECTIONS = Direction.values();
    public static final Vector3d LIFT_FORCE = new Vector3d();
    public static final Vector3d LIFT_POS = new Vector3d();
    public static final Vector3d LIFT_NORMAL = new Vector3d();
    public static final Vector3d LIFT_VELO = new Vector3d();
    public static final Vector3d DRAG = new Vector3d();
    public static final Vector3d TEMP = new Vector3d();

    public static void resetVectors() {
        LIFT_VELO.set(0.0, 0.0, 0.0);
        LIFT_POS.set(0.0, 0.0, 0.0);
        LIFT_FORCE.set(0.0, 0.0, 0.0);
        LIFT_NORMAL.set(0.0, 0.0, 0.0);
        DRAG.set(0.0, 0.0, 0.0);
    }

    public static List<LiftProviderGroup> groupLiftProviders(Collection<LiftProviderContext> liftProviders) {
        ObjectArrayList groups = new ObjectArrayList();
        ObjectOpenHashSet positions = new ObjectOpenHashSet(liftProviders.size());
        for (LiftProviderContext liftProvider : liftProviders) {
            positions.add(liftProvider.pos);
        }
        while (!positions.isEmpty()) {
            ObjectOpenHashSet groupBlocks = new ObjectOpenHashSet();
            ObjectArrayList toVisit = new ObjectArrayList();
            toVisit.add((BlockPos)positions.iterator().next());
            while (!toVisit.isEmpty()) {
                BlockPos pos = (BlockPos)toVisit.removeLast();
                if (groupBlocks.contains(pos)) continue;
                groupBlocks.add(pos);
                positions.remove(pos);
                for (Direction direction : DIRECTIONS) {
                    BlockPos offsetPos = pos.relative(direction);
                    if (!positions.contains(offsetPos)) continue;
                    toVisit.add(offsetPos);
                }
            }
            groups.add(new LiftProviderGroup((Set<BlockPos>)groupBlocks));
        }
        return groups;
    }

    @NotNull
    public Direction sable$getNormal(BlockState var1);

    default public float sable$getParallelDragScalar() {
        return 0.75f;
    }

    default public float sable$getDirectionlessDragScalar() {
        return 0.068882026f;
    }

    default public float sable$getLiftScalar() {
        return 0.475f;
    }

    default public void sable$contributeLiftAndDrag(LiftProviderContext ctx, ServerSubLevel subLevel, @NotNull Pose3d localPose, double timeStep, Vector3dc linearVelocity, Vector3dc angularVelocity, Vector3d linearImpulse, Vector3d angularImpulse, @Nullable LiftProviderGroup group) {
        double dragStrength;
        BlockSubLevelLiftProvider.resetVectors();
        LIFT_NORMAL.set(ctx.dir.x(), ctx.dir.y(), ctx.dir.z());
        LIFT_POS.set((double)ctx.pos.getX() + 0.5, (double)ctx.pos.getY() + 0.5, (double)ctx.pos.getZ() + 0.5);
        if (localPose != null) {
            localPose.transformNormal(LIFT_NORMAL);
            localPose.transformPosition(LIFT_POS);
        }
        Pose3d pose = subLevel.logicalPose();
        double pressure = DimensionPhysicsData.getAirPressure((Level)subLevel.getLevel(), (Vector3dc)pose.transformPosition((Vector3dc)LIFT_POS, TEMP));
        pose.transformPosition((Vector3dc)LIFT_POS, TEMP).sub((Vector3dc)pose.position());
        LIFT_VELO.set(linearVelocity).add((Vector3dc)angularVelocity.cross((Vector3dc)TEMP, TEMP));
        pose.transformNormalInverse(LIFT_VELO);
        LIFT_FORCE.zero();
        if (this.sable$getParallelDragScalar() > 0.0f) {
            dragStrength = LIFT_NORMAL.dot((Vector3dc)LIFT_VELO) * (double)this.sable$getParallelDragScalar() * pressure * timeStep;
            Vector3d parallelDrag = LIFT_NORMAL.mul(dragStrength, DRAG);
            LIFT_FORCE.add((Vector3dc)parallelDrag);
            if (group != null) {
                group.totalDrag.sub((Vector3dc)parallelDrag);
                group.dragCenter.fma(Math.abs(dragStrength), (Vector3dc)LIFT_POS);
                group.totalDragStrength += Math.abs(dragStrength);
            }
        }
        if (this.sable$getDirectionlessDragScalar() > 0.0f) {
            dragStrength = (double)this.sable$getDirectionlessDragScalar() * pressure * timeStep;
            Vector3d directionlessDrag = LIFT_VELO.mul(dragStrength, TEMP);
            LIFT_FORCE.add((Vector3dc)directionlessDrag);
            if (group != null) {
                group.totalDrag.sub((Vector3dc)directionlessDrag);
                group.dragCenter.fma(directionlessDrag.length(), (Vector3dc)LIFT_POS);
                group.totalDragStrength += directionlessDrag.length();
            }
        }
        if (this.sable$getLiftScalar() > 0.0f) {
            double liftStrength = LIFT_VELO.sub((Vector3dc)DRAG, TEMP).length() * (double)this.sable$getLiftScalar() * pressure * timeStep;
            Vector3d lift = LIFT_NORMAL.mul(liftStrength, TEMP);
            LIFT_FORCE.add((Vector3dc)lift);
            if (group != null) {
                group.totalLift.sub((Vector3dc)lift);
                group.liftCenter.fma(Math.abs(liftStrength), (Vector3dc)LIFT_POS);
                group.totalLiftStrength += liftStrength;
            }
        }
        linearImpulse.sub((Vector3dc)LIFT_FORCE);
        LIFT_POS.sub(subLevel.getMassTracker().getCenterOfMass(), TEMP);
        angularImpulse.sub((Vector3dc)TEMP.cross((Vector3dc)LIFT_FORCE));
        BlockSubLevelLiftProvider.resetVectors();
    }

    public record LiftProviderContext(BlockPos pos, BlockState state, Vec3 dir) {
    }

    public static final class LiftProviderGroup {
        private final Set<BlockPos> positions;
        private final Vector3d totalLift = new Vector3d();
        private final Vector3d liftCenter = new Vector3d();
        private final Vector3d totalDrag = new Vector3d();
        private final Vector3d dragCenter = new Vector3d();
        public double totalLiftStrength;
        public double totalDragStrength;

        public LiftProviderGroup(Set<BlockPos> positions) {
            this.positions = positions;
        }

        public Set<BlockPos> positions() {
            return this.positions;
        }

        public Vector3d totalLift() {
            return this.totalLift;
        }

        public Vector3d liftCenter() {
            return this.liftCenter;
        }

        public Vector3d totalDrag() {
            return this.totalDrag;
        }

        public Vector3d dragCenter() {
            return this.dragCenter;
        }
    }
}
