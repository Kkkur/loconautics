/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.instruction.PonderInstruction
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.entity.BlockEntityType
 */
package dev.ryanhcode.offroad.content.ponder.instructions;

import dev.ryanhcode.offroad.content.ponder.instructions.ChangeBoreheadAndContraptionSpeedInstruction;
import dev.ryanhcode.offroad.index.OffroadBlockEntityTypes;
import java.util.Optional;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class StopBoreheadBearingAndContraptionInstruction
extends PonderInstruction {
    private final ChangeBoreheadAndContraptionSpeedInstruction instruction;
    private final BlockPos boreheadPos;
    private final boolean forced;

    public StopBoreheadBearingAndContraptionInstruction(BlockPos boreheadPos, ChangeBoreheadAndContraptionSpeedInstruction instruction, boolean forced) {
        this.instruction = instruction;
        this.boreheadPos = boreheadPos;
        this.forced = forced;
    }

    public boolean isComplete() {
        return true;
    }

    public void tick(PonderScene scene) {
        Optional be = scene.getWorld().getBlockEntity(this.boreheadPos, (BlockEntityType)OffroadBlockEntityTypes.BOREHEAD_BEARING.get());
        be.ifPresent(bhb -> bhb.setSpeed(0.0f));
        if (this.forced) {
            this.instruction.forcedStop = true;
        } else {
            this.instruction.startSlowing = true;
        }
    }
}
