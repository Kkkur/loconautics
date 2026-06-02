/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.Create
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  dev.simulated_team.simulated.util.SimMathUtils
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.joml.AxisAngle4d
 *  org.joml.Matrix3f
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.eriksonn.aeronautics.content.blocks.propeller.small.smart_propeller;

import com.simibubi.create.Create;
import dev.eriksonn.aeronautics.config.AeroConfig;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.BasePropellerBlockEntity;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.smart_propeller.SmartPropellerBlock;
import dev.eriksonn.aeronautics.content.particle.PropellerAirParticleData;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.util.SimMathUtils;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4d;
import org.joml.Matrix3f;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class SmartPropellerBlockEntity
extends BasePropellerBlockEntity {
    public final Vector3d thrustDir;
    public LerpedFloat hingeAngle;

    public SmartPropellerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        this.hingeAngle = LerpedFloat.linear().startWithValue((Boolean)state.getValue((Property)SmartPropellerBlock.CEILING) != false ? 180.0 : 0.0).chase(0.0, (double)0.1f, LerpedFloat.Chaser.LINEAR);
        this.thrustDir = new Vector3d();
        this.prop.setThrustDirection((Vector3dc)this.thrustDir);
    }

    @Override
    public double getConfigThrust() {
        return (Double)AeroConfig.server().physics.smartPropellerThrust.get();
    }

    @Override
    public double getConfigAirflow() {
        return (Double)AeroConfig.server().physics.smartPropellerAirflow.get();
    }

    @Override
    public float getRadius() {
        return 1.0f;
    }

    @Override
    public float getOffset() {
        return 0.625f;
    }

    @Override
    public Direction getBlockDirection() {
        return Direction.UP;
    }

    @Override
    public void tick() {
        super.tick();
        this.hingeAngle.tickChaser();
        this.hingeAngle.setValue((double)this.getHingeAngle((Direction.Axis)this.getBlockState().getValue(SmartPropellerBlock.HORIZONTAL_AXIS), this.hingeAngle.getValue()));
        this.setThrustDirection();
    }

    public void sable$physicsTick(ServerSubLevel subLevel, RigidBodyHandle handle, double timeStep) {
        this.setThrustDirection();
        if (this.isActive()) {
            super.applyForces(subLevel, JOMLConversion.toMojang((Vector3dc)this.thrustDir), timeStep);
        }
    }

    private void setThrustDirection() {
        Vector3d thrustDirection = new Vector3d(0.0, 1.0, 0.0);
        Direction dir = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)((Direction.Axis)this.getBlockState().getValue((Property)BlockStateProperties.HORIZONTAL_AXIS))).getClockWise();
        thrustDirection.rotate((Quaterniondc)new Quaterniond(new AxisAngle4d(-Math.toRadians(this.hingeAngle.getValue()), (double)dir.getStepX(), (double)dir.getStepY(), (double)dir.getStepZ())));
        this.thrustDir.set((Vector3dc)thrustDirection);
    }

    @Override
    public void onActiveTick() {
        this.prop.pushEntities();
        this.spawnParticles();
    }

    public void spawnParticles() {
        Direction dir = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)((Direction.Axis)this.getBlockState().getValue((Property)BlockStateProperties.HORIZONTAL_AXIS))).getClockWise();
        Quaterniond rot = new Quaterniond(new AxisAngle4d(-Math.toRadians(this.hingeAngle.getValue()), (double)dir.getStepX(), (double)dir.getStepY(), (double)dir.getStepZ()));
        double particleCount = 1.0f + Create.RANDOM.nextFloat() - 1.0f;
        particleCount = Math.min(particleCount, 10.0);
        int i = 0;
        while ((double)i < particleCount) {
            double R = (double)this.getRadius() * Math.sqrt(Create.RANDOM.nextFloat());
            double angle = Math.PI * 2 * (double)Create.RANDOM.nextFloat();
            Vec3 particlePos = new Vec3(Math.cos(angle) * R, (double)this.getOffset(), Math.sin(angle) * R);
            Vec3 speedVector = new Vec3(0.0, this.getAirflow() / 40.0, 0.0);
            particlePos = SimMathUtils.rotateQuatReverse((Vec3)particlePos, (Quaterniond)rot);
            speedVector = SimMathUtils.rotateQuatReverse((Vec3)speedVector, (Quaterniond)rot);
            particlePos = particlePos.add(VecHelper.getCenterOf((Vec3i)this.getBlockPos()));
            this.level.addParticle((ParticleOptions)new PropellerAirParticleData(true, false), particlePos.x, particlePos.y, particlePos.z, speedVector.x(), speedVector.y(), speedVector.z());
            ++i;
        }
    }

    public float getLerpedHingeAngle(float partialTick) {
        return this.hingeAngle.getValue(partialTick);
    }

    public float getHingeAngle(Direction.Axis horizontal, float hingeAngle) {
        SubLevel subLevel = Sable.HELPER.getContaining((BlockEntity)this);
        if (subLevel != null) {
            Quaterniond Q = new Quaterniond((Quaterniondc)subLevel.logicalPose().orientation());
            Quaterniond pendulumOrientation = new Quaterniond();
            pendulumOrientation.set((Quaterniondc)Q);
            if (horizontal == Direction.Axis.Z) {
                pendulumOrientation.mul((Quaterniondc)new Quaterniond(new AxisAngle4d(Math.toRadians(90.0), 0.0, 1.0, 0.0)));
            }
            Matrix3f rotMatrix = new Matrix3f();
            rotMatrix.set((Quaterniondc)pendulumOrientation);
            float pitch = (float)Math.atan2(rotMatrix.m01, rotMatrix.m11);
            hingeAngle = -((float)Math.toDegrees(pitch));
            if (horizontal == Direction.Axis.X) {
                hingeAngle *= -1.0f;
            }
            hingeAngle = Mth.clamp((float)hingeAngle, (float)-45.0f, (float)45.0f);
            if (((Boolean)this.getBlockState().getValue((Property)SmartPropellerBlock.CEILING)).booleanValue()) {
                hingeAngle = 180.0f - hingeAngle;
            }
        }
        if (subLevel == null) {
            hingeAngle = (Boolean)this.getBlockState().getValue((Property)SmartPropellerBlock.CEILING) != false ? 180.0f : 0.0f;
        }
        return hingeAngle;
    }
}
