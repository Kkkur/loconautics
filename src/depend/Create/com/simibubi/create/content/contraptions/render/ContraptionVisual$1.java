/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.contraptions.render;

import com.simibubi.create.content.contraptions.render.ClientContraption;
import com.simibubi.create.content.contraptions.render.ContraptionVisual;
import com.simibubi.create.foundation.utility.worldWrappers.WrappedBlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

class ContraptionVisual.1
extends WrappedBlockAndTintGetter {
    final /* synthetic */ ClientContraption.RenderedBlocks val$blocks;

    ContraptionVisual.1(ContraptionVisual this$0, BlockAndTintGetter wrapped, ClientContraption.RenderedBlocks renderedBlocks) {
        this.val$blocks = renderedBlocks;
        super(wrapped);
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return this.val$blocks.lookup().apply(pos);
    }
}
