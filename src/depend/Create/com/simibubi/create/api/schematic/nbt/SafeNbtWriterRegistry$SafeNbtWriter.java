/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package com.simibubi.create.api.schematic.nbt;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

@FunctionalInterface
public static interface SafeNbtWriterRegistry.SafeNbtWriter {
    public void writeSafe(BlockEntity var1, CompoundTag var2, HolderLookup.Provider var3);
}
