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

public class LinkDockingConnectorsInstruction
extends PonderInstruction {
    final BlockPos fromPos;
    final BlockPos toPos;

    public LinkDockingConnectorsInstruction(BlockPos fromPos, BlockPos toPos) {
        this.fromPos = fromPos;
        this.toPos = toPos;
    }

    public boolean isComplete() {
        return true;
    }

    public void tick(PonderScene scene) {
        PonderLevel world = scene.getWorld();
        BlockEntity blockEntity = world.getBlockEntity(this.fromPos);
        if (blockEntity instanceof DockingConnectorBlockEntity) {
            DockingConnectorBlockEntity be1 = (DockingConnectorBlockEntity)blockEntity;
            blockEntity = world.getBlockEntity(this.toPos);
            if (blockEntity instanceof DockingConnectorBlockEntity) {
                DockingConnectorBlockEntity be2 = (DockingConnectorBlockEntity)blockEntity;
                be1.tank.connect(this.toPos, be2.tank);
            }
        }
    }
}
