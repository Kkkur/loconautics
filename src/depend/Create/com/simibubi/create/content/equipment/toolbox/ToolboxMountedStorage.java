/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.IItemHandlerModifiable
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.equipment.toolbox;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllMountedStorageTypes;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.contraption.storage.item.WrapperMountedItemStorage;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.equipment.toolbox.ToolboxInventory;
import com.simibubi.create.foundation.item.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

public class ToolboxMountedStorage
extends WrapperMountedItemStorage<ToolboxInventory> {
    public static final MapCodec<ToolboxMountedStorage> CODEC = ToolboxInventory.CODEC.xmap(ToolboxMountedStorage::new, storage -> (ToolboxInventory)storage.wrapped).fieldOf("value");

    protected ToolboxMountedStorage(MountedItemStorageType<?> type, ToolboxInventory wrapped) {
        super(type, wrapped);
    }

    protected ToolboxMountedStorage(ToolboxInventory wrapped) {
        this((MountedItemStorageType)AllMountedStorageTypes.TOOLBOX.get(), wrapped);
    }

    @Override
    public void unmount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        if (be instanceof ToolboxBlockEntity) {
            ToolboxBlockEntity toolbox = (ToolboxBlockEntity)be;
            ItemHelper.copyContents((IItemHandler)this, (IItemHandlerModifiable)toolbox.inventory);
        }
    }

    @Override
    public boolean handleInteraction(ServerPlayer player, Contraption contraption, StructureTemplate.StructureBlockInfo info) {
        return false;
    }

    public static ToolboxMountedStorage fromToolbox(ToolboxBlockEntity toolbox) {
        ToolboxInventory copy = new ToolboxInventory(null);
        ItemHelper.copyContents((IItemHandler)toolbox.inventory, (IItemHandlerModifiable)copy);
        copy.filters.clear();
        for (ItemStack stack : toolbox.inventory.filters) {
            copy.filters.add(stack.copy());
        }
        return new ToolboxMountedStorage(copy);
    }

    public static ToolboxMountedStorage fromLegacy(HolderLookup.Provider registries, CompoundTag nbt) {
        ToolboxInventory inv = new ToolboxInventory(null);
        inv.deserializeNBT(registries, nbt);
        return new ToolboxMountedStorage(inv);
    }
}
