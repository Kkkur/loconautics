/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 */
package com.simibubi.create.api.schematic.requirement;

import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import net.minecraft.world.entity.Entity;

@FunctionalInterface
public static interface SchematicRequirementRegistries.EntityRequirement {
    public ItemRequirement getRequiredItems(Entity var1);
}
