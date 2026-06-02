/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.block.Block
 */
package dev.ryanhcode.sable.index;

import dev.ryanhcode.sable.Sable;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class SableTags {
    public static final TagKey<EntityType<?>> RETAIN_IN_SUB_LEVEL = TagKey.create((ResourceKey)Registries.ENTITY_TYPE, (ResourceLocation)Sable.sablePath("retain_in_sub_level"));
    public static final TagKey<EntityType<?>> DESTROY_WITH_SUB_LEVEL = TagKey.create((ResourceKey)Registries.ENTITY_TYPE, (ResourceLocation)Sable.sablePath("destroy_with_sub_level"));
    public static final TagKey<EntityType<?>> DESTROY_WHEN_LEAVING_PLOT = TagKey.create((ResourceKey)Registries.ENTITY_TYPE, (ResourceLocation)Sable.sablePath("destroy_when_leaving_plot"));
    public static final TagKey<Block> ALWAYS_CHUNK_RENDERING = TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)Sable.sablePath("always_chunk_rendering"));
    public static final TagKey<Block> BOUNCY = TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)Sable.sablePath("bouncy"));
    public static final TagKey<Item> PADDLES = TagKey.create((ResourceKey)Registries.ITEM, (ResourceLocation)Sable.sablePath("paddles"));

    public static void register() {
    }
}
