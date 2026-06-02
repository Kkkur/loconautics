/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 */
package com.simibubi.create.content.equipment.bell;

import com.simibubi.create.content.contraptions.behaviour.BellMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.equipment.bell.HauntedBellPulser;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class HauntedBellMovementBehaviour
extends BellMovementBehaviour {
    public static final int DISTANCE = 3;

    @Override
    public void tick(MovementContext context) {
        int recharge = this.getRecharge(context);
        if (recharge > 0) {
            this.setRecharge(context, recharge - 1);
        }
    }

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        Level level;
        if (!context.world.isClientSide && (level = context.world) instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            if (this.getRecharge(context) == 0) {
                HauntedBellPulser.sendPulse(serverLevel, pos, 3, false);
                this.setRecharge(context, 65);
                HauntedBellMovementBehaviour.playSound(context);
            }
        }
    }

    @Override
    public void writeExtraData(MovementContext context) {
        context.blockEntityData.putInt("Recharge", this.getRecharge(context));
    }

    private int getRecharge(MovementContext context) {
        if (!(context.temporaryData instanceof Integer) && context.world != null) {
            context.temporaryData = context.blockEntityData.getInt("Recharge");
        }
        return (Integer)context.temporaryData;
    }

    private void setRecharge(MovementContext context, int value) {
        context.temporaryData = value;
    }
}
