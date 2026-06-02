/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.peripheral.PeripheralCapability
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 */
package com.simibubi.create.content.kinetics.speedController;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.ComputerCraftProxy;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.content.kinetics.speedController.SpeedControllerBlock;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import java.util.List;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class SpeedControllerBlockEntity
extends KineticBlockEntity {
    public static final int DEFAULT_SPEED = 16;
    public ScrollValueBehaviour targetSpeed;
    public AbstractComputerBehaviour computerBehaviour;
    boolean hasBracket = false;

    public SpeedControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        if (Mods.COMPUTERCRAFT.isLoaded()) {
            event.registerBlockEntity(PeripheralCapability.get(), (BlockEntityType)AllBlockEntityTypes.ROTATION_SPEED_CONTROLLER.get(), (be, context) -> be.computerBehaviour.getPeripheralCapability());
        }
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        this.updateBracket();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        Integer max = (Integer)AllConfigs.server().kinetics.maxRotationSpeed.get();
        this.targetSpeed = new KineticScrollValueBehaviour((Component)CreateLang.translateDirect("kinetics.speed_controller.rotation_speed", new Object[0]), this, new ControllerValueBoxTransform(this));
        this.targetSpeed.between(-max.intValue(), max);
        this.targetSpeed.value = 16;
        this.targetSpeed.withCallback(i -> this.updateTargetRotation());
        behaviours.add(this.targetSpeed);
        this.computerBehaviour = ComputerCraftProxy.behaviour(this);
        behaviours.add(this.computerBehaviour);
        this.registerAwardables(behaviours, AllAdvancements.SPEED_CONTROLLER);
    }

    private void updateTargetRotation() {
        if (this.hasNetwork()) {
            this.getOrCreateNetwork().remove(this);
        }
        RotationPropagator.handleRemoved(this.level, this.worldPosition, this);
        this.removeSource();
        this.attachKinetics();
        if (this.isCogwheelPresent() && this.getSpeed() != 0.0f) {
            this.award(AllAdvancements.SPEED_CONTROLLER);
        }
    }

    public static float getConveyedSpeed(KineticBlockEntity cogWheel, KineticBlockEntity speedControllerIn, boolean targetingController) {
        float compareSpeed;
        if (!(speedControllerIn instanceof SpeedControllerBlockEntity)) {
            return 0.0f;
        }
        float speed = speedControllerIn.getTheoreticalSpeed();
        float wheelSpeed = cogWheel.getTheoreticalSpeed();
        float desiredOutputSpeed = SpeedControllerBlockEntity.getDesiredOutputSpeed(cogWheel, speedControllerIn, targetingController);
        float f = compareSpeed = targetingController ? speed : wheelSpeed;
        if (desiredOutputSpeed >= 0.0f && compareSpeed >= 0.0f) {
            return Math.max(desiredOutputSpeed, compareSpeed);
        }
        if (desiredOutputSpeed < 0.0f && compareSpeed < 0.0f) {
            return Math.min(desiredOutputSpeed, compareSpeed);
        }
        return desiredOutputSpeed;
    }

    public static float getDesiredOutputSpeed(KineticBlockEntity cogWheel, KineticBlockEntity speedControllerIn, boolean targetingController) {
        SpeedControllerBlockEntity speedController = (SpeedControllerBlockEntity)speedControllerIn;
        float targetSpeed = speedController.targetSpeed.getValue();
        float speed = speedControllerIn.getTheoreticalSpeed();
        float wheelSpeed = cogWheel.getTheoreticalSpeed();
        if (targetSpeed == 0.0f) {
            return 0.0f;
        }
        if (targetingController && wheelSpeed == 0.0f) {
            return 0.0f;
        }
        if (!speedController.hasSource()) {
            if (targetingController) {
                return targetSpeed;
            }
            return 0.0f;
        }
        boolean wheelPowersController = speedController.source.equals((Object)cogWheel.getBlockPos());
        if (wheelPowersController) {
            if (targetingController) {
                return targetSpeed;
            }
            return wheelSpeed;
        }
        if (targetingController) {
            return speed;
        }
        return targetSpeed;
    }

    public void updateBracket() {
        if (this.level != null && this.level.isClientSide) {
            this.hasBracket = this.isCogwheelPresent();
        }
    }

    private boolean isCogwheelPresent() {
        BlockState stateAbove = this.level.getBlockState(this.worldPosition.above());
        return ICogWheel.isDedicatedCogWheel(stateAbove.getBlock()) && ICogWheel.isLargeCog(stateAbove) && ((Direction.Axis)stateAbove.getValue((Property)CogWheelBlock.AXIS)).isHorizontal();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.computerBehaviour.removePeripheral();
    }

    private class ControllerValueBoxTransform
    extends ValueBoxTransform.Sided {
        private ControllerValueBoxTransform(SpeedControllerBlockEntity speedControllerBlockEntity) {
        }

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace((double)8.0, (double)11.0, (double)15.5);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            if (direction.getAxis().isVertical()) {
                return false;
            }
            return state.getValue(SpeedControllerBlock.HORIZONTAL_AXIS) != direction.getAxis();
        }

        @Override
        public float getScale() {
            return 0.5f;
        }
    }
}
