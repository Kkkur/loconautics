/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.contraptions.behaviour;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.elevator.ElevatorContraption;
import com.simibubi.create.content.equipment.bell.AbstractBellBlock;
import com.simibubi.create.content.redstone.deskBell.DeskBellBlock;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

public class BellMovementBehaviour
implements MovementBehaviour {
    @Override
    public boolean isActive(MovementContext context) {
        return MovementBehaviour.super.isActive(context) && !(context.contraption instanceof CarriageContraption);
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    public void tick(MovementContext context) {
        Boolean b;
        Object object = context.temporaryData;
        boolean moved = object instanceof Boolean && (b = (Boolean)object) != false;
        Contraption contraption = context.contraption;
        if (contraption instanceof ElevatorContraption) {
            ElevatorContraption ec = (ElevatorContraption)contraption;
            if (!ec.arrived) {
                context.temporaryData = true;
                return;
            }
        }
        if (!moved) return;
        BellMovementBehaviour.playSound(context);
        context.temporaryData = null;
    }

    @Override
    public void onSpeedChanged(MovementContext context, Vec3 oldMotion, Vec3 motion) {
        if (context.contraption instanceof ElevatorContraption) {
            return;
        }
        double dotProduct = oldMotion.dot(motion);
        if (dotProduct <= 0.0 && context.relativeMotion.length() != 0.0 || context.firstMovement) {
            BellMovementBehaviour.playSound(context);
        }
    }

    @Override
    public void stopMoving(MovementContext context) {
        if (context.position != null && this.isActive(context)) {
            BellMovementBehaviour.playSound(context);
        }
    }

    public static void playSound(MovementContext context) {
        Level world = context.world;
        BlockPos pos = BlockPos.containing((Position)context.position);
        Block block = context.state.getBlock();
        if (AllBlocks.DESK_BELL.has(context.state)) {
            ((DeskBellBlock)block).playSound(null, (LevelAccessor)world, pos);
        } else if (block instanceof AbstractBellBlock) {
            ((AbstractBellBlock)block).playSound(world, pos);
        } else {
            world.playSound(null, pos, SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 2.0f, 1.0f);
        }
    }
}
