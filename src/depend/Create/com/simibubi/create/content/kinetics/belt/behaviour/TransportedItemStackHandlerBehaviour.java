/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.belt.behaviour;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import java.util.function.Function;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TransportedItemStackHandlerBehaviour
extends BlockEntityBehaviour {
    public static final BehaviourType<TransportedItemStackHandlerBehaviour> TYPE = new BehaviourType();
    private ProcessingCallback processingCallback;
    private PositionGetter positionGetter;

    public TransportedItemStackHandlerBehaviour(SmartBlockEntity be, ProcessingCallback processingCallback) {
        super(be);
        this.processingCallback = processingCallback;
        this.positionGetter = t -> VecHelper.getCenterOf((Vec3i)be.getBlockPos());
    }

    public TransportedItemStackHandlerBehaviour withStackPlacement(PositionGetter function) {
        this.positionGetter = function;
        return this;
    }

    public void handleProcessingOnAllItems(Function<TransportedItemStack, TransportedResult> processFunction) {
        this.handleCenteredProcessingOnAllItems(0.51f, processFunction);
    }

    public void handleProcessingOnItem(TransportedItemStack item, TransportedResult processOutput) {
        this.handleCenteredProcessingOnAllItems(0.51f, t -> {
            if (t == item) {
                return processOutput;
            }
            return null;
        });
    }

    public void handleCenteredProcessingOnAllItems(float maxDistanceFromCenter, Function<TransportedItemStack, TransportedResult> processFunction) {
        this.processingCallback.applyToAllItems(maxDistanceFromCenter, processFunction);
    }

    public Vec3 getWorldPositionOf(TransportedItemStack transported) {
        return this.positionGetter.getWorldPositionVector(transported);
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    @FunctionalInterface
    public static interface ProcessingCallback {
        public void applyToAllItems(float var1, Function<TransportedItemStack, TransportedResult> var2);
    }

    @FunctionalInterface
    public static interface PositionGetter {
        public Vec3 getWorldPositionVector(TransportedItemStack var1);
    }

    public static class TransportedResult {
        List<TransportedItemStack> outputs;
        TransportedItemStack heldOutput;
        private static final TransportedResult DO_NOTHING = new TransportedResult(null, null);
        private static final TransportedResult REMOVE_ITEM = new TransportedResult((List<TransportedItemStack>)ImmutableList.of(), null);

        public static TransportedResult doNothing() {
            return DO_NOTHING;
        }

        public static TransportedResult removeItem() {
            return REMOVE_ITEM;
        }

        public static TransportedResult convertTo(TransportedItemStack output) {
            return new TransportedResult((List<TransportedItemStack>)ImmutableList.of((Object)output), null);
        }

        public static TransportedResult convertTo(List<TransportedItemStack> outputs) {
            return new TransportedResult(outputs, null);
        }

        public static TransportedResult convertToAndLeaveHeld(List<TransportedItemStack> outputs, TransportedItemStack heldOutput) {
            return new TransportedResult(outputs, heldOutput);
        }

        private TransportedResult(List<TransportedItemStack> outputs, TransportedItemStack heldOutput) {
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
}
