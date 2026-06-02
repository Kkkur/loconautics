/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.common.NeoForgeMod
 *  net.neoforged.neoforge.common.extensions.IEntityExtension
 *  net.neoforged.neoforge.fluids.FluidType
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.entity.entity_swimming;

import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.math.LevelReusedVectors;
import dev.ryanhcode.sable.api.math.OrientedBoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.mixinterface.entity.entity_sublevel_collision.EntityMovementExtension;
import dev.ryanhcode.sable.mixinterface.entity.entity_sublevel_collision.LevelExtension;
import dev.ryanhcode.sable.neoforge.mixinhelper.entity.SableInterimCalculation;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.entity_collision.SubLevelEntityCollision;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.extensions.IEntityExtension;
import net.neoforged.neoforge.fluids.FluidType;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Entity.class}, priority=500)
public abstract class EntityMixin
implements IEntityExtension {
    @Shadow
    private Level level;
    @Shadow
    private Vec3 position;
    @Shadow
    private FluidType forgeFluidTypeOnEyes;

    @Shadow
    public abstract boolean touchingUnloadedChunk();

    @Shadow
    public abstract AABB getBoundingBox();

    @Shadow
    @Deprecated
    public abstract boolean isPushedByFluid();

    @Shadow
    public abstract Vec3 getDeltaMovement();

    @Shadow
    public abstract void setDeltaMovement(Vec3 var1);

    @Shadow
    protected abstract void setFluidTypeHeight(FluidType var1, double var2);

    @Shadow
    public abstract BlockPos blockPosition();

    @Shadow
    public abstract Level level();

    @Shadow
    public abstract Vec3 getEyePosition();

    @Overwrite
    public void updateFluidHeightAndDoFluidPushing() {
        if (!this.touchingUnloadedChunk()) {
            AABB aabb = this.getBoundingBox().deflate(0.001);
            int i = Mth.floor((double)aabb.minX);
            int j = Mth.ceil((double)aabb.maxX);
            int k = Mth.floor((double)aabb.minY);
            int l = Mth.ceil((double)aabb.maxY);
            int i1 = Mth.floor((double)aabb.minZ);
            int j1 = Mth.ceil((double)aabb.maxZ);
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            Object2ObjectArrayMap interimCalcs = null;
            for (int l1 = i; l1 < j; ++l1) {
                for (int i2 = k; i2 < l; ++i2) {
                    for (int j2 = i1; j2 < j1; ++j2) {
                        double d1;
                        blockpos$mutableblockpos.set(l1, i2, j2);
                        FluidState fluidstate = this.level.getFluidState((BlockPos)blockpos$mutableblockpos);
                        FluidType fluidType = fluidstate.getFluidType();
                        if (fluidType.isAir() || !((d1 = (double)((float)i2 + fluidstate.getHeight((BlockGetter)this.level, (BlockPos)blockpos$mutableblockpos))) >= aabb.minY)) continue;
                        if (interimCalcs == null) {
                            interimCalcs = new Object2ObjectArrayMap();
                        }
                        SableInterimCalculation interim = (SableInterimCalculation)interimCalcs.computeIfAbsent((Object)fluidType, t -> new SableInterimCalculation());
                        interim.fluidHeight = Math.max(d1 - aabb.minY, interim.fluidHeight);
                        if (!this.isPushedByFluid(fluidType)) continue;
                        Vec3 vec31 = fluidstate.getFlow((BlockGetter)this.level, (BlockPos)blockpos$mutableblockpos);
                        if (interim.fluidHeight < 0.4) {
                            vec31 = vec31.scale(interim.fluidHeight);
                        }
                        interim.flowVector = interim.flowVector.add(vec31);
                        ++interim.blockCount;
                    }
                }
            }
            ActiveSableCompanion helper = Sable.HELPER;
            BoundingBox3d globalBounds = new BoundingBox3d(aabb);
            BoundingBox3d localBounds = new BoundingBox3d();
            Iterable<SubLevel> intersecting = helper.getAllIntersecting(this.level, (BoundingBox3dc)globalBounds);
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            Vector3d playerCenter = new Vector3d();
            Vector3d playerSize = new Vector3d();
            Quaterniond playerOrientation = new Quaterniond();
            for (SubLevel subLevel : intersecting) {
                Pose3dc pose = subLevel.lastPose();
                globalBounds.transformInverse(pose, localBounds);
                LevelReusedVectors jomlSink = ((LevelExtension)this.level).sable$getJOMLSink();
                Quaterniond localPlayerBox = pose.orientation().conjugate(playerOrientation);
                double yaw = SubLevelEntityCollision.getHitBoxYaw(pose);
                localPlayerBox.rotateY(yaw);
                OrientedBoundingBox3d playerBox = new OrientedBoundingBox3d((Vector3dc)pose.transformPositionInverse(globalBounds.center(playerCenter)), (Vector3dc)globalBounds.size(playerSize), (Quaterniondc)localPlayerBox, jomlSink);
                OrientedBoundingBox3d fluidBox = new OrientedBoundingBox3d((Vector3dc)new Vector3d(), (Vector3dc)new Vector3d(1.0), JOMLConversion.QUAT_IDENTITY, jomlSink);
                int minX = Mth.floor((double)localBounds.minX);
                int maxX = Mth.ceil((double)localBounds.maxX);
                int minY = Mth.floor((double)localBounds.minY);
                int maxY = Mth.ceil((double)localBounds.maxY);
                int minZ = Mth.floor((double)localBounds.minZ);
                int maxZ = Mth.ceil((double)localBounds.maxZ);
                double minYVertex = 3.4028234663852886E38;
                boolean hasComputedMinYVertex = false;
                for (int x = minX; x < maxX; ++x) {
                    for (int y = minY; y < maxY; ++y) {
                        for (int z = minZ; z < maxZ; ++z) {
                            mutableBlockPos.set(x, y, z);
                            FluidState fluidState = this.level.getFluidState((BlockPos)mutableBlockPos);
                            FluidType fluidType = fluidState.getFluidType();
                            if (fluidType.isAir()) continue;
                            double fluidLevelY = (float)y + fluidState.getHeight((BlockGetter)this.level, (BlockPos)mutableBlockPos);
                            if (!hasComputedMinYVertex) {
                                Vector3d[] vertices;
                                for (Vector3d vertex : vertices = playerBox.vertices(jomlSink.a)) {
                                    minYVertex = Math.min(minYVertex, vertex.y);
                                }
                                hasComputedMinYVertex = true;
                            }
                            if (!(fluidLevelY >= minYVertex)) continue;
                            fluidBox.getPosition().set((double)x + 0.5, (double)y + 0.5, (double)z + 0.5);
                            if (!(OrientedBoundingBox3d.sat(playerBox, fluidBox).lengthSquared() > 0.0)) continue;
                            if (interimCalcs == null) {
                                interimCalcs = new Object2ObjectArrayMap();
                            }
                            SableInterimCalculation interim = (SableInterimCalculation)interimCalcs.computeIfAbsent((Object)fluidType, t -> new SableInterimCalculation());
                            interim.fluidHeight = Math.max(fluidLevelY - minYVertex, interim.fluidHeight);
                            if (Sable.HELPER.getTrackingSubLevel((Entity)this) == null && helper.getContaining((Entity)this) != subLevel) {
                                ((EntityMovementExtension)((Object)this)).sable$setTrackingSubLevel(subLevel);
                            }
                            if (!this.isPushedByFluid(fluidType)) continue;
                            Vec3 flowVec = fluidState.getFlow((BlockGetter)this.level, (BlockPos)mutableBlockPos);
                            if (interim.fluidHeight < 0.4) {
                                flowVec = flowVec.scale(interim.fluidHeight);
                            }
                            flowVec = pose.transformNormal(flowVec);
                            interim.flowVector = interim.flowVector.add(flowVec);
                            ++interim.blockCount;
                        }
                    }
                }
            }
            if (interimCalcs != null) {
                interimCalcs.forEach((fluidTypex, interimx) -> {
                    if (interimx.flowVector.length() > 0.0) {
                        if (interimx.blockCount > 0) {
                            interimx.flowVector = interimx.flowVector.scale(1.0 / (double)interimx.blockCount);
                        }
                        if (!(this instanceof Player)) {
                            interimx.flowVector = interimx.flowVector.normalize();
                        }
                        Vec3 vec32 = this.getDeltaMovement();
                        interimx.flowVector = interimx.flowVector.scale(this.getFluidMotionScale((FluidType)fluidTypex));
                        double d2 = 0.003;
                        if (Math.abs(vec32.x) < 0.003 && Math.abs(vec32.z) < 0.003 && interimx.flowVector.length() < 0.0045000000000000005) {
                            interimx.flowVector = interimx.flowVector.normalize().scale(0.0045000000000000005);
                        }
                        this.setDeltaMovement(this.getDeltaMovement().add(interimx.flowVector));
                    }
                    this.setFluidTypeHeight((FluidType)fluidTypex, interimx.fluidHeight);
                });
            }
        }
    }

    public boolean canStartSwimming() {
        FluidType fluidType;
        block1: {
            SubLevel subLevel;
            Pose3dc pose;
            BlockPos localBlockPos;
            BlockPos globalBlockPos;
            Level level = this.level();
            fluidType = level.getFluidState(globalBlockPos = this.blockPosition()).getFluidType();
            if (fluidType != Fluids.EMPTY.getFluidType()) break block1;
            Iterable<SubLevel> intersecting = Sable.HELPER.getAllIntersecting(this.level, (BoundingBox3dc)new BoundingBox3d(globalBlockPos).expand(0.5));
            Iterator<SubLevel> iterator = intersecting.iterator();
            while (iterator.hasNext() && (fluidType = level.getFluidState(localBlockPos = BlockPos.containing((Position)(pose = (subLevel = iterator.next()).lastPose()).transformPositionInverse(this.position))).getFluidType()) == Fluids.EMPTY.getFluidType()) {
            }
        }
        return !this.getEyeInFluidType().isAir() && this.canSwimInFluidType(this.getEyeInFluidType()) && this.canSwimInFluidType(fluidType);
    }

    @Inject(method={"updateFluidOnEyes"}, at={@At(value="TAIL")})
    public void sable$subLevelFluidOnEyes(CallbackInfo ci) {
        if (this.forgeFluidTypeOnEyes != NeoForgeMod.EMPTY_TYPE.value() && this.forgeFluidTypeOnEyes != Fluids.EMPTY.getFluidType()) {
            return;
        }
        Vec3 globalEyePos = this.getEyePosition();
        Iterable<SubLevel> intersecting = Sable.HELPER.getAllIntersecting(this.level, (BoundingBox3dc)new BoundingBox3d(BlockPos.containing((Position)globalEyePos)).expand(0.5));
        for (SubLevel subLevel : intersecting) {
            Pose3dc pose = subLevel.lastPose();
            Vec3 localEyePos = pose.transformPositionInverse(globalEyePos);
            BlockPos blockPos = BlockPos.containing((Position)localEyePos);
            FluidState fluidState = this.level.getFluidState(blockPos);
            double e = (float)blockPos.getY() + fluidState.getHeight((BlockGetter)this.level, blockPos);
            if (!(e > localEyePos.y)) continue;
            this.forgeFluidTypeOnEyes = fluidState.getFluidType();
            if (this.forgeFluidTypeOnEyes == NeoForgeMod.EMPTY_TYPE.value() || this.forgeFluidTypeOnEyes == Fluids.EMPTY.getFluidType()) continue;
            return;
        }
    }
}
