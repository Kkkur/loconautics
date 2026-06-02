/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.LevelAccessor
 */
package com.simibubi.create.content.equipment.zapper.terrainzapper;

import com.simibubi.create.content.equipment.zapper.terrainzapper.Brush;
import com.simibubi.create.content.equipment.zapper.terrainzapper.TerrainTools;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;

public abstract class ShapedBrush
extends Brush {
    public ShapedBrush(int amtParams) {
        super(amtParams);
    }

    @Override
    public Collection<BlockPos> addToGlobalPositions(LevelAccessor world, BlockPos targetPos, Direction targetFace, Collection<BlockPos> affectedPositions, TerrainTools usedTool) {
        List<BlockPos> includedPositions = this.getIncludedPositions();
        if (includedPositions == null) {
            return affectedPositions;
        }
        for (BlockPos blockPos : includedPositions) {
            affectedPositions.add(targetPos.offset((Vec3i)blockPos));
        }
        return affectedPositions;
    }

    abstract List<BlockPos> getIncludedPositions();
}
