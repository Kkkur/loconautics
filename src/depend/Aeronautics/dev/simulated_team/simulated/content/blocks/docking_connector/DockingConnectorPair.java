/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.blocks.docking_connector;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.config.server.physics.SimPhysics;
import dev.simulated_team.simulated.content.blocks.docking_connector.DockingConnectorBlockEntity;
import dev.simulated_team.simulated.content.blocks.redstone_magnet.MagnetPair;
import dev.simulated_team.simulated.content.blocks.redstone_magnet.SimMagnet;
import dev.simulated_team.simulated.data.advancements.SimAdvancements;
import dev.simulated_team.simulated.index.SimSoundEvents;
import dev.simulated_team.simulated.service.SimConfigService;
import dev.simulated_team.simulated.util.SimMathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class DockingConnectorPair
extends MagnetPair<DockingConnectorBlockEntity> {
    private final Vector3d v = new Vector3d();
    private final Quaterniond orientation1 = new Quaterniond();
    private final Quaterniond orientation2 = new Quaterniond();
    private final Quaterniond relativeBlockOrientation = new Quaterniond();
    private final Quaterniond target = new Quaterniond();

    public DockingConnectorPair(Level level, BlockPos pos1, BlockPos pos2) {
        super(level, pos1, pos2);
    }

    public static Vector3d getRelativeTipPosition(DockingConnectorBlockEntity magnet1, DockingConnectorBlockEntity magnet2, Vector3d dest) {
        Vec3 plotPos1 = magnet1.getTipPosition();
        Vec3 plotPos2 = magnet2.getTipPosition();
        return DockingConnectorPair.getRelativePosition(magnet1, magnet2, plotPos1, plotPos2, dest);
    }

    public static Vector3d getAverageTipPosition(DockingConnectorBlockEntity magnet1, DockingConnectorBlockEntity magnet2, Vector3d dest) {
        Vec3 plotPos1 = magnet1.getTipPosition();
        Vec3 plotPos2 = magnet2.getTipPosition();
        SubLevel shell1 = magnet1.getLatestSubLevel();
        SubLevel shell2 = magnet2.getLatestSubLevel();
        Vec3 pos1 = plotPos1;
        Vec3 pos2 = plotPos2;
        if (shell1 != null) {
            pos1 = shell1.logicalPose().transformPosition(pos1);
        }
        if (shell2 != null) {
            pos2 = shell2.logicalPose().transformPosition(pos2);
        }
        return dest.set(pos1.x + pos2.x, pos1.y + pos2.y, pos1.z + pos2.z).div(2.0);
    }

    @Override
    public void tick() {
        super.tick();
        this.dock(false);
    }

    private void toggleLock(boolean newLock, DockingConnectorBlockEntity dock1, DockingConnectorBlockEntity dock2) {
        Vector3d pos = DockingConnectorPair.getAverageTipPosition(dock1, dock2, new Vector3d());
        if (newLock) {
            this.level.playSound(null, pos.x, pos.y, pos.z, SimSoundEvents.DOCKING_CONNECTOR_DOCKS.event(), SoundSource.BLOCKS, 1.0f, 0.9f + this.level.getRandom().nextFloat() * 0.3f);
            SimAdvancements.A_CALCULATED_CONNECTION.awardToNearby(dock1.getBlockPos(), this.level, 16.0);
            SimAdvancements.A_CALCULATED_CONNECTION.awardToNearby(dock2.getBlockPos(), this.level, 16.0);
        } else {
            this.level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.NETHERITE_BLOCK_FALL, SoundSource.BLOCKS, 0.25f, 0.75f);
        }
    }

    public void dock(boolean force) {
        BlockEntity blockEntity = this.level.getBlockEntity(this.blockPos1);
        if (blockEntity instanceof DockingConnectorBlockEntity) {
            DockingConnectorBlockEntity dock1 = (DockingConnectorBlockEntity)blockEntity;
            blockEntity = this.level.getBlockEntity(this.blockPos2);
            if (blockEntity instanceof DockingConnectorBlockEntity) {
                DockingConnectorBlockEntity dock2 = (DockingConnectorBlockEntity)blockEntity;
                if (dock1.isExtended() && dock2.isExtended()) {
                    boolean locked = dock1.isFeetExtended() && dock2.isFeetExtended();
                    Vector3d t = this.getRelativeOrientationDifference(dock1, dock2, new Vector3d());
                    Vector3d r = DockingConnectorPair.getRelativeTipPosition(dock1, dock2, new Vector3d());
                    double minDist = Math.min(dock1.closestPairDistance, dock2.closestPairDistance);
                    if (!force && r.lengthSquared() > minDist * minDist * 2.0) {
                        return;
                    }
                    SimPhysics config = SimConfigService.INSTANCE.server().physics;
                    double angleTolerance = Math.toRadians((Double)config.dockingConnectorAngleTolerance.get());
                    double distanceTolerance = (Double)config.dockingConnectorDistanceTolerance.get();
                    if (force || t.lengthSquared() < angleTolerance * angleTolerance && r.lengthSquared() < distanceTolerance * distanceTolerance) {
                        if (force) {
                            locked = true;
                            dock1.state = DockingConnectorBlockEntity.DockingConnectorState.LOCKING;
                            dock2.state = DockingConnectorBlockEntity.DockingConnectorState.LOCKING;
                        } else {
                            boolean previousLockingState;
                            boolean bl = previousLockingState = dock1.state == DockingConnectorBlockEntity.DockingConnectorState.LOCKED && dock2.state == DockingConnectorBlockEntity.DockingConnectorState.LOCKED;
                            if (previousLockingState != locked) {
                                this.toggleLock(locked, dock1, dock2);
                            }
                        }
                        if (!this.level.isClientSide()) {
                            ServerSubLevel secondSubLevel;
                            ServerSubLevel firstSubLevel = (ServerSubLevel)Sable.HELPER.getContaining((BlockEntity)dock1);
                            if (firstSubLevel != (secondSubLevel = (ServerSubLevel)Sable.HELPER.getContaining((BlockEntity)dock2))) {
                                boolean first = firstSubLevel != null ^ secondSubLevel != null ? firstSubLevel != null : true;
                                Quaterniond blockOrientation1 = new Quaterniond(dock1.getOrientation());
                                Quaterniond blockOrientation2 = new Quaterniond(dock2.getOrientation());
                                Quaterniond orientation = this.target.mul((Quaterniondc)blockOrientation1, new Quaterniond()).mul((Quaterniondc)blockOrientation2.conjugate());
                                this.orientation1.set((Quaterniondc)(firstSubLevel == null ? this.orientation1.identity() : firstSubLevel.logicalPose().orientation()));
                                this.orientation2.set((Quaterniondc)(secondSubLevel == null ? this.orientation2.identity() : secondSubLevel.logicalPose().orientation()));
                                this.orientation2.transformInverse(r);
                                this.orientation2.premul((Quaterniondc)this.orientation1.conjugate());
                                dock1.setDock(dock2, locked, (Quaterniondc)(first ? orientation : null), (Vector3dc)r, (Quaterniondc)this.orientation2);
                                dock2.setDock(dock1, locked, (Quaterniondc)(first ? null : orientation.conjugate()), (Vector3dc)r.negate(), (Quaterniondc)this.orientation2.conjugate());
                                return;
                            }
                            dock1.setDock(dock2, locked, null, null, null);
                            dock2.setDock(dock1, locked, null, null, null);
                        }
                    } else if (dock1.state == DockingConnectorBlockEntity.DockingConnectorState.LOCKING && dock2.state == DockingConnectorBlockEntity.DockingConnectorState.LOCKING) {
                        dock1.unDock();
                        dock2.unDock();
                    }
                }
            }
        }
    }

    public void unDock() {
        BlockEntity blockEntity1 = this.level.getBlockEntity(this.blockPos1);
        BlockEntity blockEntity2 = this.level.getBlockEntity(this.blockPos2);
        if (blockEntity1 instanceof DockingConnectorBlockEntity) {
            DockingConnectorBlockEntity dock1 = (DockingConnectorBlockEntity)blockEntity1;
            if (blockEntity2 instanceof DockingConnectorBlockEntity) {
                DockingConnectorBlockEntity dock2 = (DockingConnectorBlockEntity)blockEntity2;
                dock1.unDock();
                dock2.unDock();
                this.toggleLock(false, dock1, dock2);
            }
        }
    }

    @Override
    protected double forceDistanceScale(double distance, DockingConnectorBlockEntity magnet1, DockingConnectorBlockEntity magnet2) {
        double scale = this.preventPairFightingScale(distance, magnet1, magnet2);
        distance = 1.0 + Math.max(0.0, distance - 0.25);
        distance *= distance;
        distance *= distance;
        return 4.0 * scale / distance;
    }

    @Override
    protected double torqueDistanceScale(double distance, DockingConnectorBlockEntity magnet1, DockingConnectorBlockEntity magnet2) {
        double scale = this.preventPairFightingScale(distance, magnet1, magnet2);
        distance = 1.0 + Math.max(0.0, distance - 0.5);
        distance *= distance;
        return scale / distance;
    }

    protected double preventPairFightingScale(double distance, DockingConnectorBlockEntity magnet1, DockingConnectorBlockEntity magnet2) {
        double d = Math.min(magnet1.closestPairDistance, magnet2.closestPairDistance);
        if (d < distance * 0.9) {
            double k = d / (distance + 1.0E-5);
            return k * k;
        }
        return 1.0;
    }

    @Override
    protected boolean canConnect(Vector3d relativePosition, Vector3d moment1, Vector3d moment2) {
        return relativePosition.lengthSquared() < 16.0 && moment1.dot((Vector3dc)moment2) < 0.0;
    }

    @Override
    protected Vector3d getForce(MagnetPair.PairData data, Vector3d f) {
        f.set(data.moment2()).mul(-0.5 * data.moment1().dot(data.relativePosition()));
        f.fma(-0.5 * data.moment2().dot(data.relativePosition()), data.moment1());
        f.fma(data.moment1().dot(data.moment2()), data.relativePosition());
        f.fma(3.0 * (data.moment1().dot(data.relativePosition()) * data.moment2().dot(data.relativePosition())) / (data.relativePosition().lengthSquared() + 1.0E-12), data.relativePosition());
        return f.mul(data.forceScale() / 3.0);
    }

    @Override
    protected Vector3d getTorque(MagnetPair.PairData data, Vector3d t) {
        data.moment2().cross(data.moment1(), t);
        return t.mul(data.torqueScale());
    }

    @Override
    public double getDampingRatio() {
        return 0.8;
    }

    @Override
    public double getAccelerationLimit() {
        return (Double)SimConfigService.INSTANCE.server().physics.dockingConnectorLinearAccelerationClamping.get();
    }

    @Override
    public double getAngularAccelerationLimit() {
        return (Double)SimConfigService.INSTANCE.server().physics.dockingConnectorAngularAccelerationClamping.get();
    }

    @Override
    protected Vector3d getSymmetricTorque(MagnetPair.PairData data, Vector3d t) {
        this.getRelativeOrientationDifference(data.magnet1(), data.magnet2(), t);
        this.v.set(data.moment1()).sub(data.moment2()).div(2.0);
        this.v.mul(this.v.dot((Vector3dc)t), t);
        return t.mul(data.torqueScale());
    }

    private Vector3d getRelativeOrientationDifference(SimMagnet magnet1, SimMagnet magnet2, Vector3d t) {
        SubLevel subLevel1 = magnet1.getLatestSubLevel();
        SubLevel subLevel2 = magnet2.getLatestSubLevel();
        this.orientation1.set((Quaterniondc)(subLevel1 == null ? this.orientation1.identity() : subLevel1.logicalPose().orientation()));
        this.orientation2.set((Quaterniondc)(subLevel2 == null ? this.orientation2.identity() : subLevel2.logicalPose().orientation()));
        Quaterniond blockOrientation1 = new Quaterniond(magnet1.getOrientation());
        Quaterniond blockOrientation2 = new Quaterniond(magnet2.getOrientation());
        blockOrientation2.premul((Quaterniondc)this.orientation2).premul((Quaterniondc)this.orientation1.conjugate(new Quaterniond()));
        this.relativeBlockOrientation.set((Quaterniondc)blockOrientation1).div((Quaterniondc)blockOrientation2);
        SimMathUtils.clampQuaternionToGrid((Quaterniondc)this.relativeBlockOrientation, SimMathUtils.GridQuats.REAL.opposite(), this.target);
        this.relativeBlockOrientation.div((Quaterniondc)this.target);
        t.set(this.relativeBlockOrientation.x, this.relativeBlockOrientation.y, this.relativeBlockOrientation.z).mul(2.0);
        this.orientation1.transform(t);
        return t;
    }
}
