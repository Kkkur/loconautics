package com.lycoris.loconautics.content.boiler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Boiler Body Block — the main cylindrical body of the steam boiler multiblock.
 *
 * <p>Purely structural in Phase 2: no block entity, no tick, no interaction.
 * Each body block contributes to maximum SU output and water consumption,
 * as tracked by the controller BE after the multiblock scan.
 *
 * <p>Flammable — a working steam boiler should feel dangerous if coal spills nearby.
 */
public class BoilerBodyBlock extends Block {

    public BoilerBodyBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    // ------------------------------------------------------------------ flammability

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 20;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }
}