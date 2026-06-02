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

import dev.simulated_team.simulated.content.blocks.physics_assembler.PhysicsAssemblerBlockEntity;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PullTheAssemblerKronkInstruction
extends PonderInstruction {
    protected final BlockPos assemblerPos;
    protected final boolean isAssembling;
    protected final boolean instantaneous;

    public PullTheAssemblerKronkInstruction(BlockPos assemblerPos, boolean isAssembling, boolean instantaneous) {
        this.assemblerPos = assemblerPos;
        this.isAssembling = isAssembling;
        this.instantaneous = instantaneous;
    }

    public boolean isComplete() {
        return true;
    }

    public void tick(PonderScene scene) {
        PonderLevel world = scene.getWorld();
        BlockEntity blockEntity = world.getBlockEntity(this.assemblerPos);
        if (blockEntity instanceof PhysicsAssemblerBlockEntity) {
            PhysicsAssemblerBlockEntity be = (PhysicsAssemblerBlockEntity)blockEntity;
            be.clientFlickLeverTo(this.isAssembling);
            if (this.instantaneous) {
                be.jerkLever();
            }
        }
    }
}
