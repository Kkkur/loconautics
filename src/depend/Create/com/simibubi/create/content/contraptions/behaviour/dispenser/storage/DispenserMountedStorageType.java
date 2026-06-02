/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.neoforge.items.IItemHandler
 */
package com.simibubi.create.content.contraptions.behaviour.dispenser.storage;

import com.simibubi.create.api.contraption.storage.item.simple.SimpleMountedStorage;
import com.simibubi.create.api.contraption.storage.item.simple.SimpleMountedStorageType;
import com.simibubi.create.content.contraptions.behaviour.dispenser.storage.DispenserMountedStorage;
import net.neoforged.neoforge.items.IItemHandler;

public class DispenserMountedStorageType
extends SimpleMountedStorageType<DispenserMountedStorage> {
    public DispenserMountedStorageType() {
        super(DispenserMountedStorage.CODEC);
    }

    @Override
    protected SimpleMountedStorage createStorage(IItemHandler handler) {
        return new DispenserMountedStorage(handler);
    }
}
