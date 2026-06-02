/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.Util
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.block.Block
 */
package com.simibubi.create.foundation.data.recipe;

import com.simibubi.create.foundation.data.recipe.Mods;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import net.createmod.catnip.lang.Lang;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public enum CommonMetal {
    IRON(Mods.VANILLA),
    GOLD(Mods.VANILLA),
    COPPER(Mods.VANILLA),
    ZINC(Mods.CREATE),
    BRASS(false, Mods.CREATE),
    ALUMINUM(Mods.IE, Mods.IC2),
    LEAD(Mods.MEK, Mods.TH, Mods.IE, Mods.OREGANIZED),
    NICKEL(Mods.TH, Mods.IE),
    OSMIUM(Mods.MEK),
    PLATINUM(new Mods[0]),
    QUICKSILVER(new Mods[0]),
    SILVER(Mods.TH, Mods.IE, Mods.IC2, Mods.OREGANIZED, Mods.GS, Mods.IF),
    TIN(Mods.TH, Mods.MEK, Mods.IC2),
    URANIUM(Mods.MEK, Mods.IE, Mods.IC2),
    CONSTANTAN(false, Mods.IE),
    ELECTRUM(false, Mods.IE),
    STEEL(false, Mods.IE);

    private static final Map<Mods, Set<CommonMetal>> metalsOfMods;
    public final String name = Lang.asId((String)this.name());
    public final Set<Mods> mods;
    public final boolean isNatural;
    public final ItemLikeTag ores;
    public final TagKey<Item> rawOres;
    public final ItemLikeTag rawStorageBlocks;
    public final TagKey<Item> ingots;
    public final ItemLikeTag storageBlocks;
    public final TagKey<Item> nuggets;
    public final TagKey<Item> plates;

    private CommonMetal(Mods ... mods) {
        this(true, mods);
    }

    private CommonMetal(boolean natural, Mods ... mods) {
        this.mods = mods.length == 0 ? Set.of() : Collections.unmodifiableSet(EnumSet.copyOf(Set.of(mods)));
        this.isNatural = natural;
        this.ores = new ItemLikeTag("ores/" + this.name);
        this.rawOres = CommonMetal.itemTag("raw_materials/" + this.name);
        this.rawStorageBlocks = new ItemLikeTag("storage_blocks/raw_" + this.name);
        this.ingots = CommonMetal.itemTag("ingots/" + this.name);
        this.storageBlocks = new ItemLikeTag("storage_blocks/" + this.name);
        this.nuggets = CommonMetal.itemTag("nuggets/" + this.name);
        this.plates = CommonMetal.itemTag("plates/" + this.name);
    }

    public String getName(Mods mod) {
        if (this == ALUMINUM && mod == Mods.IC2) {
            return "aluminium";
        }
        return this.name;
    }

    public String toString() {
        return this.name;
    }

    public static Set<CommonMetal> of(Mods mod) {
        return metalsOfMods.get(mod);
    }

    private static TagKey<Item> itemTag(String path) {
        return TagKey.create((ResourceKey)Registries.ITEM, (ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)"c", (String)path));
    }

    private static TagKey<Block> blockTag(String path) {
        return TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)"c", (String)path));
    }

    static {
        metalsOfMods = (Map)Util.make(() -> {
            EnumMap<Mods, EnumSet<CommonMetal>> map = new EnumMap<Mods, EnumSet<CommonMetal>>(Mods.class);
            for (Mods mod : Mods.values()) {
                EnumSet<CommonMetal> set = EnumSet.noneOf(CommonMetal.class);
                for (CommonMetal metal : CommonMetal.values()) {
                    if (!metal.mods.contains(mod)) continue;
                    set.add(metal);
                }
                map.put(mod, set);
            }
            return map;
        });
    }

    public record ItemLikeTag(TagKey<Item> items, TagKey<Block> blocks) {
        private ItemLikeTag(String path) {
            this(CommonMetal.itemTag(path), CommonMetal.blockTag(path));
        }
    }
}
