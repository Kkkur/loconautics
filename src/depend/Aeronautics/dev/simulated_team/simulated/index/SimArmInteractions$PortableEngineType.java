/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint
 *  com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.index;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import dev.simulated_team.simulated.index.SimArmInteractions;
import dev.simulated_team.simulated.index.SimBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public static class SimArmInteractions.PortableEngineType
extends ArmInteractionPointType {
    public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
        return SimBlocks.PORTABLE_ENGINES.contains(state.getBlock());
    }

    @Nullable
    public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
        return new SimArmInteractions.PortableEngineInteractionPoint(this, level, pos, state);
    }
}
