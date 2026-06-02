/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.tags.IntrinsicHolderTagsProvider
 *  net.neoforged.neoforge.common.data.ExistingFileHelper
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.infrastructure.data;

import com.simibubi.create.AllMountedStorageTypes;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.registry.CreateRegistries;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class CreateMountedItemStorageTypeTagsProvider
extends IntrinsicHolderTagsProvider<MountedItemStorageType<?>> {
    public CreateMountedItemStorageTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, CreateRegistries.MOUNTED_ITEM_STORAGE_TYPE, lookupProvider, type -> type.holder.key(), "create", existingFileHelper);
    }

    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(AllTags.AllMountedItemStorageTypeTags.INTERNAL.tag).add((Object)((MountedItemStorageType)AllMountedStorageTypes.DISPENSER.get()));
        this.tag(AllTags.AllMountedItemStorageTypeTags.FUEL_BLACKLIST.tag).add((Object)((MountedItemStorageType)AllMountedStorageTypes.VAULT.get()));
    }

    public String getName() {
        return "Create's Mounted Item Storage Type Tags";
    }
}
