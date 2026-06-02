/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.track;

import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.simibubi.create.foundation.block.render.MultiPosDestructionHandler;
import com.simibubi.create.foundation.block.render.ReducedDestroyEffects;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public static class TrackBlock.RenderProperties
extends ReducedDestroyEffects
implements MultiPosDestructionHandler {
    @Override
    @Nullable
    public Set<BlockPos> getExtraPositions(ClientLevel level, BlockPos pos, BlockState blockState, int progress) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TrackBlockEntity) {
            TrackBlockEntity track = (TrackBlockEntity)blockEntity;
            return new HashSet<BlockPos>(track.connections.keySet());
        }
        return null;
    }
}
