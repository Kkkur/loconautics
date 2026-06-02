/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.Entity
 */
package com.simibubi.create.content.schematics;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

@FunctionalInterface
public static interface SchematicPrinter.EntityTargetHandler {
    public void handle(BlockPos var1, Entity var2);
}
