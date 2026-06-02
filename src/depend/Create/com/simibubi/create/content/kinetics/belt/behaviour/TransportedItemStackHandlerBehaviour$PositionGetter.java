/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.belt.behaviour;

import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import net.minecraft.world.phys.Vec3;

@FunctionalInterface
public static interface TransportedItemStackHandlerBehaviour.PositionGetter {
    public Vec3 getWorldPositionVector(TransportedItemStack var1);
}
