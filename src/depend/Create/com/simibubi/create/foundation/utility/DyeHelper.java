/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 */
package com.simibubi.create.foundation.utility;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.createmod.catnip.data.Couple;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class DyeHelper {
    private static final Map<DyeColor, Supplier<ItemLike>> WOOL_TABLE = new HashMap<DyeColor, Supplier<ItemLike>>();
    private static final Map<DyeColor, Couple<Integer>> DYE_TABLE = new HashMap<DyeColor, Couple<Integer>>();

    public static ItemLike getWoolOfDye(DyeColor color) {
        return WOOL_TABLE.getOrDefault(color, () -> Blocks.WHITE_WOOL).get();
    }

    public static Couple<Integer> getDyeColors(DyeColor color) {
        return DYE_TABLE.getOrDefault(color, DYE_TABLE.get(DyeColor.WHITE));
    }

    public static void addDye(DyeColor color, Integer brightColor, Integer darkColor, Supplier<ItemLike> wool) {
        DYE_TABLE.put(color, (Couple<Integer>)Couple.create((Object)brightColor, (Object)darkColor));
        WOOL_TABLE.put(color, wool);
    }

    private static void addDye(DyeColor color, Integer brightColor, Integer darkColor, ItemLike wool) {
        DyeHelper.addDye(color, brightColor, darkColor, () -> wool);
    }

    static {
        DyeHelper.addDye(DyeColor.BLACK, (Integer)4538427, (Integer)2170911, (ItemLike)Blocks.BLACK_WOOL);
        DyeHelper.addDye(DyeColor.RED, (Integer)11614519, (Integer)6498103, (ItemLike)Blocks.RED_WOOL);
        DyeHelper.addDye(DyeColor.GREEN, (Integer)2132550, (Integer)1925189, (ItemLike)Blocks.GREEN_WOOL);
        DyeHelper.addDye(DyeColor.BROWN, (Integer)11306332, (Integer)6837054, (ItemLike)Blocks.BROWN_WOOL);
        DyeHelper.addDye(DyeColor.BLUE, (Integer)5476833, (Integer)5262224, (ItemLike)Blocks.BLUE_WOOL);
        DyeHelper.addDye(DyeColor.GRAY, (Integer)6121071, (Integer)3224888, (ItemLike)Blocks.GRAY_WOOL);
        DyeHelper.addDye(DyeColor.LIGHT_GRAY, (Integer)9803419, (Integer)0x707070, (ItemLike)Blocks.LIGHT_GRAY_WOOL);
        DyeHelper.addDye(DyeColor.PURPLE, (Integer)10441902, (Integer)0x63366C, (ItemLike)Blocks.PURPLE_WOOL);
        DyeHelper.addDye(DyeColor.CYAN, (Integer)4107188, (Integer)3962994, (ItemLike)Blocks.CYAN_WOOL);
        DyeHelper.addDye(DyeColor.PINK, (Integer)14002379, (Integer)12086165, (ItemLike)Blocks.PINK_WOOL);
        DyeHelper.addDye(DyeColor.LIME, (Integer)10739541, (Integer)5222767, (ItemLike)Blocks.LIME_WOOL);
        DyeHelper.addDye(DyeColor.YELLOW, (Integer)15128406, (Integer)15313961, (ItemLike)Blocks.YELLOW_WOOL);
        DyeHelper.addDye(DyeColor.LIGHT_BLUE, (Integer)6934226, (Integer)5278373, (ItemLike)Blocks.LIGHT_BLUE_WOOL);
        DyeHelper.addDye(DyeColor.ORANGE, (Integer)15635014, (Integer)14240039, (ItemLike)Blocks.ORANGE_WOOL);
        DyeHelper.addDye(DyeColor.MAGENTA, (Integer)15753904, (Integer)12600456, (ItemLike)Blocks.MAGENTA_WOOL);
        DyeHelper.addDye(DyeColor.WHITE, (Integer)15592165, (Integer)0xBBB6B0, (ItemLike)Blocks.WHITE_WOOL);
    }
}
