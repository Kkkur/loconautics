/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.AngleHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.contraptions.bearing;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.IControlContraption;
import com.simibubi.create.content.contraptions.IDisplayAssemblyExceptions;
import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import com.simibubi.create.content.contraptions.bearing.IBearingBlockEntity;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencerInstructions;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import java.util.List;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class MechanicalBearingBlockEntity
extends GeneratingKineticBlockEntity
implements IBearingBlockEntity,
IDisplayAssemblyExceptions {
    protected ScrollOptionBehaviour<IControlContraption.RotationMode> movementMode;
    protected ControlledContraptionEntity movedContraption;
    protected float angle;
    protected boolean running;
    protected boolean assembleNextTick;
    protected float clientAngleDiff;
    protected AssemblyException lastException;
    protected double sequencedAngleLimit;
    private float prevAngle;

    public MechanicalBearingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.setLazyTickRate(3);
        this.sequencedAngleLimit = -1.0;
    }

    @Override
    public boolean isWoodenTop() {
        return false;
    }

    @Override
    protected boolean syncSequenceContext() {
        return true;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.movementMode = new ScrollOptionBehaviour<IControlContraption.RotationMode>(IControlContraption.RotationMode.class, (Component)CreateLang.translateDirect("contraptions.movement_mode", new Object[0]), this, this.getMovementModeSlot());
        behaviours.add(this.movementMode);
        this.registerAwardables(behaviours, AllAdvancements.CONTRAPTION_ACTORS);
    }

    @Override
    public void remove() {
        if (!this.level.isClientSide) {
            this.disassemble();
        }
        super.remove();
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putBoolean("Running", this.running);
        compound.putFloat("Angle", this.angle);
        if (this.sequencedAngleLimit >= 0.0) {
            compound.putDouble("SequencedAngleLimit", this.sequencedAngleLimit);
        }
        AssemblyException.write(compound, registries, this.lastException);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (this.wasMoved) {
            super.read(compound, registries, clientPacket);
            return;
        }
        float angleBefore = this.angle;
        this.running = compound.getBoolean("Running");
        this.angle = compound.getFloat("Angle");
        this.sequencedAngleLimit = compound.contains("SequencedAngleLimit") ? compound.getDouble("SequencedAngleLimit") : -1.0;
        this.lastException = AssemblyException.read(compound, registries);
        super.read(compound, registries, clientPacket);
        if (!clientPacket) {
            return;
        }
        if (this.running) {
            if (this.movedContraption == null || !this.movedContraption.isStalled()) {
                this.clientAngleDiff = AngleHelper.getShortestAngleDiff((double)angleBefore, (double)this.angle);
                this.angle = angleBefore;
            }
        } else {
            this.movedContraption = null;
        }
    }

    @Override
    public float getInterpolatedAngle(float partialTicks) {
        if (this.isVirtual()) {
            return Mth.lerp((float)(partialTicks + 0.5f), (float)this.prevAngle, (float)this.angle);
        }
        if (this.movedContraption == null || this.movedContraption.isStalled() || !this.running) {
            partialTicks = 0.0f;
        }
        float angularSpeed = this.getAngularSpeed();
        if (this.sequencedAngleLimit >= 0.0) {
            angularSpeed = (float)Mth.clamp((double)angularSpeed, (double)(-this.sequencedAngleLimit), (double)this.sequencedAngleLimit);
        }
        return Mth.lerp((float)partialTicks, (float)this.angle, (float)(this.angle + angularSpeed));
    }

    @Override
    public void onSpeedChanged(float prevSpeed) {
        super.onSpeedChanged(prevSpeed);
        this.assembleNextTick = true;
        this.sequencedAngleLimit = -1.0;
        if (this.movedContraption != null && Math.signum(prevSpeed) != Math.signum(this.getSpeed()) && prevSpeed != 0.0f) {
            if (!this.movedContraption.isStalled()) {
                this.angle = Math.round(this.angle);
                this.applyRotation();
            }
            this.movedContraption.getContraption().stop(this.level);
        }
        if (!this.isWindmill() && this.sequenceContext != null && this.sequenceContext.instruction() == SequencerInstructions.TURN_ANGLE) {
            this.sequencedAngleLimit = this.sequenceContext.getEffectiveValue(this.getTheoreticalSpeed());
        }
    }

    public float getAngularSpeed() {
        float speed = MechanicalBearingBlockEntity.convertToAngular(this.isWindmill() ? this.getGeneratedSpeed() : this.getSpeed());
        if (this.getSpeed() == 0.0f) {
            speed = 0.0f;
        }
        if (this.level.isClientSide) {
            speed *= ServerSpeedProvider.get();
            speed += this.clientAngleDiff / 3.0f;
        }
        return speed;
    }

    @Override
    public AssemblyException getLastAssemblyException() {
        return this.lastException;
    }

    protected boolean isWindmill() {
        return false;
    }

    @Override
    public BlockPos getBlockPosition() {
        return this.worldPosition;
    }

    public void assemble() {
        if (!(this.level.getBlockState(this.worldPosition).getBlock() instanceof BearingBlock)) {
            return;
        }
        Direction direction = (Direction)this.getBlockState().getValue((Property)BearingBlock.FACING);
        BearingContraption contraption = new BearingContraption(this.isWindmill(), direction);
        try {
            if (!contraption.assemble(this.level, this.worldPosition)) {
                return;
            }
            this.lastException = null;
        }
        catch (AssemblyException e) {
            this.lastException = e;
            this.sendData();
            return;
        }
        if (this.isWindmill()) {
            this.award(AllAdvancements.WINDMILL);
        }
        if (contraption.getSailBlocks() >= 128) {
            this.award(AllAdvancements.WINDMILL_MAXED);
        }
        contraption.removeBlocksFromWorld(this.level, BlockPos.ZERO);
        this.movedContraption = ControlledContraptionEntity.create(this.level, this, contraption);
        BlockPos anchor = this.worldPosition.relative(direction);
        this.movedContraption.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
        this.movedContraption.setRotationAxis(direction.getAxis());
        this.level.addFreshEntity((Entity)this.movedContraption);
        AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(this.level, (Vec3i)this.worldPosition);
        if (contraption.containsBlockBreakers()) {
            this.award(AllAdvancements.CONTRAPTION_ACTORS);
        }
        this.running = true;
        this.angle = 0.0f;
        this.sendData();
        this.updateGeneratedRotation();
    }

    public void disassemble() {
        if (!this.running && this.movedContraption == null) {
            return;
        }
        this.angle = 0.0f;
        this.sequencedAngleLimit = -1.0;
        if (this.isWindmill()) {
            this.applyRotation();
        }
        if (this.movedContraption != null) {
            this.movedContraption.disassemble();
            AllSoundEvents.CONTRAPTION_DISASSEMBLE.playOnServer(this.level, (Vec3i)this.worldPosition);
        }
        this.movedContraption = null;
        this.running = false;
        this.updateGeneratedRotation();
        this.assembleNextTick = false;
        this.sendData();
    }

    @Override
    public void tick() {
        super.tick();
        this.prevAngle = this.angle;
        if (this.level.isClientSide) {
            this.clientAngleDiff /= 2.0f;
        }
        if (!this.level.isClientSide && this.assembleNextTick) {
            this.assembleNextTick = false;
            if (this.running) {
                boolean canDisassemble;
                boolean bl = canDisassemble = this.movementMode.get() == IControlContraption.RotationMode.ROTATE_PLACE || this.isNearInitialAngle() && this.movementMode.get() == IControlContraption.RotationMode.ROTATE_PLACE_RETURNED;
                if (this.speed == 0.0f && (canDisassemble || this.movedContraption == null || this.movedContraption.getContraption().getBlocks().isEmpty())) {
                    if (this.movedContraption != null) {
                        this.movedContraption.getContraption().stop(this.level);
                    }
                    this.disassemble();
                    return;
                }
            } else {
                if (this.speed == 0.0f && !this.isWindmill()) {
                    return;
                }
                this.assemble();
            }
        }
        if (!this.running) {
            return;
        }
        if (this.movedContraption == null || !this.movedContraption.isStalled()) {
            float angularSpeed = this.getAngularSpeed();
            if (this.sequencedAngleLimit >= 0.0) {
                angularSpeed = (float)Mth.clamp((double)angularSpeed, (double)(-this.sequencedAngleLimit), (double)this.sequencedAngleLimit);
                this.sequencedAngleLimit = Math.max(0.0, this.sequencedAngleLimit - (double)Math.abs(angularSpeed));
            }
            float newAngle = this.angle + angularSpeed;
            this.angle = newAngle % 360.0f;
        }
        this.applyRotation();
    }

    public boolean isNearInitialAngle() {
        return (double)Math.abs(this.angle) < 22.5 || (double)Math.abs(this.angle) > 337.5;
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (this.movedContraption != null && !this.level.isClientSide) {
            this.sendData();
        }
    }

    protected void applyRotation() {
        if (this.movedContraption == null) {
            return;
        }
        this.movedContraption.setAngle(this.angle);
        BlockState blockState = this.getBlockState();
        if (blockState.hasProperty((Property)BlockStateProperties.FACING)) {
            this.movedContraption.setRotationAxis(((Direction)blockState.getValue((Property)BlockStateProperties.FACING)).getAxis());
        }
    }

    @Override
    public void attach(ControlledContraptionEntity contraption) {
        BlockState blockState = this.getBlockState();
        if (!(contraption.getContraption() instanceof BearingContraption)) {
            return;
        }
        if (!blockState.hasProperty((Property)BearingBlock.FACING)) {
            return;
        }
        this.movedContraption = contraption;
        this.setChanged();
        BlockPos anchor = this.worldPosition.relative((Direction)blockState.getValue((Property)BearingBlock.FACING));
        this.movedContraption.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
        if (!this.level.isClientSide) {
            this.running = true;
            this.sendData();
        }
    }

    @Override
    public void onStall() {
        if (!this.level.isClientSide) {
            this.sendData();
        }
    }

    @Override
    public boolean isValid() {
        return !this.isRemoved();
    }

    @Override
    public boolean isAttachedTo(AbstractContraptionEntity contraption) {
        return this.movedContraption == contraption;
    }

    public boolean isRunning() {
        return this.running;
    }

    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (super.addToTooltip(tooltip, isPlayerSneaking)) {
            return true;
        }
        if (isPlayerSneaking) {
            return false;
        }
        if (!this.isWindmill() && this.getSpeed() == 0.0f) {
            return false;
        }
        if (this.running) {
            return false;
        }
        BlockState state = this.getBlockState();
        if (!(state.getBlock() instanceof BearingBlock)) {
            return false;
        }
        BlockState attachedState = this.level.getBlockState(this.worldPosition.relative((Direction)state.getValue((Property)BearingBlock.FACING)));
        if (attachedState.canBeReplaced()) {
            return false;
        }
        TooltipHelper.addHint(tooltip, "hint.empty_bearing", new Object[0]);
        return true;
    }

    @Override
    public void setAngle(float forcedAngle) {
        this.angle = forcedAngle;
    }

    public ControlledContraptionEntity getMovedContraption() {
        return this.movedContraption;
    }
}
