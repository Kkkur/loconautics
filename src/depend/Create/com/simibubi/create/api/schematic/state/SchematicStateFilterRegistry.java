/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.api.schematic.state;

import com.simibubi.create.api.registry.SimpleRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SchematicStateFilterRegistry {
    public static final SimpleRegistry<Block, StateFilter> REGISTRY = SimpleRegistry.create();

    private SchematicStateFilterRegistry() {
        throw new AssertionError((Object)"This class should not be instantiated");
    }

    @FunctionalInterface
    public static interface StateFilter {
        public BlockState filterStates(@Nullable BlockEntity var1, BlockState var2);
    }
}
