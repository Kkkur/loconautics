/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.BlockFace
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.redstone.smartObserver;

import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.PipeConnection;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import com.simibubi.create.content.redstone.DirectedDirectionalBlock;
import com.simibubi.create.content.redstone.FilteredDetectorFilterSlot;
import com.simibubi.create.content.redstone.smartObserver.SmartObserverBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.CapManipulationBehaviourBase;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.TankManipulationBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryTrackerBehaviour;
import java.util.List;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class SmartObserverBlockEntity
extends SmartBlockEntity
implements Clearable {
    private static final int DEFAULT_DELAY = 6;
    private FilteringBehaviour filtering;
    private InvManipulationBehaviour observedInventory;
    private TankManipulationBehaviour observedTank;
    private VersionedInventoryTrackerBehaviour invVersionTracker;
    private boolean sustainSignal;
    public int turnOffTicks = 0;

    public SmartObserverBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.setLazyTickRate(20);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.filtering = new FilteringBehaviour(this, new FilteredDetectorFilterSlot(false)).withCallback($ -> this.invVersionTracker.reset());
        behaviours.add(this.filtering);
        this.invVersionTracker = new VersionedInventoryTrackerBehaviour(this);
        behaviours.add(this.invVersionTracker);
        CapManipulationBehaviourBase.InterfaceProvider towardBlockFacing = (w, p, s) -> new BlockFace(p, DirectedDirectionalBlock.getTargetDirection(s));
        this.observedInventory = (InvManipulationBehaviour)new InvManipulationBehaviour(this, towardBlockFacing).bypassSidedness();
        behaviours.add(this.observedInventory);
        this.observedTank = (TankManipulationBehaviour)new TankManipulationBehaviour(this, towardBlockFacing).bypassSidedness();
        behaviours.add(this.observedTank);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide()) {
            return;
        }
        BlockState state = this.getBlockState();
        if (this.turnOffTicks > 0) {
            --this.turnOffTicks;
            if (this.turnOffTicks == 0) {
                this.level.scheduleTick(this.worldPosition, state.getBlock(), 1);
            }
        }
        if (!this.isActive()) {
            return;
        }
        BlockPos targetPos = this.worldPosition.relative(SmartObserverBlock.getTargetDirection(state));
        Block block = this.level.getBlockState(targetPos).getBlock();
        if (!this.filtering.getFilter().isEmpty() && block.asItem() != null && this.filtering.test(new ItemStack((ItemLike)block))) {
            this.activate(3);
            return;
        }
        TransportedItemStackHandlerBehaviour behaviour = BlockEntityBehaviour.get((BlockGetter)this.level, targetPos, TransportedItemStackHandlerBehaviour.TYPE);
        if (behaviour != null) {
            behaviour.handleCenteredProcessingOnAllItems(0.45f, stack -> {
                if (!this.filtering.test(stack.stack) || this.turnOffTicks == 6) {
                    return TransportedItemStackHandlerBehaviour.TransportedResult.doNothing();
                }
                this.activate();
                return TransportedItemStackHandlerBehaviour.TransportedResult.doNothing();
            });
            return;
        }
        FluidTransportBehaviour fluidBehaviour = BlockEntityBehaviour.get((BlockGetter)this.level, targetPos, FluidTransportBehaviour.TYPE);
        if (fluidBehaviour != null) {
            for (Direction side : Iterate.directions) {
                PipeConnection.Flow flow = fluidBehaviour.getFlow(side);
                if (flow == null || !flow.inbound || !flow.complete || !this.filtering.test(flow.fluid)) continue;
                this.activate();
                return;
            }
            return;
        }
        BlockEntity blockEntity = this.level.getBlockEntity(targetPos);
        if (blockEntity instanceof ChainConveyorBlockEntity) {
            ChainConveyorBlockEntity ccbe = (ChainConveyorBlockEntity)blockEntity;
            for (ChainConveyorPackage box : ccbe.getLoopingPackages()) {
                if (!this.filtering.test(box.item)) continue;
                this.activate();
                return;
            }
            return;
        }
        if (this.observedInventory.hasInventory()) {
            boolean skipInv = this.invVersionTracker.stillWaiting(this.observedInventory);
            this.invVersionTracker.awaitNewVersion(this.observedInventory);
            if (skipInv && this.sustainSignal) {
                this.turnOffTicks = 6;
            }
            if (!skipInv) {
                this.sustainSignal = false;
                if (!((InvManipulationBehaviour)this.observedInventory.simulate()).extract().isEmpty()) {
                    this.sustainSignal = true;
                    this.activate();
                    return;
                }
            }
        }
        if (!((TankManipulationBehaviour)this.observedTank.simulate()).extractAny().isEmpty()) {
            this.activate();
            return;
        }
    }

    public void activate() {
        this.activate(6);
    }

    public void activate(int ticks) {
        BlockState state = this.getBlockState();
        this.turnOffTicks = ticks;
        if (((Boolean)state.getValue((Property)SmartObserverBlock.POWERED)).booleanValue()) {
            return;
        }
        this.level.setBlockAndUpdate(this.worldPosition, (BlockState)state.setValue((Property)SmartObserverBlock.POWERED, (Comparable)Boolean.valueOf(true)));
        this.level.updateNeighborsAt(this.worldPosition, state.getBlock());
    }

    private boolean isActive() {
        return true;
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putInt("TurnOff", this.turnOffTicks);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.turnOffTicks = compound.getInt("TurnOff");
    }

    public void clearContent() {
        this.filtering.setFilter(ItemStack.EMPTY);
    }
}
