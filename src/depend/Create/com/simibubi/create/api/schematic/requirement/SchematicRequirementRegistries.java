/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.api.schematic.requirement;

import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SchematicRequirementRegistries {
    public static final SimpleRegistry<Block, BlockRequirement> BLOCKS = SimpleRegistry.create();
    public static final SimpleRegistry<BlockEntityType<?>, BlockEntityRequirement> BLOCK_ENTITIES = SimpleRegistry.create();
    public static final SimpleRegistry<EntityType<?>, EntityRequirement> ENTITIES = SimpleRegistry.create();

    private SchematicRequirementRegistries() {
        throw new AssertionError((Object)"This class should not be instantiated");
    }

    @FunctionalInterface
    public static interface EntityRequirement {
        public ItemRequirement getRequiredItems(Entity var1);
    }

    @FunctionalInterface
    public static interface BlockEntityRequirement {
        public ItemRequirement getRequiredItems(BlockEntity var1, BlockState var2);
    }

    @FunctionalInterface
    public static interface BlockRequirement {
        public ItemRequirement getRequiredItems(BlockState var1, @Nullable BlockEntity var2);
    }
}
