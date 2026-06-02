/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.equipment.zapper.terrainzapper;

import com.simibubi.create.content.equipment.zapper.terrainzapper.PlacementOptions;
import com.simibubi.create.content.equipment.zapper.terrainzapper.ShapedBrush;
import com.simibubi.create.foundation.utility.CreateLang;
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

public class SphereBrush
extends ShapedBrush {
    public static final int MAX_RADIUS = 10;
    private Map<Integer, List<BlockPos>> cachedBrushes = new HashMap<Integer, List<BlockPos>>();

    public SphereBrush() {
        super(1);
        for (int i = 0; i <= 10; ++i) {
            int radius = i;
            List positions = BlockPos.betweenClosedStream((BlockPos)BlockPos.ZERO.offset(-i - 1, -i - 1, -i - 1), (BlockPos)BlockPos.ZERO.offset(i + 1, i + 1, i + 1)).map(BlockPos::new).filter(p -> VecHelper.getCenterOf((Vec3i)p).distanceTo(VecHelper.getCenterOf((Vec3i)BlockPos.ZERO)) < (double)((float)radius + 0.5f)).collect(Collectors.toList());
            this.cachedBrushes.put(i, positions);
        }
    }

    @Override
    public BlockPos getOffset(Vec3 ray, Direction face, PlacementOptions option) {
        if (option == PlacementOptions.Merged) {
            return BlockPos.ZERO;
        }
        int offset = option == PlacementOptions.Attached ? 0 : -1;
        int r = this.param0 + 1 + offset;
        return BlockPos.ZERO.relative(face, r * (option == PlacementOptions.Attached ? 1 : -1));
    }

    @Override
    int getMax(int paramIndex) {
        return 10;
    }

    @Override
    Component getParamLabel(int paramIndex) {
        return CreateLang.translateDirect("generic.radius", new Object[0]);
    }

    @Override
    List<BlockPos> getIncludedPositions() {
        return this.cachedBrushes.get(this.param0);
    }
}
