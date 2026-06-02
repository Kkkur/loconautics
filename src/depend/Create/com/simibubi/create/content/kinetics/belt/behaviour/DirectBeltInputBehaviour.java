/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.belt.behaviour;

import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

public class DirectBeltInputBehaviour
extends BlockEntityBehaviour {
    public static final BehaviourType<DirectBeltInputBehaviour> TYPE = new BehaviourType();
    private InsertionCallback tryInsert = this::defaultInsertionCallback;
    private OccupiedPredicate isOccupied;
    private AvailabilityPredicate canInsert = d -> true;
    private Supplier<Boolean> supportsBeltFunnels;

    public DirectBeltInputBehaviour(SmartBlockEntity be) {
        super(be);
        this.isOccupied = d -> false;
        this.supportsBeltFunnels = () -> false;
    }

    public DirectBeltInputBehaviour allowingBeltFunnelsWhen(Supplier<Boolean> pred) {
        this.supportsBeltFunnels = pred;
        return this;
    }

    public DirectBeltInputBehaviour allowingBeltFunnels() {
        this.supportsBeltFunnels = () -> true;
        return this;
    }

    public DirectBeltInputBehaviour onlyInsertWhen(AvailabilityPredicate pred) {
        this.canInsert = pred;
        return this;
    }

    public DirectBeltInputBehaviour considerOccupiedWhen(OccupiedPredicate pred) {
        this.isOccupied = pred;
        return this;
    }

    public DirectBeltInputBehaviour setInsertionHandler(InsertionCallback callback) {
        this.tryInsert = callback;
        return this;
    }

    private ItemStack defaultInsertionCallback(TransportedItemStack inserted, Direction side, boolean simulate) {
        IItemHandler lazy = (IItemHandler)this.blockEntity.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, this.blockEntity.getBlockPos(), (Object)side);
        if (lazy == null) {
            return inserted.stack;
        }
        return ItemHandlerHelper.insertItemStacked((IItemHandler)lazy, (ItemStack)inserted.stack.copy(), (boolean)simulate);
    }

    public boolean canInsertFromSide(Direction side) {
        return this.canInsert.test(side);
    }

    public boolean isOccupied(Direction side) {
        return this.isOccupied.test(side);
    }

    public ItemStack handleInsertion(ItemStack stack, Direction side, boolean simulate) {
        return this.handleInsertion(new TransportedItemStack(stack), side, simulate);
    }

    public ItemStack handleInsertion(TransportedItemStack stack, Direction side, boolean simulate) {
        return this.tryInsert.apply(stack, side, simulate);
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    @Nullable
    public ItemStack tryExportingToBeltFunnel(ItemStack stack, @Nullable Direction side, boolean simulate) {
        BlockPos funnelPos = this.blockEntity.getBlockPos().above();
        Level world = this.getWorld();
        BlockState funnelState = world.getBlockState(funnelPos);
        if (!(funnelState.getBlock() instanceof BeltFunnelBlock)) {
            return null;
        }
        if (funnelState.getValue(BeltFunnelBlock.SHAPE) != BeltFunnelBlock.Shape.PULLING) {
            return null;
        }
        if (side != null && FunnelBlock.getFunnelFacing(funnelState) != side) {
            return null;
        }
        BlockEntity be = world.getBlockEntity(funnelPos);
        if (!(be instanceof FunnelBlockEntity)) {
            return null;
        }
        if (((Boolean)funnelState.getValue((Property)BeltFunnelBlock.POWERED)).booleanValue()) {
            return stack;
        }
        ItemStack insert = FunnelBlock.tryInsert(world, funnelPos, stack, simulate);
        if (insert.getCount() != stack.getCount() && !simulate) {
            ((FunnelBlockEntity)be).flap(true);
        }
        return insert;
    }

    public boolean canSupportBeltFunnels() {
        return this.supportsBeltFunnels.get();
    }

    @FunctionalInterface
    public static interface InsertionCallback {
        public ItemStack apply(TransportedItemStack var1, Direction var2, boolean var3);
    }

    @FunctionalInterface
    public static interface AvailabilityPredicate {
        public boolean test(Direction var1);
    }

    @FunctionalInterface
    public static interface OccupiedPredicate {
        public boolean test(Direction var1);
    }
}
