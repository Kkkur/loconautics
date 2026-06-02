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

import dev.simulated_team.simulated.content.blocks.docking_connector.DockingConnectorBlockEntity;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ToggleConnectorLockInstruction
extends PonderInstruction {
    BlockPos pos;
    boolean lock;

    public ToggleConnectorLockInstruction(BlockPos pos, boolean lock) {
        this.pos = pos;
        this.lock = lock;
    }

    public boolean isComplete() {
        return true;
    }

    public void tick(PonderScene scene) {
        PonderLevel world = scene.getWorld();
        BlockEntity blockEntity = world.getBlockEntity(this.pos);
        if (blockEntity instanceof DockingConnectorBlockEntity) {
            DockingConnectorBlockEntity be = (DockingConnectorBlockEntity)blockEntity;
            be.setVirtualLock(this.lock);
            if (!this.lock) {
                be.tank.disconnect();
            }
        }
    }
}
