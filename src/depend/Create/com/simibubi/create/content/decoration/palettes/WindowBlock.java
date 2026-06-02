/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.decoration.palettes;

import com.simibubi.create.content.decoration.palettes.ConnectedGlassBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class WindowBlock
extends ConnectedGlassBlock {
    protected final boolean translucent;

    public WindowBlock(BlockBehaviour.Properties p_i48392_1_, boolean translucent) {
        super(p_i48392_1_);
        this.translucent = translucent;
    }

    public boolean isTranslucent() {
        return this.translucent;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        if (state.getBlock() == adjacentBlockState.getBlock()) {
            return true;
        }
        Block block = state.getBlock();
        if (block instanceof WindowBlock) {
            WindowBlock windowBlock = (WindowBlock)block;
            if (adjacentBlockState.getBlock() instanceof ConnectedGlassBlock) {
                return !windowBlock.isTranslucent() && side.getAxis().isHorizontal();
            }
        }
        return super.skipRendering(state, adjacentBlockState, side);
    }
}
