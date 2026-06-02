/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 */
package dev.simulated_team.simulated.multiloader.inventory;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public interface NBTSerializable {
    public CompoundTag write(HolderLookup.Provider var1);

    public void read(HolderLookup.Provider var1, CompoundTag var2);
}
