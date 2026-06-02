/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.neoforged.fml.loading.LoadingModList
 */
package com.simibubi.create.compat;

import java.util.Optional;
import java.util.function.Supplier;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.loading.LoadingModList;

public enum Mods {
    AETHER,
    AETHER_II,
    BETTEREND,
    COMPUTERCRAFT,
    CURIOS,
    DYNAMICTREES,
    JEI,
    FUNCTIONALSTORAGE,
    OCCULTISM,
    PACKETFIXER,
    SOPHISTICATEDBACKPACKS,
    SOPHISTICATEDSTORAGE,
    STORAGEDRAWERS,
    TCONSTRUCT,
    FRAMEDBLOCKS,
    XLPACKETS,
    MODERNUI,
    FTBCHUNKS,
    JOURNEYMAP,
    XAEROWORLDMAP,
    FTBLIBRARY,
    SODIUM,
    INVENTORYSORTER,
    FARMERSDELIGHT;

    private final String id = Lang.asId((String)this.name());
    private final boolean isLoaded = LoadingModList.get().getModFileById(this.id) != null;

    public String id() {
        return this.id;
    }

    public ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath((String)this.id, (String)path);
    }

    public Block getBlock(String id) {
        return (Block)BuiltInRegistries.BLOCK.get(this.rl(id));
    }

    public Item getItem(String id) {
        return (Item)BuiltInRegistries.ITEM.get(this.rl(id));
    }

    public boolean contains(ItemLike entry) {
        if (!this.isLoaded()) {
            return false;
        }
        Item asItem = entry.asItem();
        return asItem != null && RegisteredObjectsHelper.getKeyOrThrow((Item)asItem).getNamespace().equals(this.id);
    }

    public boolean isLoaded() {
        return this.isLoaded;
    }

    public <T> Optional<T> runIfInstalled(Supplier<Supplier<T>> toRun) {
        if (this.isLoaded()) {
            return Optional.of(toRun.get().get());
        }
        return Optional.empty();
    }

    public void executeIfInstalled(Supplier<Runnable> toExecute) {
        if (this.isLoaded()) {
            toExecute.get().run();
        }
    }
}
