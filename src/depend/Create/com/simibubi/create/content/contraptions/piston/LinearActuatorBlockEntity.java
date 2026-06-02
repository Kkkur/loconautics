/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.contraptions.piston;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.ContraptionCollider;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.IControlContraption;
import com.simibubi.create.content.contraptions.IDisplayAssemblyExceptions;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencerInstructions;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class LinearActuatorBlockEntity
extends KineticBlockEntity
implements IControlContraption,
IDisplayAssemblyExceptions {
    public float offset;
    public boolean running;
    public boolean assembleNextTick;
    public boolean needsContraption;
    public AbstractContraptionEntity movedContraption;
    protected boolean forceMove;
    protected ScrollOptionBehaviour<IControlContraption.MovementMode> movementMode;
    protected boolean waitingForSpeedChange;
    protected AssemblyException lastException;
    protected double sequencedOffsetLimit;
    protected float clientOffsetDiff;

    public LinearActuatorBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        this.setLazyTickRate(3);
        this.forceMove = true;
        this.needsContraption = true;
        this.sequencedOffsetLimit = -1.0;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.movementMode = new ScrollOptionBehaviour<IControlContraption.MovementMode>(IControlContraption.MovementMode.class, (Component)CreateLang.translateDirect("contraptions.movement_mode", new Object[0]), this, this.getMovementModeSlot());
        this.movementMode.withCallback(t -> {
            this.waitingForSpeedChange = false;
        });
        behaviours.add(this.movementMode);
        this.registerAwardables(behaviours, AllAdvancements.CONTRAPTION_ACTORS);
    }

    @Override
    protected boolean syncSequenceContext() {
        return true;
    }

    @Override
    public void tick() {
        float newOffset;
        boolean contraptionPresent;
        super.tick();
        if (this.movedContraption != null && !this.movedContraption.isAlive()) {
            this.movedContraption = null;
        }
        if (this.isPassive()) {
            return;
        }
        if (this.level.isClientSide) {
            this.clientOffsetDiff *= 0.75f;
        }
        if (this.waitingForSpeedChange) {
            if (this.movedContraption != null) {
                if (this.level.isClientSide) {
                    float syncSpeed = this.clientOffsetDiff / 2.0f;
                    this.offset += syncSpeed;
                    this.movedContraption.setContraptionMotion(this.toMotionVector(syncSpeed));
                    return;
                }
                this.movedContraption.setContraptionMotion(Vec3.ZERO);
            }
            return;
        }
        if (!this.level.isClientSide && this.assembleNextTick) {
            this.assembleNextTick = false;
            if (this.running) {
                if (this.getSpeed() == 0.0f) {
                    this.tryDisassemble();
                } else {
                    this.sendData();
                }
                return;
            }
            if (this.getSpeed() != 0.0f) {
                try {
                    this.assemble();
                    this.lastException = null;
                }
                catch (AssemblyException e) {
                    this.lastException = e;
                }
            }
            this.sendData();
            return;
        }
        if (!this.running) {
            return;
        }
        boolean bl = contraptionPresent = this.movedContraption != null;
        if (this.needsContraption && !contraptionPresent) {
            return;
        }
        float movementSpeed = this.getMovementSpeed();
        boolean locked = false;
        if (this.sequencedOffsetLimit > 0.0) {
            this.sequencedOffsetLimit = Math.max(0.0, this.sequencedOffsetLimit - (double)Math.abs(movementSpeed));
            boolean bl2 = locked = this.sequencedOffsetLimit == 0.0;
        }
        if ((int)(newOffset = this.offset + movementSpeed) != (int)this.offset) {
            this.visitNewPosition();
        }
        if (locked) {
            this.forceMove = true;
            this.resetContraptionToOffset();
            this.sendData();
        }
        if (contraptionPresent && this.moveAndCollideContraption()) {
            this.movedContraption.setContraptionMotion(Vec3.ZERO);
            this.offset = this.getGridOffset(this.offset);
            this.resetContraptionToOffset();
            this.collided();
            return;
        }
        if (!contraptionPresent || !this.movedContraption.isStalled()) {
            this.offset = newOffset;
        }
        int extensionRange = this.getExtensionRange();
        if (this.offset <= 0.0f || this.offset >= (float)extensionRange) {
            float f = this.offset = this.offset <= 0.0f ? 0.0f : (float)extensionRange;
            if (!this.level.isClientSide) {
                this.moveAndCollideContraption();
                this.resetContraptionToOffset();
                this.tryDisassemble();
                if (this.waitingForSpeedChange) {
                    this.forceMove = true;
                    this.sendData();
                }
            }
            return;
        }
    }

    protected boolean isPassive() {
        return false;
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (this.movedContraption != null && !this.level.isClientSide) {
            this.sendData();
        }
    }

    protected int getGridOffset(float offset) {
        return Mth.clamp((int)((int)(offset + 0.5f)), (int)0, (int)this.getExtensionRange());
    }

    public float getInterpolatedOffset(float partialTicks) {
        float interpolatedOffset = Mth.clamp((float)(this.offset + (partialTicks - 0.5f) * this.getMovementSpeed()), (float)0.0f, (float)this.getExtensionRange());
        return interpolatedOffset;
    }

    @Override
    public void onSpeedChanged(float prevSpeed) {
        super.onSpeedChanged(prevSpeed);
        this.sequencedOffsetLimit = -1.0;
        if (this.isPassive()) {
            return;
        }
        this.assembleNextTick = true;
        this.waitingForSpeedChange = false;
        if (this.movedContraption != null && Math.signum(prevSpeed) != Math.signum(this.getSpeed()) && prevSpeed != 0.0f) {
            if (!this.movedContraption.isStalled()) {
                this.offset = Math.round(this.offset * 16.0f) / 16;
                this.resetContraptionToOffset();
            }
            this.movedContraption.getContraption().stop(this.level);
        }
        if (this.sequenceContext != null && this.sequenceContext.instruction() == SequencerInstructions.TURN_DISTANCE) {
            this.sequencedOffsetLimit = this.sequenceContext.getEffectiveValue(this.getTheoreticalSpeed());
        }
    }

    @Override
    public void remove() {
        this.remove = true;
        if (!this.level.isClientSide) {
            this.disassemble();
        }
        super.remove();
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putBoolean("Running", this.running);
        compound.putBoolean("Waiting", this.waitingForSpeedChange);
        compound.putFloat("Offset", this.offset);
        if (this.sequencedOffsetLimit >= 0.0) {
            compound.putDouble("SequencedOffsetLimit", this.sequencedOffsetLimit);
        }
        AssemblyException.write(compound, registries, this.lastException);
        super.write(compound, registries, clientPacket);
        if (clientPacket && this.forceMove) {
            compound.putBoolean("ForceMovement", this.forceMove);
            this.forceMove = false;
        }
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        boolean forceMovement = compound.contains("ForceMovement");
        float offsetBefore = this.offset;
        this.running = compound.getBoolean("Running");
        this.waitingForSpeedChange = compound.getBoolean("Waiting");
        this.offset = compound.getFloat("Offset");
        this.sequencedOffsetLimit = compound.contains("SequencedOffsetLimit") ? compound.getDouble("SequencedOffsetLimit") : -1.0;
        this.lastException = AssemblyException.read(compound, registries);
        super.read(compound, registries, clientPacket);
        if (!clientPacket) {
            return;
        }
        if (forceMovement) {
            this.resetContraptionToOffset();
        } else if (this.running) {
            this.clientOffsetDiff = this.offset - offsetBefore;
            this.offset = offsetBefore;
        }
        if (!this.running) {
            this.movedContraption = null;
        }
    }

    @Override
    public AssemblyException getLastAssemblyException() {
        return this.lastException;
    }

    public abstract void disassemble();

    protected abstract void assemble() throws AssemblyException;

    protected abstract int getExtensionRange();

    protected abstract int getInitialOffset();

    protected abstract ValueBoxTransform getMovementModeSlot();

    protected abstract Vec3 toMotionVector(float var1);

    protected abstract Vec3 toPosition(float var1);

    protected void visitNewPosition() {
    }

    protected void tryDisassemble() {
        if (this.remove) {
            this.disassemble();
            return;
        }
        if (this.getMovementMode() == IControlContraption.MovementMode.MOVE_NEVER_PLACE) {
            this.waitingForSpeedChange = true;
            return;
        }
        int initial = this.getInitialOffset();
        if ((int)(this.offset + 0.5f) != initial && this.getMovementMode() == IControlContraption.MovementMode.MOVE_PLACE_RETURNED) {
            this.waitingForSpeedChange = true;
            return;
        }
        this.disassemble();
    }

    protected IControlContraption.MovementMode getMovementMode() {
        return this.movementMode.get();
    }

    protected boolean moveAndCollideContraption() {
        if (this.movedContraption == null) {
            return false;
        }
        if (this.movedContraption.isStalled()) {
            this.movedContraption.setContraptionMotion(Vec3.ZERO);
            return false;
        }
        Vec3 motion = this.getMotionVector();
        this.movedContraption.setContraptionMotion(this.getMotionVector());
        this.movedContraption.move(motion.x, motion.y, motion.z);
        return ContraptionCollider.collideBlocks(this.movedContraption);
    }

    protected void collided() {
        if (this.level.isClientSide) {
            this.waitingForSpeedChange = true;
            return;
        }
        this.offset = this.getGridOffset(this.offset - this.getMovementSpeed());
        this.resetContraptionToOffset();
        this.tryDisassemble();
    }

    protected void resetContraptionToOffset() {
        if (this.movedContraption == null) {
            return;
        }
        if (!this.movedContraption.isAlive()) {
            return;
        }
        Vec3 vec = this.toPosition(this.offset);
        this.movedContraption.setPos(vec.x, vec.y, vec.z);
        if (this.getSpeed() == 0.0f || this.waitingForSpeedChange) {
            this.movedContraption.setContraptionMotion(Vec3.ZERO);
        }
    }

    public float getMovementSpeed() {
        float movementSpeed = Mth.clamp((float)LinearActuatorBlockEntity.convertToLinear(this.getSpeed()), (float)-0.49f, (float)0.49f) + this.clientOffsetDiff / 2.0f;
        if (this.level.isClientSide) {
            movementSpeed *= ServerSpeedProvider.get();
        }
        if (this.sequencedOffsetLimit >= 0.0) {
            movementSpeed = (float)Mth.clamp((double)movementSpeed, (double)(-this.sequencedOffsetLimit), (double)this.sequencedOffsetLimit);
        }
        return movementSpeed;
    }

    public Vec3 getMotionVector() {
        return this.toMotionVector(this.getMovementSpeed());
    }

    @Override
    public void onStall() {
        if (!this.level.isClientSide) {
            this.forceMove = true;
            this.sendData();
        }
    }

    public void onLengthBroken() {
        this.offset = 0.0f;
        this.sendData();
    }

    @Override
    public boolean isValid() {
        return !this.isRemoved();
    }

    @Override
    public void attach(ControlledContraptionEntity contraption) {
        this.movedContraption = contraption;
        if (!this.level.isClientSide) {
            this.running = true;
            this.sendData();
        }
    }

    @Override
    public boolean isAttachedTo(AbstractContraptionEntity contraption) {
        return this.movedContraption == contraption;
    }

    @Override
    public BlockPos getBlockPosition() {
        return this.worldPosition;
    }
}
