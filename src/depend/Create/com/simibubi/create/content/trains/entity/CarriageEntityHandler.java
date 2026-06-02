/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.neoforged.neoforge.event.entity.EntityEvent$EnteringSection
 */
package com.simibubi.create.content.trains.entity;

import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.EntityEvent;

public class CarriageEntityHandler {
    public static void onEntityEnterSection(EntityEvent.EnteringSection event) {
        if (!event.didChunkChange()) {
            return;
        }
        Entity entity = event.getEntity();
        if (!(entity instanceof CarriageContraptionEntity)) {
            return;
        }
        CarriageContraptionEntity cce = (CarriageContraptionEntity)entity;
        SectionPos newPos = event.getNewPos();
        Level level = entity.level();
        if (level.isClientSide) {
            return;
        }
        if (!CarriageEntityHandler.isActiveChunk(level, newPos.center())) {
            cce.leftTickingChunks = true;
        }
    }

    public static void validateCarriageEntity(CarriageContraptionEntity entity) {
        if (!entity.isAlive()) {
            return;
        }
        Level level = entity.level();
        if (level.isClientSide) {
            return;
        }
        if (!CarriageEntityHandler.isActiveChunk(level, entity.blockPosition())) {
            entity.leftTickingChunks = true;
        }
    }

    public static boolean isActiveChunk(Level level, BlockPos pos) {
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            return serverLevel.isPositionEntityTicking(pos);
        }
        return false;
    }
}
