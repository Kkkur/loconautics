/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.decoration.palettes;

import com.simibubi.create.content.decoration.palettes.GlassPaneBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ConnectedGlassPaneBlock
extends GlassPaneBlock {
    public ConnectedGlassPaneBlock(BlockBehaviour.Properties builder) {
        super(builder);
    }

    @OnlyIn(value=Dist.CLIENT)
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        if (side.getAxis().isVertical()) {
            return adjacentBlockState == state;
        }
        return super.skipRendering(state, adjacentBlockState, side);
    }
}
