/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.simulated_team.simulated.api.BearingSlowdownController
 *  dev.simulated_team.simulated.api.BearingSlowdownController$ContraptionSymmetry
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.eriksonn.aeronautics.content.ponder.instructions;

import dev.eriksonn.aeronautics.content.blocks.propeller.bearing.propeller_bearing.PropellerBearingBlockEntity;
import dev.eriksonn.aeronautics.content.ponder.instructions.ChangePropellerRotateInstruction;
import dev.eriksonn.aeronautics.content.ponder.instructions.PropellerRotateInstruction;
import dev.simulated_team.simulated.api.BearingSlowdownController;
import net.minecraft.world.level.block.entity.BlockEntity;

public static class ChangePropellerRotateInstruction.StopRotation
extends ChangePropellerRotateInstruction {
    public ChangePropellerRotateInstruction.StopRotation(PropellerRotateInstruction instruction, float duration) {
        super(instruction, (i, s) -> {
            BlockEntity patt0$temp = s.getWorld().getBlockEntity(i.pos);
            if (patt0$temp instanceof PropellerBearingBlockEntity) {
                PropellerBearingBlockEntity bearing = (PropellerBearingBlockEntity)patt0$temp;
                float angle = bearing.getInterpolatedAngle(0.0f);
                i.slowdownController = new BearingSlowdownController();
                i.slowdownController.generate(duration, angle, i.currentSpeed, BearingSlowdownController.ContraptionSymmetry.QUARTER);
            }
        });
    }
}
