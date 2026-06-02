/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.BlockFace
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 */
package com.simibubi.create.api.packager;

import com.simibubi.create.api.packager.InventoryIdentifier;
import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

public record InventoryIdentifier.Pair(BlockPos first, BlockPos second) implements InventoryIdentifier
{
    public InventoryIdentifier.Pair(BlockPos first, BlockPos second) {
        boolean isFirstLower = first.compareTo((Vec3i)second) < 0;
        this.first = isFirstLower ? first : second;
        this.second = isFirstLower ? second : first;
    }

    @Override
    public boolean contains(BlockFace face) {
        BlockPos pos = face.getPos();
        return this.first.equals((Object)pos) || this.second.equals((Object)pos);
    }
}
