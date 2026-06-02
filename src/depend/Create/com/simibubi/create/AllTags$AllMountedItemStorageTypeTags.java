/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 */
package com.simibubi.create;

import com.simibubi.create.AllTags;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.registry.CreateRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public static enum AllTags.AllMountedItemStorageTypeTags {
    INTERNAL,
    FUEL_BLACKLIST;

    public final TagKey<MountedItemStorageType<?>> tag;

    private AllTags.AllMountedItemStorageTypeTags() {
        this(AllTags.NameSpace.MOD);
    }

    private AllTags.AllMountedItemStorageTypeTags(AllTags.NameSpace namespace) {
        this(namespace, null);
    }

    private AllTags.AllMountedItemStorageTypeTags(AllTags.NameSpace namespace, String pathOverride) {
        this.tag = TagKey.create(CreateRegistries.MOUNTED_ITEM_STORAGE_TYPE, (ResourceLocation)namespace.id(this, pathOverride));
    }

    public boolean matches(MountedItemStorage storage) {
        return this.matches(storage.type);
    }

    public boolean matches(MountedItemStorageType<?> type) {
        return type.is(this.tag);
    }
}
