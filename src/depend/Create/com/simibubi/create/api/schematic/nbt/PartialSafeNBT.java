/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 */
package com.simibubi.create.api.schematic.nbt;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public interface PartialSafeNBT {
    public void writeSafe(CompoundTag var1, HolderLookup.Provider var2);
}
