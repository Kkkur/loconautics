/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Container
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.wrapper.InvWrapper
 */
package com.simibubi.create.api.contraption.storage.item.chest;

import com.simibubi.create.api.contraption.storage.item.chest.ChestMountedStorage;
import com.simibubi.create.api.contraption.storage.item.simple.SimpleMountedStorage;
import com.simibubi.create.api.contraption.storage.item.simple.SimpleMountedStorageType;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public class ChestMountedStorageType
extends SimpleMountedStorageType<ChestMountedStorage> {
    public ChestMountedStorageType() {
        super(ChestMountedStorage.CODEC);
    }

    @Override
    protected IItemHandler getHandler(Level level, BlockEntity be) {
        InvWrapper invWrapper;
        if (be instanceof Container) {
            Container container = (Container)be;
            invWrapper = new InvWrapper(container);
        } else {
            invWrapper = null;
        }
        return invWrapper;
    }

    @Override
    protected SimpleMountedStorage createStorage(IItemHandler handler) {
        return new ChestMountedStorage(handler);
    }
}
