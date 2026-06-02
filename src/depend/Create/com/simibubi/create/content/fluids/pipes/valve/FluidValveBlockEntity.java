/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.content.fluids.pipes.valve;

import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.pipes.StraightPipeBlockEntity;
import com.simibubi.create.content.fluids.pipes.valve.FluidValveBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidValveBlockEntity
extends KineticBlockEntity {
    LerpedFloat pointer = LerpedFloat.linear().startWithValue(0.0).chase(0.0, 0.0, LerpedFloat.Chaser.LINEAR);

    public FluidValveBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void onSpeedChanged(float previousSpeed) {
        super.onSpeedChanged(previousSpeed);
        float speed = this.getSpeed();
        this.pointer.chase(speed > 0.0f ? 1.0 : 0.0, (double)this.getChaseSpeed(), LerpedFloat.Chaser.LINEAR);
        this.sendData();
    }

    @Override
    public void tick() {
        super.tick();
        this.pointer.tickChaser();
        if (this.level.isClientSide) {
            return;
        }
        BlockState blockState = this.getBlockState();
        if (!(blockState.getBlock() instanceof FluidValveBlock)) {
            return;
        }
        boolean stateOpen = (Boolean)blockState.getValue((Property)FluidValveBlock.ENABLED);
        if (stateOpen && this.pointer.getValue() == 0.0f) {
            FluidValveBlockEntity.switchToBlockState(this.level, this.worldPosition, (BlockState)blockState.setValue((Property)FluidValveBlock.ENABLED, (Comparable)Boolean.valueOf(false)));
            return;
        }
        if (!stateOpen && this.pointer.getValue() == 1.0f) {
            FluidValveBlockEntity.switchToBlockState(this.level, this.worldPosition, (BlockState)blockState.setValue((Property)FluidValveBlock.ENABLED, (Comparable)Boolean.valueOf(true)));
            return;
        }
    }

    private float getChaseSpeed() {
        return Mth.clamp((float)(Math.abs(this.getSpeed()) / 16.0f / 20.0f), (float)0.0f, (float)1.0f);
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.put("Pointer", (Tag)this.pointer.writeNBT());
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.pointer.readNBT(compound.getCompound("Pointer"), clientPacket);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new ValvePipeBehaviour(this, this));
        this.registerAwardables(behaviours, FluidPropagator.getSharedTriggers());
    }

    class ValvePipeBehaviour
    extends StraightPipeBlockEntity.StraightPipeFluidTransportBehaviour {
        public ValvePipeBehaviour(FluidValveBlockEntity this$0, SmartBlockEntity be) {
            super(be);
        }

        @Override
        public boolean canHaveFlowToward(BlockState state, Direction direction) {
            return FluidValveBlock.getPipeAxis(state) == direction.getAxis();
        }

        @Override
        public boolean canPullFluidFrom(FluidStack fluid, BlockState state, Direction direction) {
            if (state.hasProperty((Property)FluidValveBlock.ENABLED) && ((Boolean)state.getValue((Property)FluidValveBlock.ENABLED)).booleanValue()) {
                return super.canPullFluidFrom(fluid, state, direction);
            }
            return false;
        }
    }
}
