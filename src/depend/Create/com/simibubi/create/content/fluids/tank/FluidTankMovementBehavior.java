/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package com.simibubi.create.content.fluids.tank;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FluidTankMovementBehavior
implements MovementBehaviour {
    @Override
    public boolean mustTickWhileDisabled() {
        return true;
    }

    @Override
    public void tick(MovementContext context) {
        BlockEntity be;
        if (context.world.isClientSide && (be = context.contraption.getBlockEntityClientSide(context.localPos)) instanceof FluidTankBlockEntity) {
            FluidTankBlockEntity tank = (FluidTankBlockEntity)be;
            tank.getFluidLevel().tickChaser();
        }
    }
}
