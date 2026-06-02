/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.neoforged.neoforge.items.IItemHandler
 */
package com.simibubi.create.compat.thresholdSwitch;

import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.thresholdSwitch.ThresholdSwitchCompat;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.items.IItemHandler;

public class SophisticatedStorage
implements ThresholdSwitchCompat {
    @Override
    public boolean isFromThisMod(BlockEntity be) {
        if (be == null) {
            return false;
        }
        String namespace = RegisteredObjectsHelper.getKeyOrThrow((BlockEntityType)be.getType()).getNamespace();
        return Mods.SOPHISTICATEDSTORAGE.id().equals(namespace) || Mods.SOPHISTICATEDBACKPACKS.id().equals(namespace);
    }

    @Override
    public long getSpaceInSlot(IItemHandler inv, int slot) {
        return (long)inv.getSlotLimit(slot) * (long)inv.getStackInSlot(slot).getMaxStackSize() / 64L;
    }
}
