/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package com.simibubi.create.api.contraption.transformable;

import com.simibubi.create.content.contraptions.StructureTransform;
import net.minecraft.world.level.block.entity.BlockEntity;

@FunctionalInterface
public static interface MovedBlockTransformerRegistries.BlockEntityTransformer {
    public void transform(BlockEntity var1, StructureTransform var2);
}
