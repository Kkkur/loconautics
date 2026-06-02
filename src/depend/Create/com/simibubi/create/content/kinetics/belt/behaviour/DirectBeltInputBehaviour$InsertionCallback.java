/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.kinetics.belt.behaviour;

import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public static interface DirectBeltInputBehaviour.InsertionCallback {
    public ItemStack apply(TransportedItemStack var1, Direction var2, boolean var3);
}
