/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.BlockFace
 *  net.minecraft.core.BlockPos
 */
package com.simibubi.create.api.packager;

import com.simibubi.create.api.packager.InventoryIdentifier;
import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;

public record InventoryIdentifier.Single(BlockPos pos) implements InventoryIdentifier
{
    @Override
    public boolean contains(BlockFace face) {
        return this.pos.equals((Object)face.getPos());
    }
}
