/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.shapes.EntityCollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package dev.ryanhcode.sable.physics.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class SubLevelEntityCollisionContext
extends EntityCollisionContext {
    public SubLevelEntityCollisionContext(Entity entity) {
        super(entity);
    }

    public boolean isAbove(VoxelShape voxelShape, BlockPos blockPos, boolean bl) {
        return false;
    }
}
