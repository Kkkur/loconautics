/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.SubLevelAssemblyHelper$AssemblyTransform
 *  dev.ryanhcode.sable.api.physics.force.ForceGroup
 *  dev.ryanhcode.sable.api.physics.force.ForceGroups
 *  dev.ryanhcode.sable.api.physics.force.QueuedForceGroup
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  dev.ryanhcode.sable.util.LevelAccelerator
 *  dev.ryanhcode.sable.util.SableMathUtils
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.joml.Matrix3d
 *  org.joml.Matrix3dc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.balloon;

import dev.eriksonn.aeronautics.content.blocks.hot_air.BlockEntityLiftingGasProvider;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.Balloon;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.graph.BalloonLayerData;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.graph.BalloonLayerGraph;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.map.SavedBalloon;
import dev.eriksonn.aeronautics.content.blocks.hot_air.lifting_gas.LiftingGasData;
import dev.eriksonn.aeronautics.content.blocks.hot_air.lifting_gas.LiftingGasHolder;
import dev.eriksonn.aeronautics.content.blocks.hot_air.lifting_gas.LiftingGasType;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.physics.force.ForceGroup;
import dev.ryanhcode.sable.api.physics.force.ForceGroups;
import dev.ryanhcode.sable.api.physics.force.QueuedForceGroup;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.util.LevelAccelerator;
import dev.ryanhcode.sable.util.SableMathUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.invoke.LambdaMetafactory;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix3d;
import org.joml.Matrix3dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class ServerBalloon
extends Balloon {
    private final Map<LiftingGasType, LiftingGasData> gasAmounts = new Object2ObjectOpenHashMap();
    private final Matrix3d outerProduct = new Matrix3d();
    private final Matrix3d inertiaTensor = new Matrix3d();
    private final Matrix3d translatedInertiaTensor = new Matrix3d();
    private final Matrix3d translatedOuterProduct = new Matrix3d();
    private final Vector3d averagePosition = new Vector3d();
    private final Vector3d translatedAveragePosition = new Vector3d();
    private final Vector3d offset = new Vector3d();
    private final Vector3d physicsOrigin;
    private double totalLift;
    private double totalFilledVolume;
    private double totalTargetVolume;
    private double totalVolumeChange;
    private boolean leaking = false;

    @ApiStatus.Internal
    public ServerBalloon(Level level, LevelAccelerator accelerator, BlockPos controllerPos, BalloonLayerGraph graph, ObjectArrayList<BlockEntityLiftingGasProvider> heaters) {
        super(level, accelerator, controllerPos, graph, heaters);
        this.physicsOrigin = new Vector3d((double)controllerPos.getX(), (double)controllerPos.getY(), (double)controllerPos.getZ());
        this.onRebuilt();
    }

    public static Matrix3d fmaInertiaTensor(Vector3dc u, Vector3dc v, double scale, Matrix3d target) {
        target.m00 += (u.y() * v.y() + u.z() * v.z()) * scale;
        target.m01 -= u.y() * v.x() * scale;
        target.m02 -= u.z() * v.x() * scale;
        target.m10 -= u.x() * v.y() * scale;
        target.m11 += (u.z() * v.z() + u.x() * v.x()) * scale;
        target.m12 -= u.z() * v.y() * scale;
        target.m20 -= u.x() * v.z() * scale;
        target.m21 -= u.y() * v.z() * scale;
        target.m22 += (u.x() * v.x() + u.y() * v.y()) * scale;
        return target;
    }

    public void translateMatrices(ServerSubLevel serverSubLevel) {
        Vector3dc centerOfMass = serverSubLevel.getMassTracker().getCenterOfMass();
        this.offset.set(centerOfMass).sub((Vector3dc)this.physicsOrigin);
        this.translatedOuterProduct.set((Matrix3dc)this.outerProduct);
        SableMathUtils.fmaOuterProduct((Vector3dc)this.offset, (Vector3dc)this.averagePosition, (double)(-this.getCapacity()), (Matrix3d)this.translatedOuterProduct);
        SableMathUtils.fmaOuterProduct((Vector3dc)this.averagePosition, (Vector3dc)this.offset, (double)(-this.getCapacity()), (Matrix3d)this.translatedOuterProduct);
        SableMathUtils.fmaOuterProduct((Vector3dc)this.offset, (Vector3dc)this.offset, (double)this.getCapacity(), (Matrix3d)this.translatedOuterProduct);
        this.translatedInertiaTensor.set((Matrix3dc)this.inertiaTensor);
        ServerBalloon.fmaInertiaTensor((Vector3dc)this.offset, (Vector3dc)this.averagePosition, -this.getCapacity(), this.translatedInertiaTensor);
        ServerBalloon.fmaInertiaTensor((Vector3dc)this.averagePosition, (Vector3dc)this.offset, -this.getCapacity(), this.translatedInertiaTensor);
        ServerBalloon.fmaInertiaTensor((Vector3dc)this.offset, (Vector3dc)this.offset, this.getCapacity(), this.translatedInertiaTensor);
    }

    @Override
    protected void checkHeaters() {
        super.checkHeaters();
        for (LiftingGasData data : this.gasAmounts.values()) {
            data.target = 0.0;
        }
        if (this.leaking) {
            return;
        }
        for (BlockEntityLiftingGasProvider heater : this.heaters) {
            this.gasAmounts.compute(heater.getLiftingGasType(), (k, v) -> {
                if (v == null) {
                    v = new LiftingGasData();
                }
                v.target += heater.getGasOutput();
                return v;
            });
        }
    }

    public void applyForces(double timeStep) {
        int capacity = this.getCapacity();
        if (capacity <= 0) {
            return;
        }
        ServerSubLevel subLevel = (ServerSubLevel)Sable.HELPER.getContaining(this.level, (Vec3i)this.controllerPos);
        if (subLevel == null || this.totalFilledVolume == 0.0) {
            return;
        }
        this.translateMatrices(subLevel);
        ServerLevel level = subLevel.getLevel();
        Pose3d pose = subLevel.logicalPose();
        this.translatedAveragePosition.set((Vector3dc)this.averagePosition).add((Vector3dc)this.physicsOrigin);
        Vector3d localAveragePosition = new Vector3d((Vector3dc)this.translatedAveragePosition).sub((Vector3dc)pose.rotationPoint());
        Vector3d worldCenter = new Vector3d((Vector3dc)localAveragePosition);
        pose.orientation().transform(worldCenter).add((Vector3dc)pose.position());
        Vector3d gravity = pose.orientation().transformInverse(new Vector3d((Vector3dc)DimensionPhysicsData.getGravity((Level)level, (Vector3dc)worldCenter)));
        double pressure = DimensionPhysicsData.getAirPressure((Level)level, (Vector3dc)worldCenter);
        double diff = 1.0;
        Vector3d gradient = new Vector3d();
        double pressureX = DimensionPhysicsData.getAirPressure((Level)level, (Vector3dc)gradient.set(1.0, 0.0, 0.0).add((Vector3dc)worldCenter)) - pressure;
        double pressureY = DimensionPhysicsData.getAirPressure((Level)level, (Vector3dc)gradient.set(0.0, 1.0, 0.0).add((Vector3dc)worldCenter)) - pressure;
        double pressureZ = DimensionPhysicsData.getAirPressure((Level)level, (Vector3dc)gradient.set(0.0, 0.0, 1.0).add((Vector3dc)worldCenter)) - pressure;
        gradient.set(pressureX, pressureY, pressureZ).div(1.0);
        pose.orientation().transformInverse(gradient);
        Vector3d baseForcePerBlock = new Vector3d((Vector3dc)gravity).mul(-this.totalLift / (double)capacity);
        Vector3d baseTorquePerBlock = new Vector3d();
        Vector3d force = new Vector3d();
        Vector3d torque = new Vector3d();
        Vector3d temp = new Vector3d();
        Vector3d centerForce = new Vector3d();
        this.translatedOuterProduct.transform(gradient);
        baseTorquePerBlock.cross((Vector3dc)localAveragePosition, centerForce).add((Vector3dc)baseForcePerBlock);
        baseTorquePerBlock.cross((Vector3dc)gradient, force).fma((double)capacity * pressure, (Vector3dc)centerForce);
        localAveragePosition.cross((Vector3dc)force, torque);
        torque.add((Vector3dc)gradient.cross((Vector3dc)centerForce, temp));
        torque.fma(pressure, (Vector3dc)this.translatedInertiaTensor.transform((Vector3dc)baseTorquePerBlock, temp));
        force.mul(timeStep);
        torque.mul(timeStep);
        QueuedForceGroup forceGroup = subLevel.getOrCreateQueuedForceGroup((ForceGroup)ForceGroups.BALLOON_LIFT.get());
        forceGroup.getForceTotal().applyLinearAndAngularImpulse((Vector3dc)force, (Vector3dc)torque);
        if (subLevel.isTrackingIndividualQueuedForces()) {
            forceGroup.recordPointForce((Vector3dc)new Vector3d((Vector3dc)this.translatedAveragePosition), (Vector3dc)force);
        }
    }

    @Override
    protected void onRebuilt() {
        BlockPos pos;
        Iterator<BlockPos> iter;
        this.outerProduct.zero();
        this.inertiaTensor.zero();
        this.averagePosition.zero();
        Vector3d p = new Vector3d();
        Matrix3d m = new Matrix3d();
        for (List<BalloonLayerData> layers : this.graph.getAllLayers()) {
            for (BalloonLayerData layer : layers) {
                iter = layer.nonSolidBlockIterator();
                while (iter.hasNext()) {
                    pos = iter.next();
                    p.set((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5).sub((Vector3dc)this.physicsOrigin);
                    this.averagePosition.add((Vector3dc)p);
                }
            }
        }
        this.averagePosition.div((double)this.getCapacity());
        for (List<BalloonLayerData> layers : this.graph.getAllLayers()) {
            for (BalloonLayerData layer : layers) {
                iter = layer.nonSolidBlockIterator();
                while (iter.hasNext()) {
                    pos = iter.next();
                    int x = pos.getX();
                    int y = pos.getY();
                    int z = pos.getZ();
                    p.set((double)x + 0.5, (double)y + 0.5, (double)z + 0.5).sub((Vector3dc)this.physicsOrigin);
                    this.outerProduct.add((Matrix3dc)m.set(p.x * p.x, p.x * p.y, p.x * p.z, p.y * p.x, p.y * p.y, p.y * p.z, p.z * p.x, p.z * p.y, p.z * p.z));
                    this.inertiaTensor.add((Matrix3dc)m.set(p.y * p.y + p.z * p.z, -p.x * p.y, -p.x * p.z, -p.y * p.x, p.z * p.z + p.x * p.x, -p.y * p.z, -p.z * p.x, -p.z * p.y, p.x * p.x + p.y * p.y));
                }
            }
        }
        this.leaking = false;
    }

    @Override
    protected void onHotAirAdded(BlockPos pos) {
        Matrix3d m = new Matrix3d();
        Vector3d p = new Vector3d();
        p.set((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5).sub((Vector3dc)this.physicsOrigin);
        this.averagePosition.mul((double)(this.getCapacity() - 1)).add((Vector3dc)p).div((double)this.getCapacity());
        this.outerProduct.add((Matrix3dc)m.set(p.x * p.x, p.x * p.y, p.x * p.z, p.y * p.x, p.y * p.y, p.y * p.z, p.z * p.x, p.z * p.y, p.z * p.z));
        this.inertiaTensor.add((Matrix3dc)m.set(p.y * p.y + p.z * p.z, -p.x * p.y, -p.x * p.z, -p.y * p.x, p.z * p.z + p.x * p.x, -p.y * p.z, -p.z * p.x, -p.z * p.y, p.x * p.x + p.y * p.y));
    }

    @Override
    protected void onHotAirRemoved(BlockPos pos) {
        Matrix3d m = new Matrix3d();
        Vector3d p = new Vector3d();
        p.set((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5).sub((Vector3dc)this.physicsOrigin);
        this.averagePosition.mul((double)(this.getCapacity() + 1)).sub((Vector3dc)p).div((double)this.getCapacity());
        this.outerProduct.add((Matrix3dc)m.set(p.x * p.x, p.x * p.y, p.x * p.z, p.y * p.x, p.y * p.y, p.y * p.z, p.z * p.x, p.z * p.y, p.z * p.z));
        this.inertiaTensor.add((Matrix3dc)m.set(p.y * p.y + p.z * p.z, -p.x * p.y, -p.x * p.z, -p.y * p.x, p.z * p.z + p.x * p.x, -p.y * p.z, -p.z * p.x, -p.z * p.y, p.x * p.x + p.y * p.y));
    }

    @Override
    protected void onHotAirRemoved(Iterable<BlockPos> iterable) {
        super.onHotAirRemoved(iterable);
        Matrix3d m = new Matrix3d();
        Vector3d p = new Vector3d();
        for (BlockPos blockPos : iterable) {
            int y = blockPos.getY();
            int x = blockPos.getX();
            int z = blockPos.getZ();
            p.set((double)x + 0.5, (double)y + 0.5, (double)z + 0.5).sub((Vector3dc)this.physicsOrigin);
            this.averagePosition.mul((double)this.capacity).sub((Vector3dc)p).div((double)(this.capacity - 1));
            this.outerProduct.sub((Matrix3dc)m.set(p.x * p.x, p.x * p.y, p.x * p.z, p.y * p.x, p.y * p.y, p.y * p.z, p.z * p.x, p.z * p.y, p.z * p.z));
            this.inertiaTensor.sub((Matrix3dc)m.set(p.y * p.y + p.z * p.z, -p.x * p.y, -p.x * p.z, -p.y * p.x, p.z * p.z + p.x * p.x, -p.y * p.z, -p.z * p.x, -p.z * p.y, p.x * p.x + p.y * p.y));
            --this.capacity;
        }
    }

    @Override
    public boolean isValid() {
        return this.totalTargetVolume > 0.05 || this.totalFilledVolume > 0.05;
    }

    @Override
    public void tick() {
        super.tick();
        this.updateGasAmounts();
    }

    public void updateGasAmounts() {
        LiftingGasData data;
        int capacity = this.getCapacity();
        this.totalTargetVolume = 0.0;
        for (LiftingGasData data2 : this.gasAmounts.values()) {
            this.totalTargetVolume += data2.target;
        }
        double scale = Math.min((double)capacity / this.totalTargetVolume, 1.0);
        double totalDesiredVolume = 0.0;
        for (Map.Entry<LiftingGasType, LiftingGasData> entry : this.gasAmounts.entrySet()) {
            data = entry.getValue();
            LiftingGasType type = entry.getKey();
            double diff = data.target * scale - data.amount;
            double d = diff > 0.0 ? diff / type.getFillingTime() : (data.nudge = diff < 0.0 ? diff / type.getEmptyingTime() : 0.0);
            if (type.getResponsivenessAdjustmentFactor() > 0.0 && type.getResponsivenessAdjustmentRange() > 0.0) {
                double x = diff / ((double)capacity * type.getResponsivenessAdjustmentRange());
                data.nudge *= 1.0 + type.getResponsivenessAdjustmentFactor() / (1.0 + 3.0 * x * x);
            }
            totalDesiredVolume += data.amount + data.nudge;
        }
        this.totalLift = 0.0;
        this.totalFilledVolume = 0.0;
        this.totalVolumeChange = 0.0;
        for (Map.Entry<LiftingGasType, LiftingGasData> entry : this.gasAmounts.entrySet()) {
            data = entry.getValue();
            data.amount += data.nudge;
            this.totalLift += data.amount * entry.getKey().getLiftStrength();
            this.totalFilledVolume += data.amount;
            this.totalVolumeChange += data.nudge;
        }
        this.totalTargetVolume = Math.min(this.totalTargetVolume, (double)capacity);
    }

    @Override
    public void merge(Balloon other) {
        super.merge(other);
        if (other instanceof ServerBalloon) {
            ServerBalloon otherServerBalloon = (ServerBalloon)other;
            for (Map.Entry<LiftingGasType, LiftingGasData> entry : otherServerBalloon.gasAmounts.entrySet()) {
                LiftingGasType type = entry.getKey();
                LiftingGasData data = entry.getValue();
                this.gasAmounts.computeIfAbsent((LiftingGasType)type, (Function<LiftingGasType, LiftingGasData>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, lambda$merge$1(dev.eriksonn.aeronautics.content.blocks.hot_air.lifting_gas.LiftingGasType ), (Ldev/eriksonn/aeronautics/content/blocks/hot_air/lifting_gas/LiftingGasType;)Ldev/eriksonn/aeronautics/content/blocks/hot_air/lifting_gas/LiftingGasData;)()).amount += data.amount;
            }
        }
    }

    @Override
    public void setLeaking() {
        this.leaking = true;
    }

    public Vec3 getCenter() {
        return JOMLConversion.toMojang((Vector3dc)this.averagePosition).add(this.physicsOrigin.x(), this.physicsOrigin.y(), this.physicsOrigin.z());
    }

    public double getTotalLift() {
        return this.totalLift;
    }

    public double getTotalFilledVolume() {
        return this.totalFilledVolume;
    }

    public double getTotalTargetVolume() {
        return this.totalTargetVolume;
    }

    public double getTotalVolumeChange() {
        return this.totalVolumeChange;
    }

    @Override
    public boolean shouldSpawnGust(BlockPos pos) {
        float percentHeight = ((float)pos.getY() + 0.5f - (float)this.bounds.minY) / this.getHeight();
        return (double)percentHeight > 1.0 - Math.clamp(this.totalFilledVolume / (double)this.getCapacity(), 0.0, 1.0);
    }

    @Override
    public void setAssembling(SubLevelAssemblyHelper.AssemblyTransform transform) {
        super.setAssembling(transform);
        this.physicsOrigin.set((double)this.controllerPos.getX(), (double)this.controllerPos.getY(), (double)this.controllerPos.getZ());
    }

    public void loadFrom(SavedBalloon unloaded) {
        for (LiftingGasHolder entry : unloaded.gasData()) {
            LiftingGasType type = entry.type();
            LiftingGasData data = entry.data();
            this.gasAmounts.put(type, data);
        }
    }

    public List<LiftingGasHolder> getLiftingGasHolders() {
        ObjectArrayList holders = new ObjectArrayList();
        for (Map.Entry<LiftingGasType, LiftingGasData> entry : this.gasAmounts.entrySet()) {
            holders.add(new LiftingGasHolder(entry.getKey(), entry.getValue()));
        }
        return holders;
    }

    private static /* synthetic */ LiftingGasData lambda$merge$1(LiftingGasType x) {
        return new LiftingGasData();
    }
}
