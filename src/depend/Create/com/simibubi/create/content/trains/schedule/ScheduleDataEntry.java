/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 */
package com.simibubi.create.content.trains.schedule;

import com.simibubi.create.content.trains.schedule.IScheduleInput;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public abstract class ScheduleDataEntry
implements IScheduleInput {
    protected CompoundTag data = new CompoundTag();

    @Override
    public CompoundTag getData() {
        return this.data;
    }

    @Override
    public void setData(HolderLookup.Provider registries, CompoundTag data) {
        this.data = data;
        this.readAdditional(registries, data);
    }

    protected void writeAdditional(HolderLookup.Provider registries, CompoundTag tag) {
    }

    protected void readAdditional(HolderLookup.Provider registries, CompoundTag tag) {
    }

    protected <T> T enumData(String key, Class<T> enumClass) {
        T[] enumConstants = enumClass.getEnumConstants();
        return enumConstants[this.data.getInt(key) % enumConstants.length];
    }

    protected String textData(String key) {
        return this.data.getString(key);
    }

    protected int intData(String key) {
        return this.data.getInt(key);
    }
}
