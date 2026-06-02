/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.ChiseledBookShelfBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create;

import com.simibubi.create.api.schematic.state.SchematicStateFilterRegistry;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class AllSchematicStateFilters {
    public static void registerDefaults() {
        SchematicStateFilterRegistry.REGISTRY.register(Blocks.CHISELED_BOOKSHELF, (blockEntity, state) -> {
            for (BooleanProperty p : ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES) {
                state = (BlockState)state.setValue((Property)p, (Comparable)Boolean.valueOf(false));
            }
            return state;
        });
    }
}
