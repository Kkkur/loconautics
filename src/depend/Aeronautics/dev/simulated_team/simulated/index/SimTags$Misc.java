/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.saveddata.maps.MapDecorationType
 */
package dev.simulated_team.simulated.index;

import dev.simulated_team.simulated.Simulated;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;

public static class SimTags.Misc {
    public static final TagKey<MapDecorationType> NAV_TABLE_FINDABLE = TagKey.create((ResourceKey)Registries.MAP_DECORATION_TYPE, (ResourceLocation)Simulated.path("nav_table_findable"));
    public static final TagKey<EntityType<?>> ARMOR_STAND_IGNORE = TagKey.create((ResourceKey)Registries.ENTITY_TYPE, (ResourceLocation)Simulated.path("armor_stand_ignore"));
    public static final TagKey<EntityType<?>> LASER_BLACKLIST = TagKey.create((ResourceKey)Registries.ENTITY_TYPE, (ResourceLocation)Simulated.path("laser_entity_blacklist"));
}
