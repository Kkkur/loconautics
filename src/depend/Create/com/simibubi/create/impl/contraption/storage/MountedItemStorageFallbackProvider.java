/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.Block
 *  net.neoforged.neoforge.common.NeoForge
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.impl.contraption.storage;

import com.simibubi.create.AllMountedStorageTypes;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.registry.SimpleRegistry;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

public enum MountedItemStorageFallbackProvider implements SimpleRegistry.Provider<Block, MountedItemStorageType<?>>
{
    INSTANCE;


    @Override
    @Nullable
    public MountedItemStorageType<?> get(Block block) {
        return AllTags.AllBlockTags.FALLBACK_MOUNTED_STORAGE_BLACKLIST.matches(block) ? null : (MountedItemStorageType)AllMountedStorageTypes.FALLBACK.get();
    }

    @Override
    public void onRegister(Runnable invalidate) {
        NeoForge.EVENT_BUS.addListener(event -> {
            if (event.shouldUpdateStaticData()) {
                invalidate.run();
            }
        });
    }
}
