/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.equipment.zapper.terrainzapper;

import com.simibubi.create.content.equipment.zapper.terrainzapper.Brush;
import com.simibubi.create.content.equipment.zapper.terrainzapper.TerrainTools;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class DynamicBrush
extends Brush {
    public static final int MAX_RADIUS = 10;
    private boolean surface;

    public DynamicBrush(boolean surface) {
        super(1);
        this.surface = surface;
    }

    @Override
    Component getParamLabel(int paramIndex) {
        return CreateLang.translateDirect("generic.range", new Object[0]);
    }

    @Override
    public TerrainTools[] getSupportedTools() {
        TerrainTools[] terrainToolsArray;
        if (this.surface) {
            TerrainTools[] terrainToolsArray2 = new TerrainTools[3];
            terrainToolsArray2[0] = TerrainTools.Overlay;
            terrainToolsArray2[1] = TerrainTools.Replace;
            terrainToolsArray = terrainToolsArray2;
            terrainToolsArray2[2] = TerrainTools.Clear;
        } else {
            TerrainTools[] terrainToolsArray3 = new TerrainTools[2];
            terrainToolsArray3[0] = TerrainTools.Replace;
            terrainToolsArray = terrainToolsArray3;
            terrainToolsArray3[1] = TerrainTools.Clear;
        }
        return terrainToolsArray;
    }

    @Override
    public boolean hasPlacementOptions() {
        return false;
    }

    @Override
    public boolean hasConnectivityOptions() {
        return true;
    }

    @Override
    int getMax(int paramIndex) {
        return 10;
    }

    @Override
    int getMin(int paramIndex) {
        return 1;
    }

    @Override
    public TerrainTools redirectTool(TerrainTools tool) {
        if (tool == TerrainTools.Overlay) {
            return TerrainTools.Place;
        }
        return super.redirectTool(tool);
    }

    @Override
    public Collection<BlockPos> addToGlobalPositions(LevelAccessor world, BlockPos targetPos, Direction targetFace, Collection<BlockPos> affectedPositions, TerrainTools usedTool) {
        boolean searchDiagonals = this.param1 == 0;
        boolean fuzzy = this.param2 == 0;
        boolean replace = usedTool != TerrainTools.Overlay;
        int searchRange = this.param0;
        HashSet<BlockPos> visited = new HashSet<BlockPos>();
        LinkedList<BlockPos> frontier = new LinkedList<BlockPos>();
        BlockState state = world.getBlockState(targetPos);
        LinkedList<BlockPos> offsets = new LinkedList<BlockPos>();
        for (int x = -1; x <= 1; ++x) {
            for (int y = -1; y <= 1; ++y) {
                for (int z = -1; z <= 1; ++z) {
                    if (Math.abs(x) + Math.abs(y) + Math.abs(z) >= 2 && !searchDiagonals || targetFace.getAxis().choose(x, y, z) != 0 && this.surface) continue;
                    offsets.add(new BlockPos(x, y, z));
                }
            }
        }
        BlockPos startPos = replace ? targetPos : targetPos.relative(targetFace);
        frontier.add(startPos);
        while (!frontier.isEmpty()) {
            BlockPos currentPos = (BlockPos)frontier.remove(0);
            if (visited.contains(currentPos)) continue;
            visited.add(currentPos);
            if (!currentPos.closerThan((Vec3i)startPos, (double)searchRange)) continue;
            if (replace) {
                BlockState stateToReplace = world.getBlockState(currentPos);
                BlockState stateAboveStateToReplace = world.getBlockState(currentPos.relative(targetFace));
                if (stateToReplace.getDestroySpeed((BlockGetter)world, currentPos) == -1.0f || stateToReplace.getBlock() != state.getBlock() && !fuzzy || stateToReplace.canBeReplaced() || BlockHelper.hasBlockSolidSide(stateAboveStateToReplace, (BlockGetter)world, currentPos.relative(targetFace), targetFace.getOpposite()) && this.surface) continue;
                affectedPositions.add(currentPos);
                for (BlockPos offset : offsets) {
                    frontier.add(currentPos.offset((Vec3i)offset));
                }
                continue;
            }
            BlockState stateToPlaceAt = world.getBlockState(currentPos);
            BlockState stateToPlaceOn = world.getBlockState(currentPos.relative(targetFace.getOpposite()));
            if (stateToPlaceOn.canBeReplaced() || stateToPlaceOn.getBlock() != state.getBlock() && !fuzzy || !stateToPlaceAt.canBeReplaced()) continue;
            affectedPositions.add(currentPos);
            for (BlockPos offset : offsets) {
                frontier.add(currentPos.offset((Vec3i)offset));
            }
        }
        return affectedPositions;
    }
}
