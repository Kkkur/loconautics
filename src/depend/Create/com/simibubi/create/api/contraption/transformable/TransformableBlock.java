/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.api.contraption.transformable;

import com.simibubi.create.content.contraptions.StructureTransform;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface TransformableBlock {
    public BlockState transform(BlockState var1, StructureTransform var2);
}
