/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.contraptions.gantry;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.ContraptionCollider;
import com.simibubi.create.content.contraptions.IDisplayAssemblyExceptions;
import com.simibubi.create.content.contraptions.gantry.GantryCarriageBlock;
import com.simibubi.create.content.contraptions.gantry.GantryContraption;
import com.simibubi.create.content.contraptions.gantry.GantryContraptionEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.gantry.GantryShaftBlock;
import com.simibubi.create.content.kinetics.gantry.GantryShaftBlockEntity;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencerInstructions;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class GantryCarriageBlockEntity
extends KineticBlockEntity
implements IDisplayAssemblyExceptions {
    boolean assembleNextTick;
    protected AssemblyException lastException;

    public GantryCarriageBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.registerAwardables(behaviours, AllAdvancements.CONTRAPTION_ACTORS);
    }

    @Override
    public void onSpeedChanged(float previousSpeed) {
        super.onSpeedChanged(previousSpeed);
    }

    public void checkValidGantryShaft() {
        if (this.shouldAssemble()) {
            this.queueAssembly();
        }
    }

    @Override
    public void initialize() {
        super.initialize();
        if (!this.getBlockState().canSurvive((LevelReader)this.level, this.worldPosition)) {
            this.level.destroyBlock(this.worldPosition, true);
        }
    }

    public void queueAssembly() {
        this.assembleNextTick = true;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            return;
        }
        if (this.assembleNextTick) {
            this.tryAssemble();
            this.assembleNextTick = false;
        }
    }

    @Override
    public AssemblyException getLastAssemblyException() {
        return this.lastException;
    }

    private void tryAssemble() {
        Direction shaftOrientation;
        BlockState blockState = this.getBlockState();
        if (!(blockState.getBlock() instanceof GantryCarriageBlock)) {
            return;
        }
        Direction direction = (Direction)blockState.getValue((Property)GantryCarriageBlock.FACING);
        GantryContraption contraption = new GantryContraption(direction);
        BlockEntity blockEntity = this.level.getBlockEntity(this.worldPosition.relative(direction.getOpposite()));
        if (!(blockEntity instanceof GantryShaftBlockEntity)) {
            return;
        }
        GantryShaftBlockEntity shaftBE = (GantryShaftBlockEntity)blockEntity;
        BlockState shaftState = shaftBE.getBlockState();
        if (!AllBlocks.GANTRY_SHAFT.has(shaftState)) {
            return;
        }
        float pinionMovementSpeed = shaftBE.getPinionMovementSpeed();
        Direction movementDirection = shaftOrientation = (Direction)shaftState.getValue((Property)GantryShaftBlock.FACING);
        if (pinionMovementSpeed < 0.0f) {
            movementDirection = movementDirection.getOpposite();
        }
        try {
            this.lastException = null;
            if (!contraption.assemble(this.level, this.worldPosition)) {
                return;
            }
            this.sendData();
        }
        catch (AssemblyException e) {
            this.lastException = e;
            this.sendData();
            return;
        }
        if (ContraptionCollider.isCollidingWithWorld(this.level, contraption, this.worldPosition.relative(movementDirection), movementDirection)) {
            return;
        }
        if (contraption.containsBlockBreakers()) {
            this.award(AllAdvancements.CONTRAPTION_ACTORS);
        }
        contraption.removeBlocksFromWorld(this.level, BlockPos.ZERO);
        GantryContraptionEntity movedContraption = GantryContraptionEntity.create(this.level, contraption, shaftOrientation);
        BlockPos anchor = this.worldPosition;
        movedContraption.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
        AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(this.level, (Vec3i)this.worldPosition);
        this.level.addFreshEntity((Entity)movedContraption);
        if (shaftBE.sequenceContext != null && shaftBE.sequenceContext.instruction() == SequencerInstructions.TURN_DISTANCE) {
            movedContraption.limitMovement(shaftBE.sequenceContext.getEffectiveValue(shaftBE.getTheoreticalSpeed()));
        }
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        AssemblyException.write(compound, registries, this.lastException);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        this.lastException = AssemblyException.read(compound, registries);
        super.read(compound, registries, clientPacket);
    }

    @Override
    public float propagateRotationTo(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff, boolean connectedViaAxes, boolean connectedViaCogs) {
        float defaultModifier = super.propagateRotationTo(target, stateFrom, stateTo, diff, connectedViaAxes, connectedViaCogs);
        if (connectedViaAxes) {
            return defaultModifier;
        }
        if (!AllBlocks.GANTRY_SHAFT.has(stateTo)) {
            return defaultModifier;
        }
        if (!((Boolean)stateTo.getValue((Property)GantryShaftBlock.POWERED)).booleanValue()) {
            return defaultModifier;
        }
        Direction direction = Direction.getNearest((float)diff.getX(), (float)diff.getY(), (float)diff.getZ());
        if (stateFrom.getValue((Property)GantryCarriageBlock.FACING) != direction.getOpposite()) {
            return defaultModifier;
        }
        return GantryCarriageBlockEntity.getGantryPinionModifier((Direction)stateTo.getValue((Property)GantryShaftBlock.FACING), (Direction)stateFrom.getValue((Property)GantryCarriageBlock.FACING));
    }

    public static float getGantryPinionModifier(Direction shaft, Direction pinionDirection) {
        Direction.Axis shaftAxis = shaft.getAxis();
        float directionModifier = shaft.getAxisDirection().getStep();
        if (shaftAxis == Direction.Axis.Y && (pinionDirection == Direction.NORTH || pinionDirection == Direction.EAST)) {
            return -directionModifier;
        }
        if (shaftAxis == Direction.Axis.X && (pinionDirection == Direction.DOWN || pinionDirection == Direction.SOUTH)) {
            return -directionModifier;
        }
        if (shaftAxis == Direction.Axis.Z && (pinionDirection == Direction.UP || pinionDirection == Direction.WEST)) {
            return -directionModifier;
        }
        return directionModifier;
    }

    private boolean shouldAssemble() {
        BlockState blockState = this.getBlockState();
        if (!(blockState.getBlock() instanceof GantryCarriageBlock)) {
            return false;
        }
        Direction facing = ((Direction)blockState.getValue((Property)GantryCarriageBlock.FACING)).getOpposite();
        BlockState shaftState = this.level.getBlockState(this.worldPosition.relative(facing));
        if (!(shaftState.getBlock() instanceof GantryShaftBlock)) {
            return false;
        }
        if (((Boolean)shaftState.getValue((Property)GantryShaftBlock.POWERED)).booleanValue()) {
            return false;
        }
        BlockEntity be = this.level.getBlockEntity(this.worldPosition.relative(facing));
        return be instanceof GantryShaftBlockEntity && ((GantryShaftBlockEntity)be).canAssembleOn();
    }
}
