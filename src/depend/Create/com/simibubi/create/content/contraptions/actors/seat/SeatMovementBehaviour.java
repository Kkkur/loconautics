/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.block.SlabBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.block.state.properties.SlabType
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.contraptions.actors.seat;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import java.util.Map;
import java.util.UUID;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.Vec3;

public class SeatMovementBehaviour
implements MovementBehaviour {
    @Override
    public void startMoving(MovementContext context) {
        MovementBehaviour.super.startMoving(context);
        int indexOf = context.contraption.getSeats().indexOf(context.localPos);
        context.data.putInt("SeatIndex", indexOf);
    }

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        boolean solid;
        MovementBehaviour.super.visitNewPosition(context, pos);
        AbstractContraptionEntity contraptionEntity = context.contraption.entity;
        if (contraptionEntity == null) {
            return;
        }
        int index = context.data.getInt("SeatIndex");
        if (index == -1) {
            return;
        }
        Map<UUID, Integer> seatMapping = context.contraption.getSeatMapping();
        BlockState blockState = context.world.getBlockState(pos);
        boolean slab = blockState.getBlock() instanceof SlabBlock && blockState.getValue((Property)SlabBlock.TYPE) == SlabType.BOTTOM;
        boolean bl = solid = blockState.canOcclude() || slab;
        if (!seatMapping.containsValue(index)) {
            return;
        }
        if (!solid) {
            return;
        }
        Entity toDismount = null;
        for (Map.Entry<UUID, Integer> entry : seatMapping.entrySet()) {
            if (entry.getValue() != index) continue;
            for (Entity entity : contraptionEntity.getPassengers()) {
                if (!entry.getKey().equals(entity.getUUID())) continue;
                toDismount = entity;
            }
        }
        if (toDismount == null) {
            return;
        }
        toDismount.stopRiding();
        Vec3 position = VecHelper.getCenterOf((Vec3i)pos).add(0.0, slab ? 0.5 : 1.0, 0.0);
        toDismount.teleportTo(position.x, position.y, position.z);
        toDismount.getPersistentData().remove("ContraptionDismountLocation");
    }
}
