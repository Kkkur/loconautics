/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.simibubi.create.content.redstone.link;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelSupportBehaviour;
import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlock;
import com.simibubi.create.content.redstone.link.RedstoneLinkFrequencySlot;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.lang3.tuple.Pair;

public class RedstoneLinkBlockEntity
extends SmartBlockEntity {
    private boolean receivedSignalChanged;
    private int receivedSignal;
    private int transmittedSignal;
    private LinkBehaviour link;
    private boolean transmitter;
    public FactoryPanelSupportBehaviour panelSupport;

    public RedstoneLinkBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.panelSupport = new FactoryPanelSupportBehaviour(this, () -> this.link != null && this.link.isListening(), () -> this.receivedSignal > 0, () -> ((RedstoneLinkBlock)AllBlocks.REDSTONE_LINK.get()).updateTransmittedSignal(this.getBlockState(), this.level, this.worldPosition));
        behaviours.add(this.panelSupport);
    }

    @Override
    public void addBehavioursDeferred(List<BlockEntityBehaviour> behaviours) {
        this.createLink();
        behaviours.add(this.link);
    }

    protected void createLink() {
        Pair<ValueBoxTransform, ValueBoxTransform> slots = ValueBoxTransform.Dual.makeSlots(RedstoneLinkFrequencySlot::new);
        this.link = this.transmitter ? LinkBehaviour.transmitter(this, slots, this::getSignal) : LinkBehaviour.receiver(this, slots, this::setSignal);
    }

    public int getSignal() {
        return this.transmittedSignal;
    }

    public void setSignal(int power) {
        if (this.receivedSignal != power) {
            this.receivedSignalChanged = true;
        }
        this.receivedSignal = power;
    }

    public void transmit(int strength) {
        this.transmittedSignal = strength;
        if (this.link != null) {
            this.link.notifySignalChange();
        }
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putBoolean("Transmitter", this.transmitter);
        compound.putInt("Receive", this.getReceivedSignal());
        compound.putBoolean("ReceivedChanged", this.receivedSignalChanged);
        compound.putInt("Transmit", this.transmittedSignal);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        this.transmitter = compound.getBoolean("Transmitter");
        super.read(compound, registries, clientPacket);
        this.receivedSignal = compound.getInt("Receive");
        this.receivedSignalChanged = compound.getBoolean("ReceivedChanged");
        if (this.level == null || this.level.isClientSide || !this.link.newPosition) {
            this.transmittedSignal = compound.getInt("Transmit");
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isTransmitterBlock() != this.transmitter) {
            this.transmitter = this.isTransmitterBlock();
            LinkBehaviour prevlink = this.link;
            this.removeBehaviour(LinkBehaviour.TYPE);
            this.createLink();
            this.link.copyItemsFrom(prevlink);
            this.attachBehaviourLate(this.link);
        }
        if (this.transmitter) {
            return;
        }
        if (this.level.isClientSide) {
            return;
        }
        BlockState blockState = this.getBlockState();
        if (!AllBlocks.REDSTONE_LINK.has(blockState)) {
            return;
        }
        if (this.getReceivedSignal() > 0 != (Boolean)blockState.getValue((Property)RedstoneLinkBlock.POWERED)) {
            this.receivedSignalChanged = true;
            this.level.setBlockAndUpdate(this.worldPosition, (BlockState)blockState.cycle((Property)RedstoneLinkBlock.POWERED));
        }
        if (this.receivedSignalChanged) {
            this.updateSelfAndAttached(blockState);
        }
    }

    @Override
    public void remove() {
        super.remove();
        this.updateSelfAndAttached(this.getBlockState());
    }

    public void updateSelfAndAttached(BlockState blockState) {
        Direction attachedFace = ((Direction)blockState.getValue((Property)RedstoneLinkBlock.FACING)).getOpposite();
        BlockPos attachedPos = this.worldPosition.relative(attachedFace);
        this.level.blockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition).getBlock());
        this.level.blockUpdated(attachedPos, this.level.getBlockState(attachedPos).getBlock());
        this.receivedSignalChanged = false;
        this.panelSupport.notifyPanels();
    }

    protected Boolean isTransmitterBlock() {
        return (Boolean)this.getBlockState().getValue((Property)RedstoneLinkBlock.RECEIVER) == false;
    }

    public int getReceivedSignal() {
        return this.receivedSignal;
    }
}
