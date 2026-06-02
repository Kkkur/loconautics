/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.phys.Vec3
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.simibubi.create.content.equipment.zapper.terrainzapper;

import com.simibubi.create.content.equipment.zapper.terrainzapper.PlacementOptions;
import com.simibubi.create.content.equipment.zapper.terrainzapper.ShapedBrush;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

public class CylinderBrush
extends ShapedBrush {
    public static final int MAX_RADIUS = 8;
    public static final int MAX_HEIGHT = 8;
    private Map<Pair<Integer, Integer>, List<BlockPos>> cachedBrushes = new HashMap<Pair<Integer, Integer>, List<BlockPos>>();

    public CylinderBrush() {
        super(2);
        for (int i = 0; i <= 8; ++i) {
            int radius = i;
            List positions = BlockPos.betweenClosedStream((BlockPos)BlockPos.ZERO.offset(-i - 1, 0, -i - 1), (BlockPos)BlockPos.ZERO.offset(i + 1, 0, i + 1)).map(BlockPos::new).filter(p -> VecHelper.getCenterOf((Vec3i)p).distanceTo(VecHelper.getCenterOf((Vec3i)BlockPos.ZERO)) < (double)((float)radius + 0.42f)).collect(Collectors.toList());
            for (int h = 0; h <= 8; ++h) {
                ArrayList<BlockPos> stackedPositions = new ArrayList<BlockPos>();
                for (int layer = 0; layer < h; ++layer) {
                    int yOffset = layer - h / 2;
                    for (BlockPos p2 : positions) {
                        stackedPositions.add(p2.above(yOffset));
                    }
                }
                this.cachedBrushes.put((Pair<Integer, Integer>)Pair.of((Object)i, (Object)h), stackedPositions);
            }
        }
    }

    @Override
    public BlockPos getOffset(Vec3 ray, Direction face, PlacementOptions option) {
        boolean negative;
        if (option == PlacementOptions.Merged) {
            return BlockPos.ZERO;
        }
        int offset = option == PlacementOptions.Attached ? 0 : -1;
        boolean bl = negative = face.getAxisDirection() == Direction.AxisDirection.NEGATIVE;
        int yOffset = option == PlacementOptions.Attached ? (negative ? 1 : 2) : (negative ? 0 : -1);
        int r = this.param0 + 1 + offset;
        int y = (this.param1 + (this.param1 == 0 ? 0 : yOffset)) / 2;
        return BlockPos.ZERO.relative(face, (face.getAxis().isVertical() ? y : r) * (option == PlacementOptions.Attached ? 1 : -1));
    }

    @Override
    int getMax(int paramIndex) {
        return paramIndex == 0 ? 8 : 8;
    }

    @Override
    int getMin(int paramIndex) {
        return paramIndex == 0 ? 0 : 1;
    }

    @Override
    Component getParamLabel(int paramIndex) {
        return paramIndex == 0 ? CreateLang.translateDirect("generic.radius", new Object[0]) : super.getParamLabel(paramIndex);
    }

    @Override
    public List<BlockPos> getIncludedPositions() {
        return this.cachedBrushes.get(Pair.of((Object)this.param0, (Object)this.param1));
    }
}
