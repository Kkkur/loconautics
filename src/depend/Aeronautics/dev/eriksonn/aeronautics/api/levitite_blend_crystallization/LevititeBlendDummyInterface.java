/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.material.FluidState
 */
package dev.eriksonn.aeronautics.api.levitite_blend_crystallization;

import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.LevititeBlendHelper;
import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.LevititeCrystallizerManager;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;

public interface LevititeBlendDummyInterface {
    default public void levititeBlendTick(Level level, BlockPos pos, FluidState state) {
        Set<BlockPos> tickedPositions = LevititeCrystallizerManager.getTickedPositions(level);
        if (tickedPositions.contains(pos)) {
            return;
        }
        LevititeBlendHelper.checkSurroundingSources(level, pos, state);
    }
}
