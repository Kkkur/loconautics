/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.belt.transport;

import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.BeltCrusherInteractionHandler;
import com.simibubi.create.content.kinetics.belt.transport.BeltFunnelInteractionHandler;
import com.simibubi.create.content.kinetics.belt.transport.BeltTunnelInteractionHandler;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BeltInventory {
    final BeltBlockEntity belt;
    private final List<TransportedItemStack> items;
    final List<TransportedItemStack> toInsert;
    final List<TransportedItemStack> toRemove;
    boolean beltMovementPositive;
    final float SEGMENT_WINDOW = 0.75f;
    TransportedItemStack lazyClientItem;

    public BeltInventory(BeltBlockEntity be) {
        this.belt = be;
        this.items = new LinkedList<TransportedItemStack>();
        this.toInsert = new LinkedList<TransportedItemStack>();
        this.toRemove = new LinkedList<TransportedItemStack>();
    }

    public void tick() {
        if (this.lazyClientItem != null) {
            if (this.lazyClientItem.locked) {
                this.lazyClientItem = null;
            } else {
                this.lazyClientItem.locked = true;
            }
        }
        if (!this.toInsert.isEmpty() || !this.toRemove.isEmpty()) {
            this.toInsert.forEach(this::insert);
            this.toInsert.clear();
            this.items.removeAll(this.toRemove);
            this.toRemove.clear();
            this.belt.notifyUpdate();
        }
        if (this.belt.getSpeed() == 0.0f) {
            return;
        }
        if (this.beltMovementPositive != this.belt.getDirectionAwareBeltMovementSpeed() > 0.0f) {
            this.beltMovementPositive = !this.beltMovementPositive;
            Collections.reverse(this.items);
            this.belt.notifyUpdate();
        }
        TransportedItemStack stackInFront = null;
        TransportedItemStack currentItem = null;
        Iterator<TransportedItemStack> iterator = this.items.iterator();
        float beltSpeed = this.belt.getDirectionAwareBeltMovementSpeed();
        Direction movementFacing = this.belt.getMovementFacing();
        boolean horizontal = this.belt.getBlockState().getValue(BeltBlock.SLOPE) == BeltSlope.HORIZONTAL;
        float spacing = 1.0f;
        Level world = this.belt.getLevel();
        boolean onClient = world.isClientSide && !this.belt.isVirtual();
        Ending ending = Ending.UNRESOLVED;
        while (iterator.hasNext()) {
            float diffToEnd;
            stackInFront = currentItem;
            currentItem = iterator.next();
            currentItem.prevBeltPosition = currentItem.beltPosition;
            currentItem.prevSideOffset = currentItem.sideOffset;
            if (currentItem.stack.isEmpty()) {
                iterator.remove();
                currentItem = null;
                continue;
            }
            float movement = beltSpeed;
            if (onClient) {
                movement *= ServerSpeedProvider.get();
            }
            if (world.isClientSide && currentItem.locked) continue;
            if (currentItem.lockedExternally) {
                currentItem.lockedExternally = false;
                continue;
            }
            boolean noMovement = false;
            float currentPos = currentItem.beltPosition;
            if (stackInFront != null) {
                float diff = stackInFront.beltPosition - currentPos;
                if (Math.abs(diff) <= spacing) {
                    noMovement = true;
                }
                movement = this.beltMovementPositive ? Math.min(movement, diff - spacing) : Math.max(movement, diff + spacing);
            }
            float f = diffToEnd = this.beltMovementPositive ? (float)this.belt.beltLength - currentPos : -currentPos;
            if (Math.abs(diffToEnd) < Math.abs(movement) + 1.0f) {
                if (ending == Ending.UNRESOLVED) {
                    ending = this.resolveEnding();
                }
                diffToEnd += this.beltMovementPositive ? -ending.margin : ending.margin;
            }
            float limitedMovement = this.beltMovementPositive ? Math.min(movement, diffToEnd) : Math.max(movement, diffToEnd);
            float nextOffset = currentItem.beltPosition + limitedMovement;
            if (!onClient && horizontal) {
                ItemStack item = currentItem.stack;
                if (this.handleBeltProcessingAndCheckIfRemoved(currentItem, nextOffset, noMovement)) {
                    iterator.remove();
                    this.belt.notifyUpdate();
                    continue;
                }
                if (item != currentItem.stack) {
                    this.belt.notifyUpdate();
                }
                if (currentItem.locked) continue;
            }
            if (BeltFunnelInteractionHandler.checkForFunnels(this, currentItem, nextOffset) || noMovement || BeltTunnelInteractionHandler.flapTunnelsAndCheckIfStuck(this, currentItem, nextOffset) || BeltCrusherInteractionHandler.checkForCrushers(this, currentItem, nextOffset)) continue;
            currentItem.beltPosition += limitedMovement;
            float diffToMiddle = currentItem.getTargetSideOffset() - currentItem.sideOffset;
            currentItem.sideOffset += Mth.clamp((float)(diffToMiddle * Math.abs(limitedMovement) * 6.0f), (float)(-Math.abs(diffToMiddle)), (float)Math.abs(diffToMiddle));
            currentPos = currentItem.beltPosition;
            if (limitedMovement == movement || onClient) continue;
            int lastOffset = this.beltMovementPositive ? this.belt.beltLength - 1 : 0;
            BlockPos nextPosition = BeltHelper.getPositionForOffset(this.belt, this.beltMovementPositive ? this.belt.beltLength : -1);
            if (ending == Ending.FUNNEL) continue;
            if (ending == Ending.INSERT) {
                ItemStack remainder;
                DirectBeltInputBehaviour inputBehaviour = BlockEntityBehaviour.get((BlockGetter)world, nextPosition, DirectBeltInputBehaviour.TYPE);
                if (inputBehaviour == null || !inputBehaviour.canInsertFromSide(movementFacing) || ItemStack.matches((ItemStack)(remainder = inputBehaviour.handleInsertion(currentItem, movementFacing, false)), (ItemStack)currentItem.stack)) continue;
                currentItem.stack = remainder;
                if (remainder.isEmpty()) {
                    this.lazyClientItem = currentItem;
                    this.lazyClientItem.locked = false;
                    iterator.remove();
                } else {
                    currentItem.stack = remainder;
                }
                BeltTunnelInteractionHandler.flapTunnel(this, lastOffset, movementFacing, false);
                this.belt.notifyUpdate();
                continue;
            }
            if (ending == Ending.BLOCKED || ending != Ending.EJECT) continue;
            this.eject(currentItem);
            iterator.remove();
            BeltTunnelInteractionHandler.flapTunnel(this, lastOffset, movementFacing, false);
            this.belt.notifyUpdate();
        }
    }

    protected boolean handleBeltProcessingAndCheckIfRemoved(TransportedItemStack currentItem, float nextOffset, boolean noMovement) {
        int currentSegment = (int)currentItem.beltPosition;
        if (currentItem.locked) {
            BeltProcessingBehaviour processingBehaviour = this.getBeltProcessingAtSegment(currentSegment);
            TransportedItemStackHandlerBehaviour stackHandlerBehaviour = this.getTransportedItemStackHandlerAtSegment(currentSegment);
            if (stackHandlerBehaviour == null) {
                return false;
            }
            if (processingBehaviour == null) {
                currentItem.locked = false;
                this.belt.notifyUpdate();
                return false;
            }
            BeltProcessingBehaviour.ProcessingResult result = processingBehaviour.handleHeldItem(currentItem, stackHandlerBehaviour);
            if (result == BeltProcessingBehaviour.ProcessingResult.REMOVE) {
                return true;
            }
            if (result == BeltProcessingBehaviour.ProcessingResult.HOLD) {
                return false;
            }
            currentItem.locked = false;
            this.belt.notifyUpdate();
            return false;
        }
        if (noMovement) {
            return false;
        }
        if (currentItem.beltPosition > 0.5f || this.beltMovementPositive) {
            int firstUpcomingSegment = (int)(currentItem.beltPosition + (this.beltMovementPositive ? 0.5f : -0.5f));
            int step = this.beltMovementPositive ? 1 : -1;
            int segment = firstUpcomingSegment;
            while (this.beltMovementPositive ? (float)segment + 0.5f <= nextOffset : (float)segment + 0.5f >= nextOffset) {
                BeltProcessingBehaviour processingBehaviour = this.getBeltProcessingAtSegment(segment);
                TransportedItemStackHandlerBehaviour stackHandlerBehaviour = this.getTransportedItemStackHandlerAtSegment(segment);
                if (processingBehaviour != null && stackHandlerBehaviour != null && !BeltProcessingBehaviour.isBlocked((BlockGetter)this.belt.getLevel(), BeltHelper.getPositionForOffset(this.belt, segment))) {
                    BeltProcessingBehaviour.ProcessingResult result = processingBehaviour.handleReceivedItem(currentItem, stackHandlerBehaviour);
                    if (result == BeltProcessingBehaviour.ProcessingResult.REMOVE) {
                        return true;
                    }
                    if (result == BeltProcessingBehaviour.ProcessingResult.HOLD) {
                        currentItem.beltPosition = (float)segment + 0.5f + (this.beltMovementPositive ? 0.001953125f : -0.001953125f);
                        currentItem.locked = true;
                        this.belt.notifyUpdate();
                        return false;
                    }
                }
                segment += step;
            }
        }
        return false;
    }

    protected BeltProcessingBehaviour getBeltProcessingAtSegment(int segment) {
        return BlockEntityBehaviour.get((BlockGetter)this.belt.getLevel(), BeltHelper.getPositionForOffset(this.belt, segment).above(2), BeltProcessingBehaviour.TYPE);
    }

    protected TransportedItemStackHandlerBehaviour getTransportedItemStackHandlerAtSegment(int segment) {
        return BlockEntityBehaviour.get((BlockGetter)this.belt.getLevel(), BeltHelper.getPositionForOffset(this.belt, segment), TransportedItemStackHandlerBehaviour.TYPE);
    }

    private Ending resolveEnding() {
        BlockPos nextPosition;
        Level world = this.belt.getLevel();
        DirectBeltInputBehaviour inputBehaviour = BlockEntityBehaviour.get((BlockGetter)world, nextPosition = BeltHelper.getPositionForOffset(this.belt, this.beltMovementPositive ? this.belt.beltLength : -1), DirectBeltInputBehaviour.TYPE);
        if (inputBehaviour != null) {
            return Ending.INSERT;
        }
        if (BlockHelper.hasBlockSolidSide(world.getBlockState(nextPosition), (BlockGetter)world, nextPosition, this.belt.getMovementFacing().getOpposite())) {
            return Ending.BLOCKED;
        }
        return Ending.EJECT;
    }

    public boolean canInsertAt(int segment) {
        return this.canInsertAtFromSide(segment, Direction.UP);
    }

    public boolean canInsertAtFromSide(int segment, Direction side) {
        float segmentPos = segment;
        if (this.belt.getMovementFacing() == side.getOpposite()) {
            return false;
        }
        if (this.belt.getMovementFacing() != side) {
            segmentPos += 0.5f;
        } else if (!this.beltMovementPositive) {
            segmentPos += 1.0f;
        }
        for (TransportedItemStack stack : this.items) {
            if (!this.isBlocking(segment, side, segmentPos, stack)) continue;
            return false;
        }
        for (TransportedItemStack stack : this.toInsert) {
            if (!this.isBlocking(segment, side, segmentPos, stack)) continue;
            return false;
        }
        return true;
    }

    private boolean isBlocking(int segment, Direction side, float segmentPos, TransportedItemStack stack) {
        float currentPos = stack.beltPosition;
        return stack.insertedAt == segment && stack.insertedFrom == side && (this.beltMovementPositive ? currentPos <= segmentPos + 1.0f : currentPos >= segmentPos - 1.0f);
    }

    public void addItem(TransportedItemStack newStack) {
        this.toInsert.add(newStack);
    }

    private void insert(TransportedItemStack newStack) {
        if (this.items.isEmpty()) {
            this.items.add(newStack);
        } else {
            int index = 0;
            for (TransportedItemStack stack : this.items) {
                if (stack.compareTo(newStack) > 0 == this.beltMovementPositive) break;
                ++index;
            }
            this.items.add(index, newStack);
        }
    }

    public TransportedItemStack getStackAtOffset(int offset) {
        float min = offset;
        float max = offset + 1;
        for (TransportedItemStack stack : this.items) {
            if (this.toRemove.contains(stack) || stack.beltPosition > max || !(stack.beltPosition > min)) continue;
            return stack;
        }
        return null;
    }

    public void read(CompoundTag nbt, HolderLookup.Provider registries) {
        this.items.clear();
        nbt.getList("Items", 10).forEach(inbt -> this.items.add(TransportedItemStack.read((CompoundTag)inbt, registries)));
        if (nbt.contains("LazyItem")) {
            this.lazyClientItem = TransportedItemStack.read(nbt.getCompound("LazyItem"), registries);
        }
        this.beltMovementPositive = nbt.getBoolean("PositiveOrder");
    }

    public CompoundTag write(HolderLookup.Provider registries) {
        if (!this.toInsert.isEmpty() || !this.toRemove.isEmpty()) {
            this.toInsert.forEach(this::insert);
            this.toInsert.clear();
            this.items.removeAll(this.toRemove);
            this.toRemove.clear();
        }
        CompoundTag nbt = new CompoundTag();
        ListTag itemsNBT = new ListTag();
        this.items.forEach(stack -> itemsNBT.add((Object)stack.serializeNBT(registries)));
        nbt.put("Items", (Tag)itemsNBT);
        if (this.lazyClientItem != null) {
            nbt.put("LazyItem", (Tag)this.lazyClientItem.serializeNBT(registries));
        }
        nbt.putBoolean("PositiveOrder", this.beltMovementPositive);
        return nbt;
    }

    public void eject(TransportedItemStack stack) {
        ItemStack ejected = stack.stack;
        Vec3 outPos = BeltHelper.getVectorForOffset(this.belt, stack.beltPosition);
        float movementSpeed = Math.max(Math.abs(this.belt.getBeltMovementSpeed()), 0.125f);
        Vec3 outMotion = Vec3.atLowerCornerOf((Vec3i)this.belt.getBeltChainDirection()).scale((double)movementSpeed).add(0.0, 0.125, 0.0);
        outPos = outPos.add(outMotion.normalize().scale(0.001));
        ItemEntity entity = new ItemEntity(this.belt.getLevel(), outPos.x, outPos.y + 0.375, outPos.z, ejected);
        entity.setDeltaMovement(outMotion);
        entity.setDefaultPickUpDelay();
        entity.hurtMarked = true;
        this.belt.getLevel().addFreshEntity((Entity)entity);
    }

    public void ejectAll() {
        this.items.forEach(this::eject);
        this.items.clear();
    }

    public void applyToEachWithin(float position, float maxDistanceToPosition, Function<TransportedItemStack, TransportedItemStackHandlerBehaviour.TransportedResult> processFunction) {
        boolean dirty = false;
        for (TransportedItemStack transported : this.items) {
            TransportedItemStackHandlerBehaviour.TransportedResult result;
            if (this.toRemove.contains(transported)) continue;
            ItemStack stackBefore = transported.stack.copy();
            if (Math.abs(position - transported.beltPosition) >= maxDistanceToPosition || (result = processFunction.apply(transported)) == null || result.didntChangeFrom(stackBefore)) continue;
            dirty = true;
            if (result.hasHeldOutput()) {
                TransportedItemStack held = result.getHeldOutput();
                held.beltPosition = (float)((int)position) + 0.5f - (this.beltMovementPositive ? 0.001953125f : -0.001953125f);
                this.toInsert.add(held);
            }
            this.toInsert.addAll(result.getOutputs());
            this.toRemove.add(transported);
        }
        if (dirty) {
            this.belt.notifyUpdate();
        }
    }

    public List<TransportedItemStack> getTransportedItems() {
        return this.items;
    }

    @Nullable
    public TransportedItemStack getLazyClientItem() {
        return this.lazyClientItem;
    }

    private static enum Ending {
        UNRESOLVED(0.0f),
        EJECT(0.0f),
        INSERT(0.25f),
        FUNNEL(0.5f),
        BLOCKED(0.45f);

        private float margin;

        private Ending(float f) {
            this.margin = f;
        }
    }
}
