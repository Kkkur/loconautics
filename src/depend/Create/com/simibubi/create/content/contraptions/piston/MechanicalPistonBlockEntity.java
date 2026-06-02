/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.contraptions.piston;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.ContraptionCollider;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.DirectionalExtenderScrollOptionSlot;
import com.simibubi.create.content.contraptions.piston.LinearActuatorBlockEntity;
import com.simibubi.create.content.contraptions.piston.MechanicalPistonBlock;
import com.simibubi.create.content.contraptions.piston.PistonContraption;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class MechanicalPistonBlockEntity
extends LinearActuatorBlockEntity {
    protected int extensionLength;

    public MechanicalPistonBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        this.extensionLength = compound.getInt("ExtensionLength");
        super.read(compound, registries, clientPacket);
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        tag.putInt("ExtensionLength", this.extensionLength);
        super.write(tag, registries, clientPacket);
    }

    @Override
    public void assemble() throws AssemblyException {
        if (!(this.level.getBlockState(this.worldPosition).getBlock() instanceof MechanicalPistonBlock)) {
            return;
        }
        Direction direction = (Direction)this.getBlockState().getValue((Property)BlockStateProperties.FACING);
        PistonContraption contraption = new PistonContraption(direction, this.getMovementSpeed() < 0.0f);
        if (!contraption.assemble(this.level, this.worldPosition)) {
            return;
        }
        Direction positive = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)direction.getAxis());
        Direction movementDirection = this.getSpeed() > 0.0f ^ direction.getAxis() != Direction.Axis.Z ? positive : positive.getOpposite();
        BlockPos anchor = contraption.anchor.relative(direction, contraption.initialExtensionProgress);
        if (ContraptionCollider.isCollidingWithWorld(this.level, contraption, anchor.relative(movementDirection), movementDirection)) {
            return;
        }
        this.extensionLength = contraption.extensionLength;
        float resultingOffset = (float)contraption.initialExtensionProgress + Math.signum(this.getMovementSpeed()) * 0.5f;
        if (resultingOffset <= 0.0f || resultingOffset >= (float)this.extensionLength) {
            return;
        }
        this.running = true;
        this.offset = contraption.initialExtensionProgress;
        this.sendData();
        this.clientOffsetDiff = 0.0f;
        BlockPos startPos = BlockPos.ZERO.relative(direction, contraption.initialExtensionProgress);
        contraption.removeBlocksFromWorld(this.level, startPos);
        this.movedContraption = ControlledContraptionEntity.create(this.getLevel(), this, contraption);
        this.resetContraptionToOffset();
        this.forceMove = true;
        this.level.addFreshEntity((Entity)this.movedContraption);
        AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(this.level, (Vec3i)this.worldPosition);
        if (contraption.containsBlockBreakers()) {
            this.award(AllAdvancements.CONTRAPTION_ACTORS);
        }
    }

    @Override
    public void disassemble() {
        if (!this.running && this.movedContraption == null) {
            return;
        }
        if (!this.remove) {
            this.getLevel().setBlock(this.worldPosition, (BlockState)this.getBlockState().setValue(MechanicalPistonBlock.STATE, (Comparable)((Object)MechanicalPistonBlock.PistonState.EXTENDED)), 19);
        }
        if (this.movedContraption != null) {
            this.resetContraptionToOffset();
            this.movedContraption.disassemble();
            AllSoundEvents.CONTRAPTION_DISASSEMBLE.playOnServer(this.level, (Vec3i)this.worldPosition);
        }
        this.running = false;
        this.movedContraption = null;
        this.sendData();
        if (this.remove) {
            ((MechanicalPistonBlock)AllBlocks.MECHANICAL_PISTON.get()).playerWillDestroy(this.level, this.worldPosition, this.getBlockState(), null);
        }
    }

    @Override
    protected void collided() {
        super.collided();
        if (!this.running && this.getMovementSpeed() > 0.0f) {
            this.assembleNextTick = true;
        }
    }

    @Override
    public float getMovementSpeed() {
        float movementSpeed = Mth.clamp((float)MechanicalPistonBlockEntity.convertToLinear(this.getSpeed()), (float)-0.49f, (float)0.49f);
        if (this.level.isClientSide) {
            movementSpeed *= ServerSpeedProvider.get();
        }
        Direction pistonDirection = (Direction)this.getBlockState().getValue((Property)BlockStateProperties.FACING);
        int movementModifier = pistonDirection.getAxisDirection().getStep() * (pistonDirection.getAxis() == Direction.Axis.Z ? -1 : 1);
        movementSpeed = movementSpeed * (float)(-movementModifier) + this.clientOffsetDiff / 2.0f;
        int extensionRange = this.getExtensionRange();
        movementSpeed = Mth.clamp((float)movementSpeed, (float)(0.0f - this.offset), (float)((float)extensionRange - this.offset));
        if (this.sequencedOffsetLimit >= 0.0) {
            movementSpeed = (float)Mth.clamp((double)movementSpeed, (double)(-this.sequencedOffsetLimit), (double)this.sequencedOffsetLimit);
        }
        return movementSpeed;
    }

    @Override
    protected int getExtensionRange() {
        return this.extensionLength;
    }

    @Override
    protected void visitNewPosition() {
    }

    @Override
    protected Vec3 toMotionVector(float speed) {
        Direction pistonDirection = (Direction)this.getBlockState().getValue((Property)BlockStateProperties.FACING);
        return Vec3.atLowerCornerOf((Vec3i)pistonDirection.getNormal()).scale((double)speed);
    }

    @Override
    protected Vec3 toPosition(float offset) {
        Vec3 position = Vec3.atLowerCornerOf((Vec3i)((Direction)this.getBlockState().getValue((Property)BlockStateProperties.FACING)).getNormal()).scale((double)offset);
        return position.add(Vec3.atLowerCornerOf((Vec3i)this.movedContraption.getContraption().anchor));
    }

    @Override
    protected ValueBoxTransform getMovementModeSlot() {
        return new DirectionalExtenderScrollOptionSlot((state, d) -> {
            Direction.Axis axis = d.getAxis();
            Direction.Axis extensionAxis = ((Direction)state.getValue((Property)MechanicalPistonBlock.FACING)).getAxis();
            Direction.Axis shaftAxis = ((IRotate)state.getBlock()).getRotationAxis((BlockState)state);
            return extensionAxis != axis && shaftAxis != axis;
        });
    }

    @Override
    protected int getInitialOffset() {
        return this.movedContraption == null ? 0 : ((PistonContraption)this.movedContraption.getContraption()).initialExtensionProgress;
    }
}
