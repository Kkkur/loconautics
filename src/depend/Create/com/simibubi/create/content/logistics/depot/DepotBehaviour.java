/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.Containers
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 *  net.neoforged.neoforge.items.ItemStackHandler
 */
package com.simibubi.create.content.logistics.depot;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.depot.DepotItemHandler;
import com.simibubi.create.content.logistics.funnel.AbstractFunnelBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.mixin.accessor.ItemStackHandlerAccessor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Clearable;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;

public class DepotBehaviour
extends BlockEntityBehaviour
implements Clearable {
    public static final BehaviourType<DepotBehaviour> TYPE = new BehaviourType();
    TransportedItemStack heldItem;
    List<TransportedItemStack> incoming;
    ItemStackHandler processingOutputBuffer;
    public DepotItemHandler itemHandler;
    TransportedItemStackHandlerBehaviour transportedHandler;
    Supplier<Integer> maxStackSize = () -> this.heldItem != null ? this.heldItem.stack.getMaxStackSize() : 64;
    Supplier<Boolean> canAcceptItems = () -> true;
    Predicate<Direction> canFunnelsPullFrom = $ -> true;
    Consumer<ItemStack> onHeldInserted;
    Predicate<ItemStack> acceptedItems = $ -> true;
    boolean allowMerge;

    public DepotBehaviour(final SmartBlockEntity be) {
        super(be);
        this.onHeldInserted = $ -> {};
        this.incoming = new ArrayList<TransportedItemStack>();
        this.itemHandler = new DepotItemHandler(this);
        this.processingOutputBuffer = new ItemStackHandler(this, 8){

            protected void onContentsChanged(int slot) {
                be.notifyUpdate();
            }
        };
    }

    public void enableMerging() {
        this.allowMerge = true;
    }

    public DepotBehaviour withCallback(Consumer<ItemStack> changeListener) {
        this.onHeldInserted = changeListener;
        return this;
    }

    public DepotBehaviour onlyAccepts(Predicate<ItemStack> filter) {
        this.acceptedItems = filter;
        return this;
    }

    @Override
    public void tick() {
        BeltProcessingBehaviour.ProcessingResult result;
        super.tick();
        Level world = this.blockEntity.getLevel();
        Iterator<TransportedItemStack> iterator = this.incoming.iterator();
        while (iterator.hasNext()) {
            TransportedItemStack ts = iterator.next();
            if (!this.tick(ts) || world.isClientSide && !this.blockEntity.isVirtual()) continue;
            if (this.heldItem == null) {
                this.heldItem = ts;
            } else if (!ItemHelper.canItemStackAmountsStack(this.heldItem.stack, ts.stack)) {
                Vec3 vec = VecHelper.getCenterOf((Vec3i)this.blockEntity.getBlockPos());
                Containers.dropItemStack((Level)this.blockEntity.getLevel(), (double)vec.x, (double)(vec.y + 0.5), (double)vec.z, (ItemStack)ts.stack);
            } else {
                this.heldItem.stack.grow(ts.stack.getCount());
            }
            iterator.remove();
            this.blockEntity.notifyUpdate();
        }
        if (this.heldItem == null) {
            return;
        }
        if (!this.tick(this.heldItem)) {
            return;
        }
        BlockPos pos = this.blockEntity.getBlockPos();
        if (world.isClientSide) {
            return;
        }
        if (this.handleBeltFunnelOutput()) {
            return;
        }
        BeltProcessingBehaviour processingBehaviour = BlockEntityBehaviour.get((BlockGetter)world, pos.above(2), BeltProcessingBehaviour.TYPE);
        if (processingBehaviour == null) {
            return;
        }
        if (!this.heldItem.locked && BeltProcessingBehaviour.isBlocked((BlockGetter)world, pos)) {
            return;
        }
        ItemStack previousItem = this.heldItem.stack;
        boolean wasLocked = this.heldItem.locked;
        BeltProcessingBehaviour.ProcessingResult processingResult = result = wasLocked ? processingBehaviour.handleHeldItem(this.heldItem, this.transportedHandler) : processingBehaviour.handleReceivedItem(this.heldItem, this.transportedHandler);
        if (this.heldItem == null || result == BeltProcessingBehaviour.ProcessingResult.REMOVE) {
            this.heldItem = null;
            this.blockEntity.sendData();
            return;
        }
        boolean bl = this.heldItem.locked = result == BeltProcessingBehaviour.ProcessingResult.HOLD;
        if (this.heldItem.locked != wasLocked || !ItemStack.matches((ItemStack)previousItem, (ItemStack)this.heldItem.stack)) {
            this.blockEntity.sendData();
        }
    }

    protected boolean tick(TransportedItemStack heldItem) {
        heldItem.prevBeltPosition = heldItem.beltPosition;
        heldItem.prevSideOffset = heldItem.sideOffset;
        float diff = 0.5f - heldItem.beltPosition;
        if (diff > 0.001953125f) {
            if (diff > 0.03125f && !BeltHelper.isItemUpright(heldItem.stack)) {
                ++heldItem.angle;
            }
            heldItem.beltPosition += diff / 4.0f;
        }
        return diff < 0.0625f;
    }

    private boolean handleBeltFunnelOutput() {
        BlockState funnel = this.getWorld().getBlockState(this.getPos().above());
        Direction funnelFacing = AbstractFunnelBlock.getFunnelFacing(funnel);
        if (funnelFacing == null || !this.canFunnelsPullFrom.test(funnelFacing.getOpposite())) {
            return false;
        }
        for (int slot = 0; slot < this.processingOutputBuffer.getSlots(); ++slot) {
            ItemStack previousItem = this.processingOutputBuffer.getStackInSlot(slot);
            if (previousItem.isEmpty()) continue;
            ItemStack afterInsert = this.blockEntity.getBehaviour(DirectBeltInputBehaviour.TYPE).tryExportingToBeltFunnel(previousItem, null, false);
            if (afterInsert == null) {
                return false;
            }
            if (previousItem.getCount() == afterInsert.getCount()) continue;
            this.processingOutputBuffer.setStackInSlot(slot, afterInsert);
            this.blockEntity.notifyUpdate();
            return true;
        }
        ItemStack previousItem = this.heldItem.stack;
        ItemStack afterInsert = this.blockEntity.getBehaviour(DirectBeltInputBehaviour.TYPE).tryExportingToBeltFunnel(previousItem, null, false);
        if (afterInsert == null) {
            return false;
        }
        if (previousItem.getCount() != afterInsert.getCount()) {
            if (afterInsert.isEmpty()) {
                this.heldItem = null;
            } else {
                this.heldItem.stack = afterInsert;
            }
            this.blockEntity.notifyUpdate();
            return true;
        }
        return false;
    }

    public void clearContent() {
        ((ItemStackHandlerAccessor)this.processingOutputBuffer).create$getStacks().clear();
        this.incoming.clear();
        this.heldItem = null;
    }

    @Override
    public void destroy() {
        super.destroy();
        Level level = this.getWorld();
        BlockPos pos = this.getPos();
        ItemHelper.dropContents(level, pos, (IItemHandler)this.processingOutputBuffer);
        for (TransportedItemStack transportedItemStack : this.incoming) {
            Block.popResource((Level)level, (BlockPos)pos, (ItemStack)transportedItemStack.stack);
        }
        if (!this.getHeldItemStack().isEmpty()) {
            Block.popResource((Level)level, (BlockPos)pos, (ItemStack)this.getHeldItemStack());
        }
    }

    @Override
    public void unload() {
        if (this.itemHandler != null) {
            this.blockEntity.invalidateCapabilities();
        }
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (this.heldItem != null) {
            compound.put("HeldItem", (Tag)this.heldItem.serializeNBT(registries));
        }
        compound.put("OutputBuffer", (Tag)this.processingOutputBuffer.serializeNBT(registries));
        if (this.canMergeItems() && !this.incoming.isEmpty()) {
            compound.put("Incoming", (Tag)NBTHelper.writeCompoundList(this.incoming, stack -> stack.serializeNBT(registries)));
        }
    }

    @Override
    public void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        this.heldItem = null;
        if (compound.contains("HeldItem")) {
            this.heldItem = TransportedItemStack.read(compound.getCompound("HeldItem"), registries);
        }
        this.processingOutputBuffer.deserializeNBT(registries, compound.getCompound("OutputBuffer"));
        if (this.canMergeItems()) {
            ListTag list = compound.getList("Incoming", 10);
            this.incoming = NBTHelper.readCompoundList((ListTag)list, c -> TransportedItemStack.read(c, registries));
        }
    }

    public void addSubBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this.blockEntity).allowingBeltFunnels().setInsertionHandler(this::tryInsertingFromSide).considerOccupiedWhen(this::isOccupied));
        this.transportedHandler = new TransportedItemStackHandlerBehaviour(this.blockEntity, this::applyToAllItems).withStackPlacement(this::getWorldPositionOf);
        behaviours.add(this.transportedHandler);
    }

    public ItemStack getHeldItemStack() {
        return this.heldItem == null ? ItemStack.EMPTY : this.heldItem.stack;
    }

    public boolean canMergeItems() {
        return this.allowMerge;
    }

    public int getPresentStackSize() {
        int cumulativeStackSize = 0;
        cumulativeStackSize += this.getHeldItemStack().getCount();
        for (int slot = 0; slot < this.processingOutputBuffer.getSlots(); ++slot) {
            cumulativeStackSize += this.processingOutputBuffer.getStackInSlot(slot).getCount();
        }
        return cumulativeStackSize;
    }

    public int getRemainingSpace() {
        int cumulativeStackSize = this.getPresentStackSize();
        for (TransportedItemStack transportedItemStack : this.incoming) {
            cumulativeStackSize += transportedItemStack.stack.getCount();
        }
        int fromGetter = Math.min(this.maxStackSize.get() == 0 ? 64 : this.maxStackSize.get(), this.getHeldItemStack().getMaxStackSize());
        return fromGetter - cumulativeStackSize;
    }

    public ItemStack insert(TransportedItemStack heldItem, boolean simulate) {
        boolean stackTooLarge;
        if (!this.canAcceptItems.get().booleanValue()) {
            return heldItem.stack;
        }
        if (!this.acceptedItems.test(heldItem.stack)) {
            return heldItem.stack;
        }
        if (this.canMergeItems()) {
            int remainingSpace = this.getRemainingSpace();
            ItemStack inserted = heldItem.stack;
            if (remainingSpace <= 0) {
                return inserted;
            }
            if (this.heldItem != null && !ItemHelper.canItemStackAmountsStack(this.heldItem.stack, inserted)) {
                return inserted;
            }
            ItemStack returned = ItemStack.EMPTY;
            if (remainingSpace < inserted.getCount()) {
                returned = heldItem.stack.copyWithCount(inserted.getCount() - remainingSpace);
                if (!simulate) {
                    TransportedItemStack copy = heldItem.copy();
                    copy.stack.setCount(remainingSpace);
                    if (this.heldItem != null) {
                        this.incoming.add(copy);
                    } else {
                        this.heldItem = copy;
                    }
                }
            } else if (!simulate) {
                if (this.heldItem != null) {
                    this.incoming.add(heldItem);
                } else {
                    this.heldItem = heldItem;
                }
            }
            return returned;
        }
        ItemStack returned = ItemStack.EMPTY;
        int maxCount = heldItem.stack.getMaxStackSize();
        boolean bl = stackTooLarge = maxCount < heldItem.stack.getCount();
        if (stackTooLarge) {
            returned = heldItem.stack.copyWithCount(heldItem.stack.getCount() - maxCount);
        }
        if (simulate) {
            return returned;
        }
        if (this.isEmpty()) {
            if (heldItem.insertedFrom.getAxis().isHorizontal()) {
                AllSoundEvents.DEPOT_SLIDE.playOnServer(this.getWorld(), (Vec3i)this.getPos());
            } else {
                AllSoundEvents.DEPOT_PLOP.playOnServer(this.getWorld(), (Vec3i)this.getPos());
            }
        }
        if (stackTooLarge) {
            heldItem = heldItem.copy();
            heldItem.stack.setCount(maxCount);
        }
        this.heldItem = heldItem;
        this.onHeldInserted.accept(heldItem.stack);
        return returned;
    }

    public void setHeldItem(TransportedItemStack heldItem) {
        this.heldItem = heldItem;
    }

    public void removeHeldItem() {
        this.heldItem = null;
    }

    public void setCenteredHeldItem(TransportedItemStack heldItem) {
        this.heldItem = heldItem;
        this.heldItem.beltPosition = 0.5f;
        this.heldItem.prevBeltPosition = 0.5f;
    }

    private boolean isOccupied(Direction side) {
        if (!this.getHeldItemStack().isEmpty() && !this.canMergeItems()) {
            return true;
        }
        if (!this.isOutputEmpty() && !this.canMergeItems()) {
            return true;
        }
        return this.canAcceptItems.get() == false;
    }

    private ItemStack tryInsertingFromSide(TransportedItemStack transportedStack, Direction side, boolean simulate) {
        ItemStack inserted = transportedStack.stack;
        if (this.isOccupied(side)) {
            return inserted;
        }
        int size = transportedStack.stack.getCount();
        transportedStack = transportedStack.copy();
        transportedStack.beltPosition = side.getAxis().isVertical() ? 0.5f : 0.0f;
        transportedStack.insertedFrom = side;
        transportedStack.prevSideOffset = transportedStack.sideOffset;
        transportedStack.prevBeltPosition = transportedStack.beltPosition;
        ItemStack remainder = this.insert(transportedStack, simulate);
        if (remainder.getCount() != size) {
            this.blockEntity.notifyUpdate();
        }
        return remainder;
    }

    private void applyToAllItems(float maxDistanceFromCentre, Function<TransportedItemStack, TransportedItemStackHandlerBehaviour.TransportedResult> processFunction) {
        if (this.heldItem == null) {
            return;
        }
        if (0.5f - this.heldItem.beltPosition > maxDistanceFromCentre) {
            return;
        }
        boolean dirty = false;
        TransportedItemStack transportedItemStack = this.heldItem;
        ItemStack stackBefore = transportedItemStack.stack.copy();
        TransportedItemStackHandlerBehaviour.TransportedResult result = processFunction.apply(transportedItemStack);
        if (result == null || result.didntChangeFrom(stackBefore)) {
            return;
        }
        dirty = true;
        this.heldItem = null;
        if (result.hasHeldOutput()) {
            this.setCenteredHeldItem(result.getHeldOutput());
        }
        for (TransportedItemStack added : result.getOutputs()) {
            if (this.getHeldItemStack().isEmpty()) {
                this.setCenteredHeldItem(added);
                continue;
            }
            ItemStack remainder = ItemHandlerHelper.insertItemStacked((IItemHandler)this.processingOutputBuffer, (ItemStack)added.stack, (boolean)false);
            Vec3 vec = VecHelper.getCenterOf((Vec3i)this.blockEntity.getBlockPos());
            Containers.dropItemStack((Level)this.blockEntity.getLevel(), (double)vec.x, (double)(vec.y + 0.5), (double)vec.z, (ItemStack)remainder);
        }
        if (dirty) {
            this.blockEntity.notifyUpdate();
        }
    }

    public boolean isEmpty() {
        return this.heldItem == null && this.isOutputEmpty();
    }

    public boolean isOutputEmpty() {
        for (int i = 0; i < this.processingOutputBuffer.getSlots(); ++i) {
            if (this.processingOutputBuffer.getStackInSlot(i).isEmpty()) continue;
            return false;
        }
        return true;
    }

    private Vec3 getWorldPositionOf(TransportedItemStack transported) {
        return VecHelper.getCenterOf((Vec3i)this.blockEntity.getBlockPos());
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    public boolean isItemValid(ItemStack stack) {
        return this.acceptedItems.test(stack);
    }
}
