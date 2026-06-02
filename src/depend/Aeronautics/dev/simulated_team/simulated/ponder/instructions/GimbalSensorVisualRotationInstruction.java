/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.ponder.api.level.PonderLevel
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.instruction.PonderInstruction
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.ponder.instructions;

import dev.simulated_team.simulated.content.blocks.gimbal_sensor.GimbalSensorBlockEntity;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class GimbalSensorVisualRotationInstruction
extends PonderInstruction {
    BlockPos location;
    Boolean unlocked;

    public GimbalSensorVisualRotationInstruction(BlockPos location, Boolean unlocked) {
        this.location = location;
        this.unlocked = unlocked;
    }

    public boolean isComplete() {
        return false;
    }

    public void tick(PonderScene scene) {
        PonderLevel world = scene.getWorld();
        BlockEntity blockEntity = world.getBlockEntity(this.location);
        if (blockEntity instanceof GimbalSensorBlockEntity) {
            GimbalSensorBlockEntity be = (GimbalSensorBlockEntity)blockEntity;
            be.updateVisualRotation = this.unlocked;
        }
    }
}
