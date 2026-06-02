/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.BlockFace
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 */
package com.simibubi.create.api.packager;

import com.simibubi.create.api.packager.InventoryIdentifier;
import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public record InventoryIdentifier.Bounds(BoundingBox bounds) implements InventoryIdentifier
{
    @Override
    public boolean contains(BlockFace face) {
        return this.bounds.isInside((Vec3i)face.getPos());
    }
}
