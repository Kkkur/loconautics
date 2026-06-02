/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 */
package com.simibubi.create.content.kinetics.simpleRelays;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class SimpleKineticBlockEntity
extends KineticBlockEntity {
    public SimpleKineticBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(this.worldPosition).inflate(1.0);
    }

    @Override
    public List<BlockPos> addPropagationLocations(IRotate block, BlockState state, List<BlockPos> neighbours) {
        if (!ICogWheel.isLargeCog(state)) {
            return super.addPropagationLocations(block, state, neighbours);
        }
        BlockPos.betweenClosedStream((BlockPos)new BlockPos(-1, -1, -1), (BlockPos)new BlockPos(1, 1, 1)).forEach(offset -> {
            if (offset.distSqr((Vec3i)BlockPos.ZERO) == 2.0) {
                neighbours.add(this.worldPosition.offset((Vec3i)offset));
            }
        });
        return neighbours;
    }

    @Override
    protected boolean isNoisy() {
        return false;
    }
}
