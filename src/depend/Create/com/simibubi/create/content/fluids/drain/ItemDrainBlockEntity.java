/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 */
package com.simibubi.create.content.fluids.drain;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.drain.ItemDrainItemHandler;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.utility.BlockHelper;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class ItemDrainBlockEntity
extends SmartBlockEntity
implements IHaveGoggleInformation {
    public static final int FILLING_TIME = 20;
    SmartFluidTankBehaviour internalTank;
    TransportedItemStack heldItem;
    protected int processingTicks;
    Map<Direction, ItemDrainItemHandler> itemHandlers = new IdentityHashMap<Direction, ItemDrainItemHandler>();

    public ItemDrainBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        for (Direction d : Iterate.horizontalDirections) {
            this.itemHandlers.put(d, new ItemDrainItemHandler(this, d));
        }
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.ITEM_DRAIN.get(), (be, context) -> {
            if (context != null && context.getAxis().isHorizontal()) {
                return be.itemHandlers.get(context);
            }
            return null;
        });
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.ITEM_DRAIN.get(), (be, context) -> {
            if (context != Direction.UP) {
                return be.internalTank.getCapability();
            }
            return null;
        });
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this).allowingBeltFunnels().setInsertionHandler(this::tryInsertingFromSide));
        this.internalTank = SmartFluidTankBehaviour.single(this, 1500).allowExtraction().forbidInsertion();
        behaviours.add(this.internalTank);
        this.registerAwardables(behaviours, AllAdvancements.DRAIN, AllAdvancements.CHAINED_DRAIN);
    }

    private ItemStack tryInsertingFromSide(TransportedItemStack transportedStack, Direction side, boolean simulate) {
        ItemStack inserted = transportedStack.stack;
        ItemStack returned = ItemStack.EMPTY;
        if (!this.getHeldItemStack().isEmpty()) {
            return inserted;
        }
        if (inserted.getCount() > 1 && GenericItemEmptying.canItemBeEmptied(this.level, inserted)) {
            returned = inserted.copyWithCount(inserted.getCount() - 1);
            inserted = inserted.copyWithCount(1);
        }
        if (simulate) {
            return returned;
        }
        transportedStack = transportedStack.copy();
        transportedStack.stack = inserted.copy();
        transportedStack.beltPosition = side.getAxis().isVertical() ? 0.5f : 0.0f;
        transportedStack.prevSideOffset = transportedStack.sideOffset;
        transportedStack.prevBeltPosition = transportedStack.beltPosition;
        this.setHeldItem(transportedStack, side);
        this.setChanged();
        this.sendData();
        return returned;
    }

    public ItemStack getHeldItemStack() {
        return this.heldItem == null ? ItemStack.EMPTY : this.heldItem.stack;
    }

    @Override
    public void tick() {
        boolean onClient;
        super.tick();
        if (this.heldItem == null) {
            this.processingTicks = 0;
            return;
        }
        boolean bl = onClient = this.level.isClientSide && !this.isVirtual();
        if (this.processingTicks > 0) {
            boolean wasAtBeginning;
            this.heldItem.prevBeltPosition = 0.5f;
            boolean bl2 = wasAtBeginning = this.processingTicks == 20;
            if (!onClient || this.processingTicks < 20) {
                --this.processingTicks;
            }
            if (!this.continueProcessing()) {
                this.processingTicks = 0;
                this.notifyUpdate();
                return;
            }
            if (wasAtBeginning != (this.processingTicks == 20)) {
                this.sendData();
            }
            return;
        }
        this.heldItem.prevBeltPosition = this.heldItem.beltPosition;
        this.heldItem.prevSideOffset = this.heldItem.sideOffset;
        this.heldItem.beltPosition += this.itemMovementPerTick();
        if (this.heldItem.beltPosition > 1.0f) {
            BlockPos nextPosition;
            DirectBeltInputBehaviour directBeltInputBehaviour;
            this.heldItem.beltPosition = 1.0f;
            if (onClient) {
                return;
            }
            Direction side = this.heldItem.insertedFrom;
            ItemStack tryExportingToBeltFunnel = this.getBehaviour(DirectBeltInputBehaviour.TYPE).tryExportingToBeltFunnel(this.heldItem.stack, side.getOpposite(), false);
            if (tryExportingToBeltFunnel != null) {
                if (tryExportingToBeltFunnel.getCount() != this.heldItem.stack.getCount()) {
                    if (tryExportingToBeltFunnel.isEmpty()) {
                        this.heldItem = null;
                    } else {
                        this.heldItem.stack = tryExportingToBeltFunnel;
                    }
                    this.notifyUpdate();
                    return;
                }
                if (!tryExportingToBeltFunnel.isEmpty()) {
                    return;
                }
            }
            if ((directBeltInputBehaviour = BlockEntityBehaviour.get((BlockGetter)this.level, nextPosition = this.worldPosition.relative(side), DirectBeltInputBehaviour.TYPE)) == null) {
                if (!BlockHelper.hasBlockSolidSide(this.level.getBlockState(nextPosition), (BlockGetter)this.level, nextPosition, side.getOpposite())) {
                    ItemStack ejected = this.heldItem.stack;
                    Vec3 outPos = VecHelper.getCenterOf((Vec3i)this.worldPosition).add(Vec3.atLowerCornerOf((Vec3i)side.getNormal()).scale(0.75));
                    float movementSpeed = this.itemMovementPerTick();
                    Vec3 outMotion = Vec3.atLowerCornerOf((Vec3i)side.getNormal()).scale((double)movementSpeed).add(0.0, 0.125, 0.0);
                    outPos.add(outMotion.normalize());
                    ItemEntity entity = new ItemEntity(this.level, outPos.x, outPos.y + 0.375, outPos.z, ejected);
                    entity.setDeltaMovement(outMotion);
                    entity.setDefaultPickUpDelay();
                    entity.hurtMarked = true;
                    this.level.addFreshEntity((Entity)entity);
                    this.heldItem = null;
                    this.notifyUpdate();
                }
                return;
            }
            if (!directBeltInputBehaviour.canInsertFromSide(side)) {
                return;
            }
            ItemStack returned = directBeltInputBehaviour.handleInsertion(this.heldItem.copy(), side, false);
            if (returned.isEmpty()) {
                if (this.level.getBlockEntity(nextPosition) instanceof ItemDrainBlockEntity) {
                    this.award(AllAdvancements.CHAINED_DRAIN);
                }
                this.heldItem = null;
                this.notifyUpdate();
                return;
            }
            if (returned.getCount() != this.heldItem.stack.getCount()) {
                this.heldItem.stack = returned;
                this.notifyUpdate();
                return;
            }
            return;
        }
        if (this.heldItem.prevBeltPosition < 0.5f && this.heldItem.beltPosition >= 0.5f) {
            if (!GenericItemEmptying.canItemBeEmptied(this.level, this.heldItem.stack)) {
                return;
            }
            this.heldItem.beltPosition = 0.5f;
            if (onClient) {
                return;
            }
            this.processingTicks = 20;
            this.sendData();
        }
    }

    protected boolean continueProcessing() {
        if (this.level.isClientSide && !this.isVirtual()) {
            return true;
        }
        if (this.processingTicks < 5) {
            return true;
        }
        if (!GenericItemEmptying.canItemBeEmptied(this.level, this.heldItem.stack)) {
            return false;
        }
        Pair<FluidStack, ItemStack> emptyItem = GenericItemEmptying.emptyItem(this.level, this.heldItem.stack, true);
        FluidStack fluidFromItem = (FluidStack)emptyItem.getFirst();
        if (this.processingTicks > 5) {
            this.internalTank.allowInsertion();
            if (this.internalTank.getPrimaryHandler().fill(fluidFromItem, IFluidHandler.FluidAction.SIMULATE) != fluidFromItem.getAmount()) {
                this.internalTank.forbidInsertion();
                this.processingTicks = 20;
                return true;
            }
            this.internalTank.forbidInsertion();
            return true;
        }
        emptyItem = GenericItemEmptying.emptyItem(this.level, this.heldItem.stack.copy(), false);
        this.award(AllAdvancements.DRAIN);
        ItemStack out = (ItemStack)emptyItem.getSecond();
        if (!out.isEmpty()) {
            this.heldItem.stack = out;
        } else {
            this.heldItem = null;
        }
        this.internalTank.allowInsertion();
        this.internalTank.getPrimaryHandler().fill(fluidFromItem, IFluidHandler.FluidAction.EXECUTE);
        this.internalTank.forbidInsertion();
        this.notifyUpdate();
        return true;
    }

    private float itemMovementPerTick() {
        return 0.125f;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.invalidateCapabilities();
    }

    public void setHeldItem(TransportedItemStack heldItem, Direction insertedFrom) {
        this.heldItem = heldItem;
        this.heldItem.insertedFrom = insertedFrom;
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putInt("ProcessingTicks", this.processingTicks);
        if (this.heldItem != null) {
            compound.put("HeldItem", (Tag)this.heldItem.serializeNBT(registries));
        }
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        this.heldItem = null;
        this.processingTicks = compound.getInt("ProcessingTicks");
        if (compound.contains("HeldItem")) {
            this.heldItem = TransportedItemStack.read(compound.getCompound("HeldItem"), registries);
        }
        super.read(compound, registries, clientPacket);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return this.containedFluidTooltip(tooltip, isPlayerSneaking, (IFluidHandler)this.level.getCapability(Capabilities.FluidHandler.BLOCK, this.worldPosition, null));
    }
}
