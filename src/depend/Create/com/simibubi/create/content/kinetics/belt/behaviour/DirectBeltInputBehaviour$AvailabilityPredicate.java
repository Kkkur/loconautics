/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 */
package com.simibubi.create.content.kinetics.belt.behaviour;

import net.minecraft.core.Direction;

@FunctionalInterface
public static interface DirectBeltInputBehaviour.AvailabilityPredicate {
    public boolean test(Direction var1);
}
