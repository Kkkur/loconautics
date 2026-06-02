/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemStackHandler
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.logistics.vault;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllMountedStorageTypes;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.contraption.storage.item.WrapperMountedItemStorage;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.logistics.vault.ItemVaultBlockEntity;
import com.simibubi.create.foundation.codec.CreateCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class ItemVaultMountedStorage
extends WrapperMountedItemStorage<ItemStackHandler> {
    public static final MapCodec<ItemVaultMountedStorage> CODEC = CreateCodecs.ITEM_STACK_HANDLER.xmap(ItemVaultMountedStorage::new, storage -> (ItemStackHandler)storage.wrapped).fieldOf("value");

    protected ItemVaultMountedStorage(MountedItemStorageType<?> type, ItemStackHandler handler) {
        super(type, handler);
    }

    protected ItemVaultMountedStorage(ItemStackHandler handler) {
        this((MountedItemStorageType)AllMountedStorageTypes.VAULT.get(), handler);
    }

    @Override
    public void unmount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        if (be instanceof ItemVaultBlockEntity) {
            ItemVaultBlockEntity vault = (ItemVaultBlockEntity)be;
            vault.applyInventoryToBlock((ItemStackHandler)this.wrapped);
        }
    }

    @Override
    public boolean handleInteraction(ServerPlayer player, Contraption contraption, StructureTemplate.StructureBlockInfo info) {
        return false;
    }

    public static ItemVaultMountedStorage fromVault(ItemVaultBlockEntity vault) {
        return new ItemVaultMountedStorage(ItemVaultMountedStorage.copyToItemStackHandler((IItemHandler)vault.getInventoryOfBlock()));
    }

    public static ItemVaultMountedStorage fromLegacy(HolderLookup.Provider registries, CompoundTag nbt) {
        ItemStackHandler handler = new ItemStackHandler();
        handler.deserializeNBT(registries, nbt);
        return new ItemVaultMountedStorage(handler);
    }
}
