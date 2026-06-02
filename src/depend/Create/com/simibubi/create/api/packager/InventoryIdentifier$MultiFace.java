/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.BlockFace
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 */
package com.simibubi.create.api.packager;

import com.simibubi.create.api.packager.InventoryIdentifier;
import java.util.Set;
import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public record InventoryIdentifier.MultiFace(BlockPos pos, Set<Direction> sides) implements InventoryIdentifier
{
    @Override
    public boolean contains(BlockFace face) {
        return this.pos.equals((Object)face.getPos()) && this.sides.contains(face.getFace());
    }
}
