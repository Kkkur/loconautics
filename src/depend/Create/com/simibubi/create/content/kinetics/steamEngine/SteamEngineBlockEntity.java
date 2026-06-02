/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.steamEngine;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.steamEngine.PoweredShaftBlockEntity;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlock;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineValueBox;
import com.simibubi.create.content.kinetics.steamEngine.SteamJetParticleData;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import java.lang.ref.WeakReference;
import java.util.List;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class SteamEngineBlockEntity
extends SmartBlockEntity
implements IHaveGoggleInformation {
    protected ScrollOptionBehaviour<WindmillBearingBlockEntity.RotationDirection> movementDirection;
    public WeakReference<PoweredShaftBlockEntity> target;
    public WeakReference<FluidTankBlockEntity> source = new WeakReference<Object>(null);
    float prevAngle = 0.0f;

    public SteamEngineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.target = new WeakReference<Object>(null);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.movementDirection = new ScrollOptionBehaviour<WindmillBearingBlockEntity.RotationDirection>(WindmillBearingBlockEntity.RotationDirection.class, (Component)CreateLang.translateDirect("contraptions.windmill.rotation_direction", new Object[0]), this, new SteamEngineValueBox());
        this.movementDirection.onlyActiveWhen(() -> {
            PoweredShaftBlockEntity shaft = this.getShaft();
            return shaft == null || !shaft.hasSource();
        });
        this.movementDirection.withCallback($ -> this.onDirectionChanged());
        behaviours.add(this.movementDirection);
        this.registerAwardables(behaviours, AllAdvancements.STEAM_ENGINE);
    }

    private void onDirectionChanged() {
    }

    @Override
    public void tick() {
        int conveyedSpeedLevel;
        float efficiency;
        super.tick();
        FluidTankBlockEntity tank = this.getTank();
        PoweredShaftBlockEntity shaft = this.getShaft();
        if (tank == null || shaft == null || !this.isValid()) {
            if (this.level.isClientSide()) {
                return;
            }
            if (shaft == null) {
                return;
            }
            if (!shaft.getBlockPos().subtract((Vec3i)this.worldPosition).equals((Object)shaft.enginePos)) {
                return;
            }
            if (shaft.engineEfficiency == 0.0f) {
                return;
            }
            Direction facing = SteamEngineBlock.getFacing(this.getBlockState());
            if (this.level.isLoaded(this.worldPosition.relative(facing.getOpposite()))) {
                shaft.update(this.worldPosition, 0, 0.0f);
            }
            return;
        }
        boolean verticalTarget = false;
        BlockState shaftState = shaft.getBlockState();
        Direction.Axis targetAxis = Direction.Axis.X;
        Block block = shaftState.getBlock();
        if (block instanceof IRotate) {
            IRotate ir = (IRotate)block;
            targetAxis = ir.getRotationAxis(shaftState);
        }
        verticalTarget = targetAxis == Direction.Axis.Y;
        BlockState blockState = this.getBlockState();
        if (!AllBlocks.STEAM_ENGINE.has(blockState)) {
            return;
        }
        Direction facing = SteamEngineBlock.getFacing(blockState);
        if (facing.getAxis() == Direction.Axis.Y) {
            facing = (Direction)blockState.getValue((Property)SteamEngineBlock.FACING);
        }
        if ((efficiency = Mth.clamp((float)tank.boiler.getEngineEfficiency(tank.getTotalTankSize()), (float)0.0f, (float)1.0f)) > 0.0f) {
            this.award(AllAdvancements.STEAM_ENGINE);
        }
        int n = efficiency == 0.0f ? 1 : (conveyedSpeedLevel = verticalTarget ? 1 : (int)GeneratingKineticBlockEntity.convertToDirection(1.0f, facing));
        if (targetAxis == Direction.Axis.Z) {
            conveyedSpeedLevel *= -1;
        }
        if (this.movementDirection.get() == WindmillBearingBlockEntity.RotationDirection.COUNTER_CLOCKWISE) {
            conveyedSpeedLevel *= -1;
        }
        float shaftSpeed = shaft.getTheoreticalSpeed();
        if (shaft.hasSource() && shaftSpeed != 0.0f && conveyedSpeedLevel != 0 && shaftSpeed > 0.0f != conveyedSpeedLevel > 0) {
            this.movementDirection.setValue(1 - this.movementDirection.get().ordinal());
            conveyedSpeedLevel *= -1;
        }
        shaft.update(this.worldPosition, conveyedSpeedLevel, efficiency);
        if (!this.level.isClientSide) {
            return;
        }
        CatnipServices.PLATFORM.executeOnClientOnly(() -> this::spawnParticles);
    }

    @Override
    public void remove() {
        PoweredShaftBlockEntity shaft = this.getShaft();
        if (shaft != null) {
            shaft.remove(this.worldPosition);
        }
        super.remove();
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().inflate(2.0);
    }

    public PoweredShaftBlockEntity getShaft() {
        PoweredShaftBlockEntity shaft = (PoweredShaftBlockEntity)this.target.get();
        if (shaft == null || shaft.isRemoved() || !shaft.canBePoweredBy(this.worldPosition)) {
            PoweredShaftBlockEntity ps;
            Direction facing;
            BlockEntity anyShaftAt;
            if (shaft != null) {
                this.target = new WeakReference<Object>(null);
            }
            if ((anyShaftAt = this.level.getBlockEntity(this.worldPosition.relative(facing = SteamEngineBlock.getFacing(this.getBlockState()), 2))) instanceof PoweredShaftBlockEntity && (ps = (PoweredShaftBlockEntity)anyShaftAt).canBePoweredBy(this.worldPosition)) {
                shaft = ps;
                this.target = new WeakReference<PoweredShaftBlockEntity>(shaft);
            }
        }
        return shaft;
    }

    public FluidTankBlockEntity getTank() {
        FluidTankBlockEntity tank = (FluidTankBlockEntity)this.source.get();
        if (tank == null || tank.isRemoved()) {
            Direction facing;
            BlockEntity be;
            if (tank != null) {
                this.source = new WeakReference<Object>(null);
            }
            if ((be = this.level.getBlockEntity(this.worldPosition.relative((facing = SteamEngineBlock.getFacing(this.getBlockState())).getOpposite()))) instanceof FluidTankBlockEntity) {
                FluidTankBlockEntity tankBe;
                tank = tankBe = (FluidTankBlockEntity)be;
                this.source = new WeakReference<FluidTankBlockEntity>(tank);
            }
        }
        if (tank == null) {
            return null;
        }
        return tank.getControllerBE();
    }

    public boolean isValid() {
        Direction dir = SteamEngineBlock.getConnectedDirection(this.getBlockState()).getOpposite();
        Level level = this.getLevel();
        if (level == null) {
            return false;
        }
        return level.getBlockState(this.getBlockPos().relative(dir)).is((Block)AllBlocks.FLUID_TANK.get());
    }

    @OnlyIn(value=Dist.CLIENT)
    private void spawnParticles() {
        FluidTankBlockEntity controller;
        float angle;
        Float targetAngle = this.getTargetAngle();
        PoweredShaftBlockEntity ste = (PoweredShaftBlockEntity)this.target.get();
        if (ste == null) {
            return;
        }
        if (!ste.isPoweredBy(this.worldPosition) || ste.engineEfficiency == 0.0f) {
            return;
        }
        if (targetAngle == null) {
            return;
        }
        angle += (angle = AngleHelper.deg((double)targetAngle.floatValue())) < 0.0f ? -105.0f : 285.0f;
        angle %= 360.0f;
        PoweredShaftBlockEntity shaft = this.getShaft();
        if (shaft == null || shaft.getSpeed() == 0.0f) {
            return;
        }
        if (!(!(angle >= 0.0f) || this.prevAngle > 180.0f && angle < 180.0f)) {
            this.prevAngle = angle;
            return;
        }
        if (!(!(angle < 0.0f) || this.prevAngle < -180.0f && angle > -180.0f)) {
            this.prevAngle = angle;
            return;
        }
        FluidTankBlockEntity sourceBE = (FluidTankBlockEntity)this.source.get();
        if (sourceBE != null && (controller = sourceBE.getControllerBE()) != null && controller.boiler != null) {
            controller.boiler.queueSoundOnSide(this.worldPosition, SteamEngineBlock.getFacing(this.getBlockState()));
        }
        Direction facing = SteamEngineBlock.getFacing(this.getBlockState());
        Vec3 offset = VecHelper.rotate((Vec3)new Vec3(0.0, 0.0, 1.0).add(VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)this.level.random, (float)1.0f).multiply(1.0, 1.0, 0.0).normalize().scale(0.5)), (double)AngleHelper.verticalAngle((Direction)facing), (Direction.Axis)Direction.Axis.X);
        offset = VecHelper.rotate((Vec3)offset, (double)AngleHelper.horizontalAngle((Direction)facing), (Direction.Axis)Direction.Axis.Y);
        Vec3 v = offset.scale(0.5).add(Vec3.atCenterOf((Vec3i)this.worldPosition));
        Vec3 m = offset.subtract(Vec3.atLowerCornerOf((Vec3i)facing.getNormal()).scale(0.75));
        this.level.addParticle((ParticleOptions)new SteamJetParticleData(1.0f), v.x, v.y, v.z, m.x, m.y, m.z);
        this.prevAngle = angle;
    }

    @OnlyIn(value=Dist.CLIENT)
    @Nullable
    public Float getTargetAngle() {
        float angle = 0.0f;
        BlockState blockState = this.getBlockState();
        if (!AllBlocks.STEAM_ENGINE.has(blockState)) {
            return null;
        }
        Direction facing = SteamEngineBlock.getFacing(blockState);
        PoweredShaftBlockEntity shaft = this.getShaft();
        Direction.Axis facingAxis = facing.getAxis();
        Direction.Axis axis = Direction.Axis.Y;
        if (shaft == null) {
            return null;
        }
        axis = KineticBlockEntityRenderer.getRotationAxisOf(shaft);
        angle = KineticBlockEntityRenderer.getAngleForBe(shaft, shaft.getBlockPos(), axis);
        if (axis == facingAxis) {
            return null;
        }
        if (axis.isHorizontal() && facingAxis == Direction.Axis.X ^ facing.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            angle *= -1.0f;
        }
        if (axis == Direction.Axis.X && facing == Direction.DOWN) {
            angle *= -1.0f;
        }
        return Float.valueOf(angle);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        PoweredShaftBlockEntity shaft = this.getShaft();
        return shaft == null ? false : shaft.addToEngineTooltip(tooltip, isPlayerSneaking);
    }
}
