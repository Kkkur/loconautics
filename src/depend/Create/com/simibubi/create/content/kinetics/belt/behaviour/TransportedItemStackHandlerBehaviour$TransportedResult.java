/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.belt.behaviour;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public static class TransportedItemStackHandlerBehaviour.TransportedResult {
    List<TransportedItemStack> outputs;
    TransportedItemStack heldOutput;
    private static final TransportedItemStackHandlerBehaviour.TransportedResult DO_NOTHING = new TransportedItemStackHandlerBehaviour.TransportedResult(null, null);
    private static final TransportedItemStackHandlerBehaviour.TransportedResult REMOVE_ITEM = new TransportedItemStackHandlerBehaviour.TransportedResult((List<TransportedItemStack>)ImmutableList.of(), null);

    public static TransportedItemStackHandlerBehaviour.TransportedResult doNothing() {
        return DO_NOTHING;
    }

    public static TransportedItemStackHandlerBehaviour.TransportedResult removeItem() {
        return REMOVE_ITEM;
    }

    public static TransportedItemStackHandlerBehaviour.TransportedResult convertTo(TransportedItemStack output) {
        return new TransportedItemStackHandlerBehaviour.TransportedResult((List<TransportedItemStack>)ImmutableList.of((Object)output), null);
    }

    public static TransportedItemStackHandlerBehaviour.TransportedResult convertTo(List<TransportedItemStack> outputs) {
        return new TransportedItemStackHandlerBehaviour.TransportedResult(outputs, null);
    }

    public static TransportedItemStackHandlerBehaviour.TransportedResult convertToAndLeaveHeld(List<TransportedItemStack> outputs, TransportedItemStack heldOutput) {
        return new TransportedItemStackHandlerBehaviour.TransportedResult(outputs, heldOutput);
    }

    private TransportedItemStackHandlerBehaviour.TransportedResult(List<TransportedItemStack> outputs, TransportedItemStack heldOutput) {
        this.outputs = outputs;
        this.heldOutput = heldOutput;
    }

    public boolean doesNothing() {
        return this.outputs == null;
    }

    public boolean didntChangeFrom(ItemStack stackBefore) {
        return this.doesNothing() || this.outputs.size() == 1 && ItemStack.matches((ItemStack)this.outputs.get((int)0).stack, (ItemStack)stackBefore) && !this.hasHeldOutput();
    }

    public List<TransportedItemStack> getOutputs() {
        if (this.outputs == null) {
            throw new IllegalStateException("Do not call getOutputs() on a Result that doesNothing().");
        }
        return this.outputs;
    }

    public boolean hasHeldOutput() {
        return this.heldOutput != null;
    }

    @Nullable
    public TransportedItemStack getHeldOutput() {
        if (this.heldOutput == null) {
            throw new IllegalStateException("Do not call getHeldOutput() on a Result with hasHeldOutput() == false.");
        }
        return this.heldOutput;
    }
}
