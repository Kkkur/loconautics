/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.kinetics.belt;

import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.foundation.block.render.MultiPosDestructionHandler;
import com.simibubi.create.foundation.block.render.ReducedDestroyEffects;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public static class BeltBlock.RenderProperties
extends ReducedDestroyEffects
implements MultiPosDestructionHandler {
    @Override
    public Set<BlockPos> getExtraPositions(ClientLevel level, BlockPos pos, BlockState blockState, int progress) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof BeltBlockEntity) {
            BeltBlockEntity belt = (BeltBlockEntity)blockEntity;
            return new HashSet<BlockPos>(BeltBlock.getBeltChain((LevelAccessor)level, belt.getController()));
        }
        return null;
    }
}
