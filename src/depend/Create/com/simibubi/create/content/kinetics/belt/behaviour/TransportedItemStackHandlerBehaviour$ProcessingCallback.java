/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.kinetics.belt.behaviour;

import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import java.util.function.Function;

@FunctionalInterface
public static interface TransportedItemStackHandlerBehaviour.ProcessingCallback {
    public void applyToAllItems(float var1, Function<TransportedItemStack, TransportedItemStackHandlerBehaviour.TransportedResult> var2);
}
