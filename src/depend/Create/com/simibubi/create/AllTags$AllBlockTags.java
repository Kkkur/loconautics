/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create;

import com.simibubi.create.AllTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public static enum AllTags.AllBlockTags {
    BRITTLE,
    CASING,
    COPYCAT_ALLOW,
    COPYCAT_DENY,
    FAN_PROCESSING_CATALYSTS_BLASTING(AllTags.NameSpace.MOD, "fan_processing_catalysts/blasting"),
    FAN_PROCESSING_CATALYSTS_HAUNTING(AllTags.NameSpace.MOD, "fan_processing_catalysts/haunting"),
    FAN_PROCESSING_CATALYSTS_SMOKING(AllTags.NameSpace.MOD, "fan_processing_catalysts/smoking"),
    FAN_PROCESSING_CATALYSTS_SPLASHING(AllTags.NameSpace.MOD, "fan_processing_catalysts/splashing"),
    FAN_TRANSPARENT,
    GIRDABLE_TRACKS,
    MOVABLE_EMPTY_COLLIDER,
    NON_MOVABLE,
    NON_BREAKABLE,
    PASSIVE_BOILER_HEATERS,
    SAFE_NBT,
    SEATS,
    POSTBOXES,
    TABLE_CLOTHS,
    TOOLBOXES,
    TRACKS,
    TREE_ATTACHMENTS,
    VALVE_HANDLES,
    WINDMILL_SAILS,
    WRENCH_PICKUP,
    CHEST_MOUNTED_STORAGE,
    SIMPLE_MOUNTED_STORAGE,
    FALLBACK_MOUNTED_STORAGE_BLACKLIST,
    ROOTS,
    SUGAR_CANE_VARIANTS,
    NON_HARVESTABLE,
    SINGLE_BLOCK_INVENTORIES,
    PLOUGH_WHITELIST,
    PLOUGH_BLACKLIST,
    CARDBOARD_STORAGE_BLOCKS(AllTags.NameSpace.COMMON, "storage_blocks/cardboard"),
    ANDESITE_ALLOY_STORAGE_BLOCKS(AllTags.NameSpace.COMMON, "storage_blocks/andesite_alloy"),
    CORALS,
    SLIMY_LOGS(AllTags.NameSpace.TIC),
    NON_DOUBLE_DOOR(AllTags.NameSpace.QUARK);

    public final TagKey<Block> tag;

    private AllTags.AllBlockTags() {
        this(AllTags.NameSpace.MOD);
    }

    private AllTags.AllBlockTags(AllTags.NameSpace namespace) {
        this(namespace, null);
    }

    private AllTags.AllBlockTags(AllTags.NameSpace namespace, String pathOverride) {
        this.tag = TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)namespace.id(this, pathOverride));
    }

    public boolean matches(Block block) {
        return block.builtInRegistryHolder().is(this.tag);
    }

    public boolean matches(ItemStack stack) {
        BlockItem blockItem;
        Item item;
        return stack != null && (item = stack.getItem()) instanceof BlockItem && this.matches((blockItem = (BlockItem)item).getBlock());
    }

    public boolean matches(BlockState state) {
        return state.is(this.tag);
    }
}
