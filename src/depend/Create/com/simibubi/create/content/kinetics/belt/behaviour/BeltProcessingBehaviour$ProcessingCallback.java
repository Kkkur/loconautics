/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.kinetics.belt.behaviour;

import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;

@FunctionalInterface
public static interface BeltProcessingBehaviour.ProcessingCallback {
    public BeltProcessingBehaviour.ProcessingResult apply(TransportedItemStack var1, TransportedItemStackHandlerBehaviour var2);
}
