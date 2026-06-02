/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderGetter
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.schematics.cannon;

import com.simibubi.create.content.schematics.cannon.LaunchedItem;
import com.simibubi.create.foundation.utility.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public static class LaunchedItem.ForBlockState
extends LaunchedItem {
    public BlockState state;
    public CompoundTag data;

    LaunchedItem.ForBlockState() {
    }

    public LaunchedItem.ForBlockState(BlockPos start, BlockPos target, ItemStack stack, BlockState state, CompoundTag data) {
        super(start, target, stack);
        this.state = state;
        this.data = data;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider registries) {
        CompoundTag serializeNBT = super.serializeNBT(registries);
        serializeNBT.put("BlockState", (Tag)NbtUtils.writeBlockState((BlockState)this.state));
        if (this.data != null) {
            this.data.remove("x");
            this.data.remove("y");
            this.data.remove("z");
            this.data.remove("id");
            serializeNBT.put("Data", (Tag)this.data);
        }
        return serializeNBT;
    }

    @Override
    void readNBT(CompoundTag nbt, HolderLookup.Provider registries, HolderGetter<Block> holderGetter) {
        super.readNBT(nbt, registries, holderGetter);
        this.state = NbtUtils.readBlockState(holderGetter, (CompoundTag)nbt.getCompound("BlockState"));
        if (nbt.contains("Data", 10)) {
            this.data = nbt.getCompound("Data");
        }
    }

    @Override
    void place(Level world) {
        BlockHelper.placeSchematicBlock(world, this.state, this.target, this.stack, this.data);
    }
}
