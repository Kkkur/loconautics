/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.api.schematic.requirement;

import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import net.minecraft.world.level.block.state.BlockState;

public interface SpecialBlockEntityItemRequirement {
    public ItemRequirement getRequiredItems(BlockState var1);
}
