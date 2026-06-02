/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix3d
 *  org.joml.Quaternionf
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.blocks.redstone_magnet;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.content.blocks.redstone_magnet.MagnetBehaviour;
import dev.simulated_team.simulated.content.blocks.redstone_magnet.MagnetMap;
import dev.simulated_team.simulated.content.blocks.redstone_magnet.MagnetPair;
import dev.simulated_team.simulated.content.blocks.redstone_magnet.RedstoneMagnetBlock;
import dev.simulated_team.simulated.content.blocks.redstone_magnet.SimMagnet;
import dev.simulated_team.simulated.content.particle.MagnetFieldParticleData;
import dev.simulated_team.simulated.service.SimConfigService;
import dev.simulated_team.simulated.util.SimMovementContext;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3d;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class RedstoneMagnetBlockEntity
extends SmartBlockEntity
implements SimMagnet {
    public static MagnetMap<RedstoneMagnetBlockEntity> GLOBAL_REDSTONE_MAGNET_MAP = new MagnetMap();
    private final HashSet<MagnetParticleEmitter> particleEmitters = new HashSet();
    public SubLevel latestSubLevel;
    public MagnetBehaviour magnet;
    public HashMap<Vector3d, Vector3d> nearbyMagnetPositions = new HashMap();
    protected boolean powered;
    protected int signalStrength;

    public RedstoneMagnetBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void initialize() {
        super.initialize();
        this.updateSignal();
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.magnet = new MagnetBehaviour(this, GLOBAL_REDSTONE_MAGNET_MAP);
        behaviours.add(this.magnet);
    }

    public void updateSignal() {
        boolean shouldPower = this.level.hasNeighborSignal(this.worldPosition);
        int newSignalStrength = this.level.getBestNeighborSignal(this.worldPosition);
        if (newSignalStrength != this.signalStrength) {
            this.signalStrength = newSignalStrength;
            this.powered = shouldPower;
            this.sendData();
        }
    }

    public void tick() {
        super.tick();
        this.latestSubLevel = Sable.HELPER.getContaining((BlockEntity)this);
        if (this.latestSubLevel != null) {
            MagnetMap<RedstoneMagnetBlockEntity> map = GLOBAL_REDSTONE_MAGNET_MAP;
            SimMovementContext context = SimMovementContext.getMovementContext(this.getLevel(), this.getBlockPos().getCenter());
            List<SimMovementContext> contexts = map.findNearby(context);
            for (SimMovementContext movementContext : contexts) {
                if (movementContext.subLevel() == this.latestSubLevel) continue;
                map.tryAddPair(this.getLevel(), this.getBlockPos(), movementContext.localBlockPos(), MagnetPair::new);
            }
        }
        if (this.level.isClientSide()) {
            return;
        }
        this.spawnParticles();
        for (MagnetParticleEmitter emitter : this.particleEmitters) {
            emitter.update();
        }
        this.particleEmitters.removeIf(x -> x.time < 0);
    }

    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putBoolean("IsPowered", this.powered);
        compound.putInt("SignalStrength", this.signalStrength);
        super.write(compound, registries, clientPacket);
    }

    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        this.powered = compound.getBoolean("IsPowered");
        this.signalStrength = compound.getInt("SignalStrength");
        super.read(compound, registries, clientPacket);
    }

    private void spawnParticles() {
        float probability = 0.1f * ((float)this.signalStrength / 15.0f);
        if (probability > 0.0f && this.level.random.nextFloat() < probability) {
            BlockPos blockpos;
            boolean negative = this.level.random.nextBoolean();
            Vec3i dir = ((Direction)this.getBlockState().getValue((Property)RedstoneMagnetBlock.FACING)).getNormal();
            if (negative) {
                dir = dir.multiply(-1);
            }
            if (this.level.getBlockState(blockpos = this.getBlockPos().offset(dir)).isSolidRender((BlockGetter)this.level, blockpos)) {
                return;
            }
            Vector3d offset = JOMLConversion.toJOML((Position)VecHelper.offsetRandomly((Vec3)new Vec3(0.0, 0.0, 0.0), (RandomSource)this.level.random, (float)0.35f));
            Vector3d pos = JOMLConversion.toJOML((Position)Vec3.atLowerCornerOf((Vec3i)dir));
            offset.fma(-pos.dot((Vector3dc)offset), (Vector3dc)pos);
            pos.mul(0.55);
            pos.add((Vector3dc)offset);
            SimMovementContext context = SimMovementContext.getMovementContext(this.level, Vec3.atCenterOf((Vec3i)this.getBlockPos()));
            context.orientation().transform(pos);
            pos.add((Vector3dc)JOMLConversion.toJOML((Position)context.globalPosition()));
            this.nearbyMagnetPositions.clear();
            List<SimMovementContext> contexts = GLOBAL_REDSTONE_MAGNET_MAP.findNearby(context);
            contexts.add(context);
            Level level = context.level();
            for (SimMovementContext movementContext : contexts) {
                RedstoneMagnetBlockEntity otherMagnet = (RedstoneMagnetBlockEntity)level.getBlockEntity(movementContext.localBlockPos());
                if (otherMagnet == null) continue;
                Vector3d otherMagneticMoment = otherMagnet.setMagneticMoment(new Vector3d());
                movementContext.orientation().transform(otherMagneticMoment);
                this.nearbyMagnetPositions.put(JOMLConversion.toJOML((Position)movementContext.globalPosition()), otherMagneticMoment);
            }
            int steps = 4 + (int)(20.0f * ((float)this.signalStrength / 15.0f) * this.level.random.nextFloat());
            this.particleEmitters.add(new MagnetParticleEmitter(pos, this.nearbyMagnetPositions, steps, this.level, negative));
        }
    }

    public Quaternionf getOrientation() {
        return ((Direction)this.getBlockState().getValue((Property)BlockStateProperties.FACING)).getRotation();
    }

    @Override
    public SubLevel getLatestSubLevel() {
        if (this.latestSubLevel != null && this.latestSubLevel.isRemoved()) {
            this.latestSubLevel = null;
        }
        return this.latestSubLevel;
    }

    @Override
    public Vector3d setMagneticMoment(Vector3d v) {
        v.set((Vector3dc)JOMLConversion.toJOML((Position)Vec3.atLowerCornerOf((Vec3i)((Direction)this.getBlockState().getValue((Property)RedstoneMagnetBlock.FACING)).getNormal())));
        v.mul((double)this.signalStrength / 15.0 * Math.sqrt((Double)SimConfigService.INSTANCE.server().physics.redstoneMagnetStrength.get()));
        return v;
    }

    @Override
    public Vec3 getMagnetPosition() {
        return Vec3.atCenterOf((Vec3i)this.getBlockPos());
    }

    @Override
    public boolean magnetActive() {
        return this.signalStrength > 0;
    }

    private static class MagnetParticleEmitter {
        protected final HashMap<Vector3d, Vector3d> nearbyMagnets;
        protected final Vector3d pos;
        protected final Vector3d oldNudge = new Vector3d();
        protected final Vector3d newNudge = new Vector3d();
        protected int time;
        protected final int startTime;
        protected final Level level;
        protected final boolean negative;
        static final Vector3d k1 = new Vector3d();
        static final Vector3d k2 = new Vector3d();
        static final Vector3d k3 = new Vector3d();
        static final Vector3d k4 = new Vector3d();
        static final Vector3d posTemp = new Vector3d();
        static final Vector3d currentField = new Vector3d();
        static final Vector3d relativePos = new Vector3d();
        static final Vector3d moment = new Vector3d();

        public MagnetParticleEmitter(Vector3d startPos, HashMap<Vector3d, Vector3d> nearbyMagnets, int maxTime, Level level, boolean negative) {
            this.pos = new Vector3d((Vector3dc)startPos);
            this.nearbyMagnets = nearbyMagnets;
            this.time = maxTime;
            this.startTime = maxTime;
            this.level = level;
            this.negative = negative;
            this.rk4(this.pos, this.newNudge);
        }

        public void update() {
            --this.time;
            if (this.time < 0) {
                return;
            }
            if (!this.level.getBlockState(BlockPos.containing((double)this.pos.x, (double)this.pos.y, (double)this.pos.z)).isAir()) {
                this.time = -1;
                return;
            }
            this.pos.add((Vector3dc)this.newNudge);
            this.oldNudge.set((Vector3dc)this.newNudge);
            this.rk4(this.pos, this.newNudge);
            ((ServerLevel)this.level).sendParticles((ParticleOptions)new MagnetFieldParticleData(this.negative), this.pos.x, this.pos.y, this.pos.z, 1, 0.01, 0.01, 0.01, 0.0);
        }

        void rk4(Vector3d pos, Vector3d nudgeOut) {
            double dt = 0.2;
            this.getField(pos, k1);
            this.getField(pos.fma(0.1, (Vector3dc)k1, posTemp), k2);
            this.getField(pos.fma(0.1, (Vector3dc)k2, posTemp), k3);
            this.getField(pos.fma(0.2, (Vector3dc)k3, posTemp), k4);
            nudgeOut.set((Vector3dc)k1).fma(2.0, (Vector3dc)k2).fma(2.0, (Vector3dc)k3).add((Vector3dc)k4).mul(0.03333333333333333);
        }

        void getField(Vector3d pos, Vector3d field) {
            field.zero();
            for (Map.Entry<Vector3d, Vector3d> entry : this.nearbyMagnets.entrySet()) {
                relativePos.set((Vector3dc)pos).sub((Vector3dc)entry.getKey());
                moment.set((Vector3dc)entry.getValue()).mul(this.negative ? -1.0 : 1.0);
                if (moment.lengthSquared() == 0.0) continue;
                double distanceSq = relativePos.lengthSquared();
                if (distanceSq < 0.2) {
                    this.time = -1;
                    return;
                }
                double d = moment.dot((Vector3dc)relativePos) / distanceSq;
                currentField.set((Vector3dc)relativePos).mul(3.0 * d);
                currentField.sub((Vector3dc)moment);
                currentField.div(distanceSq);
                field.add((Vector3dc)currentField);
            }
            field.normalize();
        }

        private Matrix3d generateOuterProduct(Vector3d v1, Vector3d v2) {
            return new Matrix3d(v1.x * v2.x, v1.x * v2.y, v1.x * v2.z, v1.y * v2.x, v1.y * v2.y, v1.y * v2.z, v1.z * v2.x, v1.z * v2.y, v1.z * v2.z);
        }
    }
}
