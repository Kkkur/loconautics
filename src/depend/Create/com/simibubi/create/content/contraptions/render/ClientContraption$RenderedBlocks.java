/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.contraptions.render;

import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public record ClientContraption.RenderedBlocks(Function<BlockPos, BlockState> lookup, Iterable<BlockPos> positions) {
}
