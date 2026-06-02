/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.kinetics.belt.behaviour;

import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.funnel.AbstractFunnelBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class BeltProcessingBehaviour
extends BlockEntityBehaviour {
    public static final BehaviourType<BeltProcessingBehaviour> TYPE = new BehaviourType();
    private ProcessingCallback onItemEnter = (s, i) -> ProcessingResult.PASS;
    private ProcessingCallback continueProcessing = (s, i) -> ProcessingResult.PASS;

    public BeltProcessingBehaviour(SmartBlockEntity be) {
        super(be);
    }

    public BeltProcessingBehaviour whenItemEnters(ProcessingCallback callback) {
        this.onItemEnter = callback;
        return this;
    }

    public BeltProcessingBehaviour whileItemHeld(ProcessingCallback callback) {
        this.continueProcessing = callback;
        return this;
    }

    public static boolean isBlocked(BlockGetter world, BlockPos processingSpace) {
        BlockState blockState = world.getBlockState(processingSpace.above());
        if (AbstractFunnelBlock.isFunnel(blockState)) {
            return false;
        }
        return !blockState.getCollisionShape(world, processingSpace.above()).isEmpty();
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    public ProcessingResult handleReceivedItem(TransportedItemStack stack, TransportedItemStackHandlerBehaviour inventory) {
        return this.onItemEnter.apply(stack, inventory);
    }

    public ProcessingResult handleHeldItem(TransportedItemStack stack, TransportedItemStackHandlerBehaviour inventory) {
        return this.continueProcessing.apply(stack, inventory);
    }

    @FunctionalInterface
    public static interface ProcessingCallback {
        public ProcessingResult apply(TransportedItemStack var1, TransportedItemStackHandlerBehaviour var2);
    }

    public static enum ProcessingResult {
        PASS,
        HOLD,
        REMOVE;

    }
}
