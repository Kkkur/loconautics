/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllBlockEntityTypes
 *  com.simibubi.create.content.kinetics.fan.EncasedFanBlock
 *  com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity
 *  com.simibubi.create.content.kinetics.fan.NozzleBlockEntity
 *  com.simibubi.create.infrastructure.config.AllConfigs
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.BlockParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.create.nozzles;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlock;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import com.simibubi.create.content.kinetics.fan.NozzleBlockEntity;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.force.ForceGroup;
import dev.ryanhcode.sable.api.physics.force.ForceGroups;
import dev.ryanhcode.sable.api.physics.force.ForceTotal;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.neoforge.mixin.compatibility.create.nozzle.NozzleBlockEntityAccessor;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class NozzleHoveringHelper {
    public static List<Couple<Vec3>> gatherRaycastPoints(BlockState state) {
        if (state.hasProperty((Property)BlockStateProperties.FACING)) {
            ArrayList<Couple<Vec3>> rayPoints = new ArrayList<Couple<Vec3>>();
            Direction facing = (Direction)state.getValue((Property)BlockStateProperties.FACING);
            Direction startingDir = facing.getAxis() == Direction.Axis.Y ? Direction.NORTH : facing.getClockWise();
            int horizontalSamplePoints = 6;
            int theta = 60;
            double startScaling = 0.8;
            double endScaling = 8.0;
            for (boolean diagonal : Iterate.trueAndFalse) {
                for (int i = 0; i < 6; ++i) {
                    Vec3 start = Vec3.atLowerCornerOf((Vec3i)startingDir.getNormal()).scale(0.8).add(0.5, 0.5, 0.5);
                    if (diagonal) {
                        double angle;
                        Direction.Axis axis;
                        if (facing.getAxis().isHorizontal()) {
                            axis = Direction.Axis.Y;
                            angle = 45.0;
                        } else {
                            axis = startingDir.getClockWise().getAxis();
                            angle = facing.getAxisDirection().getStep() * 45;
                        }
                        start = VecHelper.rotateCentered((Vec3)start, (double)angle, (Direction.Axis)axis);
                    }
                    start = VecHelper.rotateCentered((Vec3)start, (double)(60 * i), (Direction.Axis)facing.getAxis());
                    Vec3 end = start.add(start.subtract(0.5, 0.5, 0.5).scale(8.0));
                    rayPoints.add((Couple<Vec3>)Couple.create((Object)start, (Object)end));
                }
            }
            Vec3 start = Vec3.atLowerCornerOf((Vec3i)facing.getNormal()).scale(0.8).add(0.5, 0.5, 0.5);
            Vec3 end = start.add(start.subtract(0.5, 0.5, 0.5).scale(8.0));
            rayPoints.add(Couple.create((Object)start, (Object)end));
            return rayPoints;
        }
        return null;
    }

    @Nullable
    public static Vector3d gatherForceFromRays(SubLevel parentSublevel, double timeStep, Level level, BlockPos blockStart, NozzleBlockEntity nbe, List<Couple<Vec3>> rayPoints) {
        if (((NozzleBlockEntityAccessor)nbe).getRange() == 0.0f) {
            return null;
        }
        Optional be = level.getBlockEntity(blockStart.relative(((Direction)nbe.getBlockState().getValue((Property)BlockStateProperties.FACING)).getOpposite()), (BlockEntityType)AllBlockEntityTypes.ENCASED_FAN.get());
        if (be.isPresent()) {
            EncasedFanBlockEntity fbe = (EncasedFanBlockEntity)be.get();
            Vector3d force = new Vector3d();
            Couple<Vec3> firstRay = rayPoints.getFirst();
            double startEndDistance = ((Vec3)firstRay.getSecond()).subtract((Vec3)firstRay.getFirst()).length();
            Vec3 blockCorner = Vec3.atLowerCornerOf((Vec3i)blockStart);
            for (Couple<Vec3> rayPoint : rayPoints) {
                double inverseHitPercentage;
                ActiveSableCompanion helper;
                SubLevel hitSublevel;
                Vec3 end;
                Vec3 start = blockCorner.add((Vec3)rayPoint.getFirst());
                ClipContext context = new ClipContext(start, end = blockCorner.add((Vec3)rayPoint.getSecond()), ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, CollisionContext.empty());
                BlockHitResult clip = level.clip(context);
                if (clip.getType() == HitResult.Type.MISS || (hitSublevel = (helper = Sable.HELPER).getContaining(level, (Vec3i)clip.getBlockPos())) == parentSublevel) continue;
                Vec3 hitDiff = helper.projectOutOfSubLevel(level, clip.getLocation()).subtract(helper.projectOutOfSubLevel(level, start));
                if (clip.isInside()) {
                    inverseHitPercentage = 1.0;
                } else {
                    float curveScaling = 2.0f;
                    inverseHitPercentage = Math.clamp(2.0 - hitDiff.length() / startEndDistance * 2.0, 0.0, 1.0);
                }
                Vec3 modifiedDiff = hitDiff.normalize().scale(inverseHitPercentage).scale(1.0 / (double)rayPoints.size());
                force.add(modifiedDiff.x, modifiedDiff.y, modifiedDiff.z);
                if (!(hitSublevel instanceof ServerSubLevel)) continue;
                ServerSubLevel hitServerSubLevel = (ServerSubLevel)hitSublevel;
                ForceTotal forceTotal = hitServerSubLevel.getOrCreateQueuedForceGroup((ForceGroup)ForceGroups.PROPULSION.get()).getForceTotal();
                Vector3d impulseLocation = JOMLConversion.toJOML((Position)clip.getLocation());
                Vector3d impulse = hitServerSubLevel.logicalPose().transformNormalInverse(JOMLConversion.toJOML((Position)modifiedDiff)).mul(-1.0).mul(NozzleHoveringHelper.getFanMagnitudeCalculation(parentSublevel, level, fbe) * timeStep);
                forceTotal.applyImpulseAtPoint(hitServerSubLevel.getMassTracker(), (Vector3dc)impulseLocation, (Vector3dc)impulse);
            }
            if (force.length() > 1.0E-8) {
                force.mul(NozzleHoveringHelper.getFanMagnitudeCalculation(parentSublevel, level, fbe) * timeStep);
                parentSublevel.logicalPose().transformNormalInverse(force);
            }
            return force;
        }
        return null;
    }

    private static double getFanMagnitudeCalculation(SubLevel parentSublevel, Level level, EncasedFanBlockEntity fbe) {
        int maxSpeed;
        float halfSpeed;
        float scale = ((Direction)fbe.getBlockState().getValue((Property)EncasedFanBlock.FACING)).getAxisDirection() == Direction.AxisDirection.POSITIVE ? -1.0f : 1.0f;
        double airPressure = DimensionPhysicsData.getAirPressure(level, (Vector3dc)parentSublevel.logicalPose().transformPosition(JOMLConversion.atCenterOf((Vec3i)fbe.getBlockPos())));
        int magnitude = 5;
        int softScaling = 4;
        float signumBefore = Math.signum(fbe.getSpeed());
        float speed = Math.abs(fbe.getSpeed());
        if (speed >= (halfSpeed = (float)(maxSpeed = ((Integer)AllConfigs.server().kinetics.maxRotationSpeed.get()).intValue()) / 2.0f)) {
            speed = (speed - halfSpeed) / 4.0f + halfSpeed;
        }
        return (double)(5.0f * scale * (speed *= signumBefore)) * airPressure;
    }

    public static void spawnWindHitParticle(Level level, SubLevel subLevel, BlockHitResult clip, Vector3dc origin, double airSpeed) {
        Vector3d end = JOMLConversion.toJOML((Position)clip.getLocation());
        if (clip.getType() != HitResult.Type.MISS && origin.distanceSquared(end.x, end.y, end.z) > 1.0) {
            BlockState hitState = level.getBlockState(clip.getBlockPos());
            Fluid fluid = level.getFluidState(clip.getBlockPos()).getType();
            Vector3d start = new Vector3d(origin);
            if (subLevel != null) {
                subLevel.logicalPose().transformPosition(start);
            }
            Vector3d normal = new Vector3d((double)clip.getDirection().getStepX(), (double)clip.getDirection().getStepY(), (double)clip.getDirection().getStepZ());
            SubLevel other = Sable.HELPER.getContaining(level, (Vec3i)clip.getBlockPos());
            if (other != null) {
                other.logicalPose().transformNormal(normal);
                other.logicalPose().transformPosition(end);
            }
            Vector3d offset = new Vector3d(level.random.nextDouble() * 2.0 - 1.0, level.random.nextDouble() * 2.0 - 1.0, level.random.nextDouble() * 2.0 - 1.0);
            NozzleHoveringHelper.projectOntoPlane(offset, (Vector3dc)normal, 1.0);
            end.add((Vector3dc)offset);
            Vector3d delta = end.sub((Vector3dc)start, new Vector3d());
            Vector3d particleVelocity = NozzleHoveringHelper.projectOntoPlane(new Vector3d((Vector3dc)delta), (Vector3dc)normal, 1.0);
            particleVelocity.mul(airSpeed);
            particleVelocity.fma(0.25, (Vector3dc)normal);
            end.fma(0.1, (Vector3dc)normal);
            if (other != null) {
                other.logicalPose().orientation().transformInverse(particleVelocity);
            }
            level.addParticle((ParticleOptions)ParticleTypes.DUST_PLUME, end.x, end.y, end.z, particleVelocity.x, particleVelocity.y, particleVelocity.z);
            if (hitState.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
                level.addParticle((ParticleOptions)new BlockParticleOption(ParticleTypes.BLOCK, hitState), end.x, end.y, end.z, particleVelocity.x, particleVelocity.y, particleVelocity.z);
            } else if (fluid.isSame((Fluid)Fluids.WATER)) {
                level.addParticle((ParticleOptions)ParticleTypes.SPLASH, end.x, end.y, end.z, 0.0, 0.0, 0.0);
                if (level.getRandom().nextDouble() < 0.2) {
                    level.addParticle((ParticleOptions)ParticleTypes.BUBBLE, end.x, end.y, end.z, 0.0, 0.0, 0.0);
                }
            } else if (fluid.isSame((Fluid)Fluids.LAVA)) {
                level.addParticle((ParticleOptions)ParticleTypes.SMOKE, end.x, end.y, end.z, 0.0, 0.0, 0.0);
                if (level.getRandom().nextDouble() < 0.2) {
                    level.addParticle((ParticleOptions)ParticleTypes.LAVA, end.x, end.y, end.z, 0.0, 0.0, 0.0);
                }
            }
        }
    }

    private static Vector3d projectOntoPlane(Vector3d x, Vector3dc planeNormal, double scale) {
        return x.fma(-scale * x.dot(planeNormal), planeNormal);
    }
}
