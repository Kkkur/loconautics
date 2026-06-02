/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.util.nullness.NonNullSupplier
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.material.MapColor
 */
package com.simibubi.create.content.decoration.palettes;

import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.content.decoration.palettes.PaletteBlockPattern;
import com.simibubi.create.content.decoration.palettes.PalettesVariantEntry;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import java.util.function.Function;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;

public enum AllPaletteStoneTypes {
    GRANITE(PaletteBlockPattern.VANILLA_RANGE, r -> () -> Blocks.GRANITE),
    DIORITE(PaletteBlockPattern.VANILLA_RANGE, r -> () -> Blocks.DIORITE),
    ANDESITE(PaletteBlockPattern.VANILLA_RANGE, r -> () -> Blocks.ANDESITE),
    CALCITE(PaletteBlockPattern.VANILLA_RANGE, r -> () -> Blocks.CALCITE),
    DRIPSTONE(PaletteBlockPattern.VANILLA_RANGE, r -> () -> Blocks.DRIPSTONE_BLOCK),
    DEEPSLATE(PaletteBlockPattern.VANILLA_RANGE, r -> () -> Blocks.DEEPSLATE),
    TUFF(PaletteBlockPattern.VANILLA_RANGE, r -> () -> Blocks.TUFF),
    ASURINE(PaletteBlockPattern.STANDARD_RANGE, r -> r.paletteStoneBlock("asurine", (NonNullSupplier<Block>)((NonNullSupplier)() -> Blocks.DEEPSLATE), true, true).properties(p -> p.destroyTime(1.25f).mapColor(MapColor.COLOR_BLUE)).register()),
    CRIMSITE(PaletteBlockPattern.STANDARD_RANGE, r -> r.paletteStoneBlock("crimsite", (NonNullSupplier<Block>)((NonNullSupplier)() -> Blocks.DEEPSLATE), true, true).properties(p -> p.destroyTime(1.25f).mapColor(MapColor.COLOR_RED)).register()),
    LIMESTONE(PaletteBlockPattern.STANDARD_RANGE, r -> r.paletteStoneBlock("limestone", (NonNullSupplier<Block>)((NonNullSupplier)() -> Blocks.SANDSTONE), true, false).properties(p -> p.destroyTime(1.25f).mapColor(MapColor.SAND)).register()),
    OCHRUM(PaletteBlockPattern.STANDARD_RANGE, r -> r.paletteStoneBlock("ochrum", (NonNullSupplier<Block>)((NonNullSupplier)() -> Blocks.CALCITE), true, true).properties(p -> p.destroyTime(1.25f).mapColor(MapColor.TERRACOTTA_YELLOW)).register()),
    SCORIA(PaletteBlockPattern.STANDARD_RANGE, r -> r.paletteStoneBlock("scoria", (NonNullSupplier<Block>)((NonNullSupplier)() -> Blocks.BLACKSTONE), true, false).properties(p -> p.mapColor(MapColor.COLOR_BROWN)).register()),
    SCORCHIA(PaletteBlockPattern.STANDARD_RANGE, r -> r.paletteStoneBlock("scorchia", (NonNullSupplier<Block>)((NonNullSupplier)() -> Blocks.BLACKSTONE), true, false).properties(p -> p.mapColor(MapColor.TERRACOTTA_GRAY)).register()),
    VERIDIUM(PaletteBlockPattern.STANDARD_RANGE, r -> r.paletteStoneBlock("veridium", (NonNullSupplier<Block>)((NonNullSupplier)() -> Blocks.TUFF), true, true).properties(p -> p.destroyTime(1.25f).mapColor(MapColor.WARPED_NYLIUM)).register());

    private Function<CreateRegistrate, NonNullSupplier<Block>> factory;
    private PalettesVariantEntry variants;
    public NonNullSupplier<Block> baseBlock;
    public PaletteBlockPattern[] variantTypes;
    public TagKey<Item> materialTag;

    private AllPaletteStoneTypes(PaletteBlockPattern[] variantTypes, Function<CreateRegistrate, NonNullSupplier<Block>> factory) {
        this.factory = factory;
        this.variantTypes = variantTypes;
    }

    public NonNullSupplier<Block> getBaseBlock() {
        return this.baseBlock;
    }

    public PalettesVariantEntry getVariants() {
        return this.variants;
    }

    public static void register(CreateRegistrate registrate) {
        for (AllPaletteStoneTypes paletteStoneVariants : AllPaletteStoneTypes.values()) {
            paletteStoneVariants.baseBlock = paletteStoneVariants.factory.apply(registrate);
            String id = Lang.asId((String)paletteStoneVariants.name());
            paletteStoneVariants.materialTag = AllTags.optionalTag(BuiltInRegistries.ITEM, Create.asResource("stone_types/" + id));
            paletteStoneVariants.variants = new PalettesVariantEntry(id, paletteStoneVariants);
        }
    }
}
