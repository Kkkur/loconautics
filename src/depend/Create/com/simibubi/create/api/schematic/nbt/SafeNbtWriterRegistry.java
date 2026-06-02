/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 */
package com.simibubi.create.api.schematic.nbt;

import com.simibubi.create.api.registry.SimpleRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class SafeNbtWriterRegistry {
    public static final SimpleRegistry<BlockEntityType<?>, SafeNbtWriter> REGISTRY = SimpleRegistry.create();

    private SafeNbtWriterRegistry() {
        throw new AssertionError((Object)"This class should not be instantiated");
    }

    @FunctionalInterface
    public static interface SafeNbtWriter {
        public void writeSafe(BlockEntity var1, CompoundTag var2, HolderLookup.Provider var3);
    }
}
