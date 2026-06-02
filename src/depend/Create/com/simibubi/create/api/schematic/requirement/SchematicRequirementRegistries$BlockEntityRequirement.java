/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.api.schematic.requirement;

import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public static interface SchematicRequirementRegistries.BlockEntityRequirement {
    public ItemRequirement getRequiredItems(BlockEntity var1, BlockState var2);
}
