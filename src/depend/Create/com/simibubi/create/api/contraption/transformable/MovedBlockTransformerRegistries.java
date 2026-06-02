/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.api.contraption.transformable;

import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.content.contraptions.StructureTransform;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MovedBlockTransformerRegistries {
    public static final SimpleRegistry<Block, BlockTransformer> BLOCK_TRANSFORMERS = SimpleRegistry.create();
    public static final SimpleRegistry<BlockEntityType<?>, BlockEntityTransformer> BLOCK_ENTITY_TRANSFORMERS = SimpleRegistry.create();

    private MovedBlockTransformerRegistries() {
        throw new AssertionError((Object)"This class should not be instantiated");
    }

    @FunctionalInterface
    public static interface BlockEntityTransformer {
        public void transform(BlockEntity var1, StructureTransform var2);
    }

    @FunctionalInterface
    public static interface BlockTransformer {
        public BlockState transform(BlockState var1, StructureTransform var2);
    }
}
