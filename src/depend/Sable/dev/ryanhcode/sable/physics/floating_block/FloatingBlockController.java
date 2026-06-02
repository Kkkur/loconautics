/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  org.joml.Matrix3d
 *  org.joml.Matrix3dc
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.physics.floating_block;

import dev.ryanhcode.sable.api.physics.force.ForceGroup;
import dev.ryanhcode.sable.api.physics.force.ForceGroups;
import dev.ryanhcode.sable.api.physics.force.QueuedForceGroup;
import dev.ryanhcode.sable.api.sublevel.KinematicContraption;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import dev.ryanhcode.sable.physics.floating_block.FloatingBlockCluster;
import dev.ryanhcode.sable.physics.floating_block.FloatingBlockMaterial;
import dev.ryanhcode.sable.physics.floating_block.FloatingClusterContainer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.util.SableMathUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix3d;
import org.joml.Matrix3dc;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class FloatingBlockController {
    private static final Vector3d frictionForce = new Vector3d();
    private static final Vector3d frictionTorque = new Vector3d();
    private static final Vector3d clusterFrictionForce = new Vector3d();
    private static final Vector3d clusterFrictionTorque = new Vector3d();
    private static final Vector3d localGravity = new Vector3d();
    private static final Vector3d localLinearVelocity = new Vector3d();
    private static final Vector3d localAngularVelocity = new Vector3d();
    private final FloatingClusterContainer sublevelContainer = new FloatingClusterContainer();
    List<FloatingClusterContainer> containers = new ArrayList<FloatingClusterContainer>();
    private final ServerSubLevel subLevel;
    private final Vector3d previousCenterOfMass = new Vector3d();
    private static final Vector3d totalWeightedForce = new Vector3d();
    private static final Vector3d averageForcePos = new Vector3d();
    private static final Vector3d liftingForce = new Vector3d();
    private static final Vector3d liftingTorque = new Vector3d();
    private static final Vector3d torqueTemp = new Vector3d();
    private static final Vector3d weightedPositionTemp = new Vector3d();
    private static final Vector3d totalAcceleration = new Vector3d();
    private static final Matrix3d containerRotation = new Matrix3d();
    private static final Vector3d clusterCenter = new Vector3d();
    private static final Vector3d totalAngularVelocity = new Vector3d();
    private static final Vector3d rotatedPos = new Vector3d();
    private static final Matrix3d slowDragMatrix = new Matrix3d();
    private static final Matrix3d fastDragMatrix = new Matrix3d();
    private static final Matrix3d averagePositionMatrix = new Matrix3d();
    private static final Matrix3d averagePositionMatrixInverse = new Matrix3d();
    private static final Matrix3d shiftedPositionMatrix = new Matrix3d();
    private static final Matrix3d shiftedPositionMatrixInverse = new Matrix3d();
    private static final Matrix3d tempTorqueMatrix = new Matrix3d();
    private static final Vector3d meanVelocity = new Vector3d();
    private static final Vector3d shiftedCenter = new Vector3d();
    private static final Vector3d linearSlowDrag = new Vector3d();
    private static final Matrix3d X2 = new Matrix3d();
    private static final Matrix3d Y2 = new Matrix3d();
    private static final Matrix3d YX = new Matrix3d();
    private static final Matrix3d traceMatrix = new Matrix3d();

    public FloatingBlockController(ServerSubLevel subLevel) {
        this.subLevel = subLevel;
    }

    public void physicsTick(double partialPhysicsTick, double timeStep, Vector3dc linearVelocity, Vector3dc angularVelocity, Vector3d linearImpulse, Vector3d angularImpulse) {
        this.containers.clear();
        this.containers.add(this.sublevelContainer);
        for (KinematicContraption contraption : this.subLevel.getPlot().getContraptions()) {
            FloatingClusterContainer container = contraption.sable$getFloatingClusterContainer();
            Vector3d lastPosition = new Vector3d(contraption.sable$getPosition(partialPhysicsTick - 1.0));
            Quaterniond lastOrientation = new Quaterniond((Quaterniondc)contraption.sable$getOrientation(partialPhysicsTick - 1.0));
            container.positionOffset.set(contraption.sable$getPosition(partialPhysicsTick));
            container.rotationOffset.set((Quaterniondc)contraption.sable$getOrientation(partialPhysicsTick));
            container.positionOffset.sub((Vector3dc)lastPosition, container.velocity);
            SableMathUtils.getAngularVelocity((Quaterniondc)lastOrientation, (Quaterniondc)container.rotationOffset, container.angularVelocity);
            container.velocity.mul(20.0);
            container.angularVelocity.mul(20.0);
            container.positionOffset.sub(this.subLevel.getMassTracker().getCenterOfMass());
            this.containers.add(container);
        }
        this.processBlockChanges();
        localGravity.set((Vector3dc)DimensionPhysicsData.getGravity((Level)this.subLevel.getLevel(), (Vector3dc)this.subLevel.logicalPose().position()));
        this.subLevel.logicalPose().orientation().transformInverse(localGravity);
        if (!this.needsTicking()) {
            return;
        }
        this.subLevel.logicalPose().orientation().transformInverse(linearVelocity, localLinearVelocity);
        this.subLevel.logicalPose().orientation().transformInverse(angularVelocity, localAngularVelocity);
        frictionForce.zero();
        frictionTorque.zero();
        QueuedForceGroup dragGroup = this.subLevel.getOrCreateQueuedForceGroup((ForceGroup)ForceGroups.DRAG.get());
        ObjectArrayList recordedFrictionForces = new ObjectArrayList();
        for (FloatingClusterContainer container : this.containers) {
            for (FloatingBlockCluster cluster : container.clusters) {
                if (cluster.getMaterial().scaleWithPressure()) {
                    cluster.getBlockData().computePressureScale(this.subLevel);
                }
                this.applyFriction(container, cluster, (Vector3dc)localGravity, (Vector3dc)localLinearVelocity, (Vector3dc)localAngularVelocity, clusterFrictionForce, clusterFrictionTorque);
                Vector3d recordedClusterFrictionForce = new Vector3d((Vector3dc)clusterFrictionForce);
                this.recordForce(container, cluster, dragGroup, recordedClusterFrictionForce);
                recordedFrictionForces.add(recordedClusterFrictionForce);
                frictionForce.add((Vector3dc)clusterFrictionForce);
                frictionTorque.add((Vector3dc)clusterFrictionTorque);
            }
        }
        for (Vector3d force : recordedFrictionForces) {
            force.mul(timeStep);
        }
        if (localGravity.lengthSquared() > 0.0) {
            this.applyLift(localGravity, linearImpulse, angularImpulse, timeStep);
        }
        linearImpulse.fma(timeStep, (Vector3dc)frictionForce);
        angularImpulse.fma(timeStep, (Vector3dc)frictionTorque);
    }

    public boolean needsTicking() {
        if (this.sublevelContainer.needsTicking()) {
            return true;
        }
        for (FloatingClusterContainer container : this.containers) {
            if (!container.needsTicking()) continue;
            return true;
        }
        return false;
    }

    private void processBlockChanges() {
        this.previousCenterOfMass.sub(this.subLevel.getMassTracker().getCenterOfMass());
        for (FloatingBlockCluster cluster : this.sublevelContainer.clusters) {
            cluster.getBlockData().translateOrigin((Vector3dc)this.previousCenterOfMass);
        }
        this.sublevelContainer.processBlockChanges(this.subLevel.getMassTracker().getCenterOfMass());
        this.previousCenterOfMass.set(this.subLevel.getMassTracker().getCenterOfMass());
    }

    private void applyLift(Vector3d localGravity, Vector3d linearImpulse, Vector3d angularImpulse, double timeStep) {
        double totalForce = 0.0;
        totalWeightedForce.set(0.0);
        for (FloatingClusterContainer container : this.containers) {
            for (FloatingBlockCluster cluster : container.clusters) {
                FloatingBlockMaterial material = cluster.getMaterial();
                if (material.liftStrength() == 0.0) continue;
                double clusterForce = material.liftStrength();
                if (material.scaleWithPressure()) {
                    clusterForce *= cluster.getBlockData().getPressureScale();
                }
                double weightedForce = clusterForce * cluster.getBlockData().totalScale;
                this.getTrueWeightedClusterPosition(container, cluster, weightedPositionTemp);
                if (material.preventSelfLift()) {
                    totalForce += weightedForce;
                    totalWeightedForce.fma(clusterForce, (Vector3dc)weightedPositionTemp);
                    continue;
                }
                linearImpulse.fma(-weightedForce * timeStep, (Vector3dc)localGravity);
                if (this.subLevel.isTrackingIndividualQueuedForces()) {
                    QueuedForceGroup levitationGroup = this.subLevel.getOrCreateQueuedForceGroup((ForceGroup)ForceGroups.LEVITATION.get());
                    this.recordForce(container, cluster, levitationGroup, new Vector3d((Vector3dc)localGravity).mul(-weightedForce * timeStep));
                }
                localGravity.cross((Vector3dc)weightedPositionTemp, torqueTemp);
                angularImpulse.fma(clusterForce * timeStep, (Vector3dc)torqueTemp);
            }
        }
        if (totalForce <= 0.0) {
            return;
        }
        totalWeightedForce.div(totalForce, averageForcePos);
        liftingForce.set((Vector3dc)localGravity).mul(-totalForce);
        averageForcePos.cross((Vector3dc)liftingForce, liftingTorque);
        this.subLevel.getMassTracker().getInverseInertiaTensor().transform((Vector3dc)liftingTorque, torqueTemp).cross((Vector3dc)averageForcePos, totalAcceleration);
        totalAcceleration.fma(1.0 / this.subLevel.getMassTracker().getMass(), (Vector3dc)liftingForce);
        double scaleFactor = -localGravity.lengthSquared() / localGravity.dot((Vector3dc)totalAcceleration);
        if (scaleFactor > 1.0) {
            scaleFactor = 1.0;
        }
        liftingForce.mul(scaleFactor);
        liftingTorque.mul(scaleFactor);
        if (this.subLevel.isTrackingIndividualQueuedForces()) {
            QueuedForceGroup levitationGroup = this.subLevel.getOrCreateQueuedForceGroup((ForceGroup)ForceGroups.LEVITATION.get());
            for (FloatingClusterContainer container : this.containers) {
                for (FloatingBlockCluster cluster : container.clusters) {
                    FloatingBlockMaterial material = cluster.getMaterial();
                    Vector3d force = new Vector3d((Vector3dc)localGravity).mul(timeStep * -cluster.getBlockData().totalScale * material.liftStrength());
                    force.mul(scaleFactor);
                    this.recordForce(container, cluster, levitationGroup, force);
                }
            }
        }
        linearImpulse.fma(timeStep, (Vector3dc)liftingForce);
        angularImpulse.fma(timeStep, (Vector3dc)liftingTorque);
    }

    private void recordForce(FloatingClusterContainer container, FloatingBlockCluster cluster, QueuedForceGroup forceGroup, Vector3d force) {
        forceGroup.recordPointForce((Vector3dc)this.getTrueWeightedClusterPosition(container, cluster, new Vector3d()).div(cluster.getBlockData().totalScale).add(this.subLevel.getMassTracker().getCenterOfMass()), (Vector3dc)force);
    }

    private Vector3d getTrueWeightedClusterPosition(FloatingClusterContainer container, FloatingBlockCluster cluster, Vector3d pos) {
        container.rotationOffset.transform((Vector3dc)cluster.getBlockData().weightedPosition, pos);
        return pos.fma(cluster.getBlockData().totalScale, (Vector3dc)container.positionOffset);
    }

    private void applyFriction(FloatingClusterContainer container, FloatingBlockCluster cluster, Vector3dc localGravity, Vector3dc linearVelocity, Vector3dc angularVelocity, Vector3d frictionForce, Vector3d frictionTorque) {
        double frictionScale = 1.0;
        if (cluster.getMaterial().scaleWithGravity()) {
            frictionScale = localGravity.length();
        }
        if (cluster.getMaterial().scaleWithPressure()) {
            frictionScale *= cluster.getBlockData().getPressureScale();
        }
        double speedScale = 3.0 / (cluster.getMaterial().transitionSpeed() * cluster.getMaterial().transitionSpeed());
        if (cluster.getMaterial().transitionSpeed() == 0.0) {
            speedScale = 0.0;
        }
        totalAngularVelocity.set(angularVelocity).add((Vector3dc)container.angularVelocity);
        this.getTrueWeightedClusterPosition(container, cluster, clusterCenter).div(cluster.getBlockData().totalScale);
        cluster.getBlockData().outerProduct.scale(1.0 / cluster.getBlockData().totalScale, averagePositionMatrix);
        container.rotationOffset.get(containerRotation);
        averagePositionMatrix.mulLocal((Matrix3dc)containerRotation);
        averagePositionMatrix.mul((Matrix3dc)containerRotation.transpose());
        averagePositionMatrix.invert(averagePositionMatrixInverse);
        shiftedPositionMatrixInverse.set((Matrix3dc)averagePositionMatrixInverse);
        SableMathUtils.fmaInertiaTensor((Vector3dc)totalAngularVelocity, speedScale, shiftedPositionMatrixInverse);
        shiftedPositionMatrixInverse.invert(shiftedPositionMatrix);
        angularVelocity.cross((Vector3dc)clusterCenter, meanVelocity);
        container.rotationOffset.transform((Vector3dc)cluster.getBlockData().weightedPosition, rotatedPos).div(cluster.getBlockData().totalScale);
        Vector3d extraContainerVelocity = container.angularVelocity.cross((Vector3dc)rotatedPos, rotatedPos);
        meanVelocity.add(linearVelocity).add((Vector3dc)container.velocity).add((Vector3dc)extraContainerVelocity);
        totalAngularVelocity.cross((Vector3dc)meanVelocity, shiftedCenter).mul(speedScale);
        shiftedPositionMatrix.transform(shiftedCenter);
        double slowDragScale = Math.sqrt(shiftedPositionMatrix.determinant() / averagePositionMatrix.determinant());
        slowDragScale *= Math.exp(-0.5 * (speedScale * meanVelocity.dot((Vector3dc)meanVelocity) - SableMathUtils.multiplyInnerProduct((Vector3dc)shiftedCenter, (Matrix3dc)shiftedPositionMatrixInverse, (Vector3dc)shiftedCenter)));
        if (cluster.getMaterial().transitionSpeed() == 0.0) {
            slowDragScale = 0.0;
        }
        this.getGravityMatrix(localGravity, cluster.getMaterial().slowVerticalFriction(), cluster.getMaterial().slowHorizontalFriction(), slowDragMatrix).scale(cluster.getBlockData().totalScale * frictionScale * slowDragScale);
        this.getGravityMatrix(localGravity, cluster.getMaterial().fastVerticalFriction(), cluster.getMaterial().fastHorizontalFriction(), fastDragMatrix).scale(cluster.getBlockData().totalScale * frictionScale);
        slowDragMatrix.transform(totalAngularVelocity.cross((Vector3dc)shiftedCenter, linearSlowDrag).add((Vector3dc)meanVelocity));
        fastDragMatrix.transform((Vector3dc)meanVelocity, frictionForce).add((Vector3dc)linearSlowDrag);
        clusterCenter.cross((Vector3dc)frictionForce, frictionTorque);
        Vector3d torqueTemp = shiftedCenter.cross((Vector3dc)linearSlowDrag, linearSlowDrag);
        frictionTorque.add((Vector3dc)torqueTemp);
        tempTorqueMatrix.zero();
        this.matrixThingy((Matrix3dc)averagePositionMatrix, (Matrix3dc)fastDragMatrix, tempTorqueMatrix);
        this.matrixThingy((Matrix3dc)shiftedPositionMatrix, (Matrix3dc)slowDragMatrix, tempTorqueMatrix);
        tempTorqueMatrix.transform((Vector3dc)totalAngularVelocity, torqueTemp);
        frictionTorque.add((Vector3dc)torqueTemp);
    }

    private void matrixThingy(Matrix3dc X, Matrix3dc Y, Matrix3d out) {
        Y.mul(X, YX);
        double traceX = X.m00() + X.m11() + X.m22();
        double traceY = Y.m00() + Y.m11() + Y.m22();
        double traceYX = YX.m00() + YX.m11() + YX.m22();
        traceMatrix.identity().scale(traceX).sub(X, X2);
        traceMatrix.identity().scale(traceY).sub(Y, Y2);
        traceMatrix.identity().scale(traceYX).sub((Matrix3dc)YX, YX);
        X2.mul((Matrix3dc)Y2);
        out.add((Matrix3dc)X2).sub((Matrix3dc)YX);
    }

    private Matrix3d getGravityMatrix(Vector3dc g, double verticalDrag, double horizontalDrag, Matrix3d target) {
        if (g.lengthSquared() > 1.0E-5) {
            SableMathUtils.setOuterProduct(g, g, (horizontalDrag - verticalDrag) / g.dot(g), target);
        } else {
            target.identity();
        }
        target.m00 -= horizontalDrag;
        target.m11 -= horizontalDrag;
        target.m22 -= horizontalDrag;
        return target;
    }

    private double getClampingFactor(Vector3dc currentVelocity, Vector3dc expectedVelocityChange) {
        double k = -currentVelocity.dot(expectedVelocityChange);
        double v = currentVelocity.lengthSquared();
        if (k < 0.0) {
            return 0.0;
        }
        if (10.0 * k < v) {
            return 1.0;
        }
        if (v < 1.0E-10) {
            return v / (k + 1.0E-10);
        }
        return v * (1.0 - Math.exp(-k / v)) / k;
    }

    private double getKineticClampingFactor(Vector3dc currentLinearVelocity, Vector3dc currentAngularVelocity, Vector3d frictionForce, Vector3d frictionTorque, double timestep) {
        double numerator = currentLinearVelocity.dot((Vector3dc)frictionForce) + currentAngularVelocity.dot((Vector3dc)frictionTorque);
        double denominator = frictionForce.dot((Vector3dc)frictionForce) * this.subLevel.getMassTracker().getInverseMass() + SableMathUtils.multiplyInnerProduct((Vector3dc)frictionTorque, this.subLevel.getMassTracker().getInverseInertiaTensor(), (Vector3dc)frictionTorque);
        if ((denominator *= timestep) < 1.0E-10) {
            return 1.0;
        }
        double t = -numerator / denominator;
        return Math.max(Math.min(t, 1.0), 0.0);
    }

    public void addFloatingBlock(BlockState state, Vector3d pos) {
        this.sublevelContainer.addFloatingBlock(state, pos);
    }

    public void removeFloatingBlock(BlockState state, Vector3d pos) {
        this.sublevelContainer.removeFloatingBlock(state, pos);
    }

    public void queueAddFloatingBlock(BlockState state, BlockPos pos) {
        this.sublevelContainer.queueAddFloatingBlock(state, pos);
    }

    public void queueRemoveFloatingBlock(BlockState state, BlockPos pos) {
        this.sublevelContainer.queueRemoveFloatingBlock(state, pos);
    }
}
