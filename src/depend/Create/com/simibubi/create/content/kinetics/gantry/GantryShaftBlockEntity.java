/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.kinetics.gantry;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.gantry.GantryCarriageBlock;
import com.simibubi.create.content.contraptions.gantry.GantryCarriageBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.gantry.GantryShaftBlock;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class GantryShaftBlockEntity
extends KineticBlockEntity {
    public GantryShaftBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    protected boolean syncSequenceContext() {
        return true;
    }

    public void checkAttachedCarriageBlocks() {
        if (!this.canAssembleOn()) {
            return;
        }
        for (Direction d : Iterate.directions) {
            BlockEntity blockEntity;
            BlockPos offset;
            BlockState pinionState;
            if (d.getAxis() == ((Direction)this.getBlockState().getValue((Property)GantryShaftBlock.FACING)).getAxis() || !AllBlocks.GANTRY_CARRIAGE.has(pinionState = this.level.getBlockState(offset = this.worldPosition.relative(d))) || pinionState.getValue((Property)GantryCarriageBlock.FACING) != d || !((blockEntity = this.level.getBlockEntity(offset)) instanceof GantryCarriageBlockEntity)) continue;
            ((GantryCarriageBlockEntity)blockEntity).queueAssembly();
        }
    }

    @Override
    public void onSpeedChanged(float previousSpeed) {
        super.onSpeedChanged(previousSpeed);
        this.checkAttachedCarriageBlocks();
    }

    @Override
    public float propagateRotationTo(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff, boolean connectedViaAxes, boolean connectedViaCogs) {
        float defaultModifier = super.propagateRotationTo(target, stateFrom, stateTo, diff, connectedViaAxes, connectedViaCogs);
        if (connectedViaAxes) {
            return defaultModifier;
        }
        if (!((Boolean)stateFrom.getValue((Property)GantryShaftBlock.POWERED)).booleanValue()) {
            return defaultModifier;
        }
        if (!AllBlocks.GANTRY_CARRIAGE.has(stateTo)) {
            return defaultModifier;
        }
        Direction direction = Direction.getNearest((float)diff.getX(), (float)diff.getY(), (float)diff.getZ());
        if (stateTo.getValue((Property)GantryCarriageBlock.FACING) != direction) {
            return defaultModifier;
        }
        return GantryCarriageBlockEntity.getGantryPinionModifier((Direction)stateFrom.getValue((Property)GantryShaftBlock.FACING), (Direction)stateTo.getValue((Property)GantryCarriageBlock.FACING));
    }

    @Override
    public boolean isCustomConnection(KineticBlockEntity other, BlockState state, BlockState otherState) {
        if (!AllBlocks.GANTRY_CARRIAGE.has(otherState)) {
            return false;
        }
        BlockPos diff = other.getBlockPos().subtract((Vec3i)this.worldPosition);
        Direction direction = Direction.getNearest((float)diff.getX(), (float)diff.getY(), (float)diff.getZ());
        return otherState.getValue((Property)GantryCarriageBlock.FACING) == direction;
    }

    public boolean canAssembleOn() {
        BlockState blockState = this.getBlockState();
        if (!AllBlocks.GANTRY_SHAFT.has(blockState)) {
            return false;
        }
        if (((Boolean)blockState.getValue((Property)GantryShaftBlock.POWERED)).booleanValue()) {
            return false;
        }
        float speed = this.getPinionMovementSpeed();
        switch ((GantryShaftBlock.Part)((Object)blockState.getValue(GantryShaftBlock.PART))) {
            case END: {
                return speed < 0.0f;
            }
            case MIDDLE: {
                return speed != 0.0f;
            }
            case START: {
                return speed > 0.0f;
            }
        }
        return false;
    }

    public float getPinionMovementSpeed() {
        BlockState blockState = this.getBlockState();
        if (!AllBlocks.GANTRY_SHAFT.has(blockState)) {
            return 0.0f;
        }
        return Mth.clamp((float)GantryShaftBlockEntity.convertToLinear(-this.getSpeed()), (float)-0.49f, (float)0.49f);
    }

    @Override
    protected boolean isNoisy() {
        return false;
    }
}
