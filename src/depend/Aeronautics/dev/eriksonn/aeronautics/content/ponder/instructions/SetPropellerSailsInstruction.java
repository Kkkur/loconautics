/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.instruction.PonderInstruction
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.eriksonn.aeronautics.content.ponder.instructions;

import dev.eriksonn.aeronautics.content.blocks.propeller.bearing.propeller_bearing.PropellerBearingBlockEntity;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SetPropellerSailsInstruction
extends PonderInstruction {
    BlockPos pos;
    float sails;

    public SetPropellerSailsInstruction(BlockPos pos, float sails) {
        this.pos = pos;
        this.sails = sails;
    }

    public boolean isComplete() {
        return true;
    }

    public void tick(PonderScene scene) {
        BlockEntity blockEntity = scene.getWorld().getBlockEntity(this.pos);
        if (blockEntity instanceof PropellerBearingBlockEntity) {
            PropellerBearingBlockEntity propeller = (PropellerBearingBlockEntity)blockEntity;
            propeller.totalSailPower = this.sails;
        }
    }
}
