/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderGetter
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.compat.framedblocks;

import com.simibubi.create.compat.Mods;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FramedBlocksInSchematics {
    static final List<String> KEYS_TO_RETAIN = List.of("intangible", "glowing", "reinforced", "camo", "camo_two");

    public static CompoundTag prepareBlockEntityData(BlockState blockState, BlockEntity blockEntity) {
        CompoundTag data = null;
        if (blockEntity == null) {
            return data;
        }
        data = blockEntity.saveWithFullMetadata((HolderLookup.Provider)blockEntity.getLevel().registryAccess());
        ArrayList<String> keysToRemove = new ArrayList<String>();
        for (String key : data.getAllKeys()) {
            if (KEYS_TO_RETAIN.contains(key)) continue;
            keysToRemove.add(key);
        }
        for (String key : keysToRemove) {
            data.remove(key);
        }
        if (data.getCompound("camo").contains("fluid")) {
            data.remove("camo");
        }
        if (data.getCompound("camo_two").contains("fluid")) {
            data.remove("camo_two");
        }
        return data;
    }

    public static ItemRequirement getRequiredItems(BlockState blockState, BlockEntity blockEntity) {
        if (blockEntity == null) {
            return ItemRequirement.NONE;
        }
        CompoundTag data = blockEntity.saveWithFullMetadata((HolderLookup.Provider)blockEntity.getLevel().registryAccess());
        ArrayList<ItemRequirement.StackRequirement> list = new ArrayList<ItemRequirement.StackRequirement>();
        if (data.getBoolean("intangible")) {
            list.add(new ItemRequirement.StackRequirement(new ItemStack((ItemLike)Items.PHANTOM_MEMBRANE), ItemRequirement.ItemUseType.CONSUME));
        }
        if (data.getBoolean("glowing")) {
            list.add(new ItemRequirement.StackRequirement(new ItemStack((ItemLike)Items.GLOWSTONE_DUST), ItemRequirement.ItemUseType.CONSUME));
        }
        if (data.getBoolean("reinforced")) {
            list.add(new ItemRequirement.StackRequirement(new ItemStack((ItemLike)Mods.FRAMEDBLOCKS.getItem("framed_reinforcement")), ItemRequirement.ItemUseType.CONSUME));
        }
        if (data.contains("camo")) {
            FramedBlocksInSchematics.addCamoStack((HolderGetter<Block>)blockEntity.getLevel().holderLookup(Registries.BLOCK), data.getCompound("camo"), list);
        }
        if (data.contains("camo_two")) {
            FramedBlocksInSchematics.addCamoStack((HolderGetter<Block>)blockEntity.getLevel().holderLookup(Registries.BLOCK), data.getCompound("camo_two"), list);
        }
        return new ItemRequirement(list);
    }

    private static void addCamoStack(HolderGetter<Block> level, CompoundTag tag, List<ItemRequirement.StackRequirement> list) {
        if (!tag.contains("state")) {
            return;
        }
        BlockState blockState = NbtUtils.readBlockState(level, (CompoundTag)tag.getCompound("state"));
        ItemStack itemStack = new ItemStack((ItemLike)blockState.getBlock());
        if (!itemStack.isEmpty()) {
            list.add(new ItemRequirement.StackRequirement(itemStack, ItemRequirement.ItemUseType.CONSUME));
        }
    }
}
